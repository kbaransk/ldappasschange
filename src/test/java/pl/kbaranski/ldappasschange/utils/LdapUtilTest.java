package pl.kbaranski.ldappasschange.utils;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.kbaranski.ldappasschange.utils.ldaputil.LdapConnectException;
import pl.kbaranski.ldappasschange.utils.ldaputil.LdapException;

public class LdapUtilTest extends AbstractLdapTestUnit {

    LdapServerInstance ldapInstance;
    private LdapUtil ldapUtil;

    public LdapUtilTest() {
        ldapUtil = new LdapUtil();
        LdapConfigHandler ldapConfigHandler = new LdapConfigHandler();
        ldapConfigHandler.setUrl("ldap://localhost:10389");
        ldapConfigHandler.setUserSearchBaseDn("dc=example,dc=com");
        ldapConfigHandler.setDefaultUser("uid=admin,ou=system");
        ldapConfigHandler.setDefaultPass("secret");
        ldapUtil.setLdapConfigHandler(ldapConfigHandler);
    }

    @Before
    public void before() throws Exception {
        File workDir = new File(System.getProperty("java.io.tmpdir") + "/server-work");

        // Remove old configuration to have clean directory
        if (workDir.exists()) {
            FileUtils.deleteDirectory(workDir);
        }
        // Prepare directory for LDAP files
        workDir.mkdirs();

        // Create the server
        ldapInstance = new LdapServerInstance(workDir);

        ldapInstance.addPartition("examplecom", "dc=example,dc=com");

        ldapInstance.addUser("uid=admin2,dc=example,dc=com", new HashMap<String, String>() {
            {
                put("cn", "admin2");
                put("sn", "admin2");
                put("displayName", "admin2");
                put("uid", "admin2");
                put("userPassword", "admin");
            }
        });

        // optionally we can start a server too
        ldapInstance.startServer(10389);
    }

    @After
    public void after() throws Exception {
        ldapInstance.shutdown();
    }

    @Test
    public void testGetConnection() {
        // Pobranie polaczenia dla usera istniejacego.
        // Powinno przejsc bez bledow
        try {
            Assert.assertNotNull(ldapUtil.getConnection("ldap://localhost:10389", "uid=admin,ou=system", "secret"));
            Assert.assertNotNull(ldapUtil.getConnection("ldap://localhost:10389", "uid=admin2,dc=example,dc=com",
                    "admin"));
        } catch (Exception e) {
            Assert.fail(e.getClass().getName() + " przy probie polaczenia: " + e.getMessage());
        }

        // Pobranie polaczenia jesli user nie istnieje - wyjatek
        try {
            Assert.assertNotNull(ldapUtil.getConnection("ldap://localhost:10389", "uid=admin3,dc=example,dc=com",
                    "admin"));
            Assert.fail("Powinien zostac zgloszony wyjatek LdapConnectException");
        } catch (LdapConnectException e) {
            // OK
        } catch (LdapException e) {
            Assert.fail("Powinien zostac zgloszony wyjatek LdapConnectException");
        }

        // Pobranie polaczenia jesli haslo niepoprawne - wyjatek
        try {
            Assert.assertNotNull(ldapUtil.getConnection("ldap://localhost:10389", "uid=admin2,dc=example,dc=com",
                    "badpassword"));
            Assert.fail("Powinien zostac zgloszony wyjatek LdapConnectException");
        } catch (LdapConnectException e) {
            // OK
        } catch (LdapException e) {
            Assert.fail("Powinien zostac zgloszony wyjatek LdapConnectException");
        }
    }

    @Test
    public void testGetDnByUid() {
        try {
            Assert.assertEquals("uid=admin2,dc=example,dc=com", ldapUtil.getDnByUid("admin2"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Nie powinien zostac zgloszony wyjatek" + e.getClass().getName() + " / " + e.getMessage());
        }
    }

    @Test
    public void testUpdatePassword() {
        try {
            ldapUtil.updatePassword("uid=admin2,dc=example,dc=com", "admin", "adminxxx");
            Assert.assertNotNull(ldapUtil.getConnection("ldap://localhost:10389", "uid=admin2,dc=example,dc=com",
                    "adminxxx"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Nie powinien zostac zgloszony wyjatek" + e.getClass().getName() + " / " + e.getMessage());
        }
    }

}
