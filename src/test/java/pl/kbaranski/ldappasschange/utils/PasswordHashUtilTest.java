package pl.kbaranski.ldappasschange.utils;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;

public class PasswordHashUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void getHashNullAlgorithmTest() throws NoSuchAlgorithmException, IllegalArgumentException {
        PasswordHashUtil.getHash(null, "a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHashMd5NullPasswordTest() throws NoSuchAlgorithmException, IllegalArgumentException {
        PasswordHashUtil.getHash(PasswordHashUtil.Algorithm.MD5, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHashMd5EmptyPasswordTest() throws NoSuchAlgorithmException, IllegalArgumentException {
        PasswordHashUtil.getHash(PasswordHashUtil.Algorithm.MD5, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHashShaNullPasswordTest() throws NoSuchAlgorithmException, IllegalArgumentException {
        PasswordHashUtil.getHash(PasswordHashUtil.Algorithm.SHA, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getHashShaEmptyPasswordTest() throws NoSuchAlgorithmException, IllegalArgumentException {
        PasswordHashUtil.getHash(PasswordHashUtil.Algorithm.SHA, "");
    }

    @Test
    public void getHashShaTest() throws NoSuchAlgorithmException, IllegalArgumentException {
        Assert.assertEquals("{SHA}/xK72MkHrwZwcCEdh73wmL4XN1s=",
                PasswordHashUtil.getHash(PasswordHashUtil.Algorithm.SHA, "haslo"));
    }

    @Test
    public void getHashMd5Test() throws NoSuchAlgorithmException, IllegalArgumentException {
        Assert.assertEquals("{MD5}IHAjzLRP6019rcoAXOKaZA==",
                PasswordHashUtil.getHash(PasswordHashUtil.Algorithm.MD5, "haslo"));
    }
}
