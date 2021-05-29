package pers.zhc.web.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author bczhc
 */
public class SHA256 {
    private MessageDigest instance;

    public SHA256() {
        try {
            this.instance = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] digest(byte[] data) {
        this.instance.reset();
        return this.instance.digest(data);
    }
}
