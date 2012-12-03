package pl.kbaranski.ldappasschange.utils;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import pl.kbaranski.ldappasschange.utils.PasswordHashUtil.Algorithm;
import pl.kbaranski.ldappasschange.utils.ldaputil.LdapConnectException;
import pl.kbaranski.ldappasschange.utils.ldaputil.LdapException;
import pl.kbaranski.ldappasschange.utils.ldaputil.LdapNonUniqueUidException;

public class LdapUtil {

    LdapConfigHandler ldapConfigHandler;

    public LdapConfigHandler getLdapConfigHandler() {
        return ldapConfigHandler;
    }

    public void setLdapConfigHandler(LdapConfigHandler ldapConfigHandler) {
        this.ldapConfigHandler = ldapConfigHandler;
    }

    public DirContext getConnection(String url, String username, String password) throws LdapException {
        Hashtable<String, String> environment = new Hashtable<String, String>();
        environment.put(Context.SECURITY_PRINCIPAL, username);
        environment.put(Context.SECURITY_CREDENTIALS, password);
        DirContext initial = null;
        try {
            initial = new InitialDirContext(environment);
        } catch (NamingException e) {
            throw new LdapException(e);
        }
        DirContext context;
        try {
            context = (DirContext) initial.lookup(url);
        } catch (NamingException e) {
            throw new LdapConnectException(e);
        }
        return context;
    }

    public String getDnByUid(String uid) throws LdapException {
        if (!uid.matches("^[a-zA-Z0-9]+$")) {
            throw new RuntimeException("Nieprawidłowy uid do wyszukania: " + uid);
        }
        String url = ldapConfigHandler.getUrl();
        String username = ldapConfigHandler.getDefaultUser();
        String password = ldapConfigHandler.getDefaultPass();

        // FIXME: Dodać do filtra (objectClass=posixAccount)
        String query = "(&(objectClass=inetOrgPerson)(uid=" + uid + "))";
        final String dnAttribName = "dn";
        String[] returningAttributes = { dnAttribName };
        String baseDn = ldapConfigHandler.getUserSearchBaseDn();
        DirContext context = getConnection(url, username, password);

        List<String> ret = new ArrayList<String>();

        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctrl.setReturningAttributes(returningAttributes);
        try {
            NamingEnumeration<SearchResult> enumeration = context.search(baseDn, query, ctrl);
            while (enumeration.hasMore()) {
                SearchResult result = enumeration.next();
                ret.add(result.getNameInNamespace());
            }
        } catch (NamingException e) {
            throw new LdapException(e);
        }
        if (ret.size() == 1) {
            return ret.get(0);
        } else if (ret.size() > 1) {
            throw new LdapNonUniqueUidException();
        }
        return null;
    }

    public void updatePassword(String usernameDn, String password, String newPass) throws LdapException {
        String url = ldapConfigHandler.getUrl();
        DirContext context = getConnection(url, usernameDn, password);
        ModificationItem[] mods = new ModificationItem[1];
        Attribute mod0;
        try {
            mod0 = new BasicAttribute("userpassword", PasswordHashUtil.getHash(Algorithm.SHA, newPass));
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
        } catch (NoSuchAlgorithmException e) {
            throw new LdapException(e);
        } catch (IllegalArgumentException e) {
            throw new LdapException(e);
        }

        try {
            context.modifyAttributes(usernameDn, mods);
        } catch (NamingException e) {
            throw new LdapException(e);
        }
    }

    public void testConfig() {
        System.err.println();
        System.err.println(ldapConfigHandler.toString());
        System.err.println();
    }
}
