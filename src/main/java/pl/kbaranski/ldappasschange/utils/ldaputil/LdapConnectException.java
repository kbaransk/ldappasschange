package pl.kbaranski.ldappasschange.utils.ldaputil;

public class LdapConnectException extends LdapException {

    private static final long serialVersionUID = 1L;

    public LdapConnectException() {
        super();
    }

    public LdapConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public LdapConnectException(String message) {
        super(message);
    }

    public LdapConnectException(Throwable cause) {
        super(cause);
    }

}
