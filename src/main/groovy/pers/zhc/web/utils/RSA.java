package pers.zhc.web.utils;

import pers.zhc.web.ApplicationMain;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * @author bczhc
 */
public class RSA {
    private Cipher pukEncryptCipher;
    private Cipher pukDecryptCipher;
    private Cipher prkEncryptCipher;
    private Cipher prkDecryptCipher;

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.genKeyPair();
    }

    public RSA() {
        try {
            this.pukEncryptCipher = Cipher.getInstance("RSA");
            this.pukDecryptCipher = Cipher.getInstance("RSA");
            this.prkEncryptCipher = Cipher.getInstance("RSA");
            this.prkDecryptCipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            this.pukEncryptCipher.init(Cipher.ENCRYPT_MODE, ApplicationMain.getKeyPair().getPublic());
            this.pukDecryptCipher.init(Cipher.DECRYPT_MODE, ApplicationMain.getKeyPair().getPublic());
            this.prkEncryptCipher.init(Cipher.ENCRYPT_MODE, ApplicationMain.getKeyPair().getPrivate());
            this.prkDecryptCipher.init(Cipher.DECRYPT_MODE, ApplicationMain.getKeyPair().getPrivate());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public Cipher getPukEncryptCipher() {
        return pukEncryptCipher;
    }

    public Cipher getPukDecryptCipher() {
        return pukDecryptCipher;
    }

    public Cipher getPrkEncryptCipher() {
        return prkEncryptCipher;
    }

    public Cipher getPrkDecryptCipher() {
        return prkDecryptCipher;
    }
}