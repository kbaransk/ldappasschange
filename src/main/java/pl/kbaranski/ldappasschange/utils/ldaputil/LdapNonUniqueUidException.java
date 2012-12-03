package pl.kbaranski.ldappasschange.utils.ldaputil;

public class LdapNonUniqueUidException extends LdapException {

    private static final long serialVersionUID = 1L;

    public LdapNonUniqueUidException() {
        super();
    }

    public LdapNonUniqueUidException(String message, Throwable cause) {
        super(message, cause);
    }

    public LdapNonUniqueUidException(String message) {
        super(message);
    }

    public LdapNonUniqueUidException(Throwable cause) {
        super(cause);
    }

}
