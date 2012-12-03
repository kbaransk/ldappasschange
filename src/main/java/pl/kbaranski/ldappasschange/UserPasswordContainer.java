package pl.kbaranski.ldappasschange;

import java.io.Serializable;

public class UserPasswordContainer implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String oldPassword;
    private String password;
    private String passwordConfirm;

    public UserPasswordContainer() {
        // username = "username";
        // oldPassword = "old-password";
        // password = "new-password";
        // passwordConfirm = "new-pass-conf";
    }

    public UserPasswordContainer(String username, String oldPassword, String password, String passwordConfirm) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
