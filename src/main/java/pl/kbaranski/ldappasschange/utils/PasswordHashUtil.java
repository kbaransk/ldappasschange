package pl.kbaranski.ldappasschange.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class PasswordHashUtil {

    public enum Algorithm {
        SHA, MD5
    }

    public static String getHash(Algorithm algorithm, String password) throws NoSuchAlgorithmException,
            IllegalArgumentException {
        // Validation
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("password cannot be null");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("password cannot be empty");
        }

        // Calculate hash value
        MessageDigest md = MessageDigest.getInstance(algorithm.toString());
        md.update(password.getBytes());
        byte[] bytes = md.digest();

        // Encode Base64
        Base64 b64 = new Base64();
        String hash = b64.encodeAsString(bytes);

        // Prefix with algorithm
        String result = new StringBuilder(255).append("{").append(algorithm).append("}").append(hash).toString();
        return result;
    }
}
