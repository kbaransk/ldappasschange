package pl.kbaranski.ldappasschange.utils;

public class LdapConfigHandler {
    String url;
    String userSearchBaseDn;
    String defaultUser;
    String defaultPass;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserSearchBaseDn() {
        return userSearchBaseDn;
    }

    public void setUserSearchBaseDn(String userSearchBaseDn) {
        this.userSearchBaseDn = userSearchBaseDn;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public String getDefaultPass() {
        return defaultPass;
    }

    public void setDefaultPass(String defaultPass) {
        this.defaultPass = defaultPass;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("[url: ").append(getUrl()).append("; ");
        sb.append("baseDn: ").append(getUserSearchBaseDn()).append("; ");
        sb.append("user: ").append(getDefaultUser()).append("; ");
        sb.append("pass: ").append(getDefaultPass()).append("]");
        return sb.toString();
    }
}
