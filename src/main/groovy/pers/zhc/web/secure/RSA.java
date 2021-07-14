package pers.zhc.web.secure;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author bczhc
 */
public class RSA {
    // Singleton
    private final Cipher[] ciphers = new Cipher[4];
    private final KeyPair keyPair;

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.genKeyPair();
    }

    public RSA(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public RSA(Key publicKey) {
        this(new KeyPair((PublicKey) publicKey, null));
    }

    public Cipher getPukEncryptCipher() {
        return lazyInitCipher(0, Cipher.ENCRYPT_MODE, keyPair.getPublic());
    }

    public Cipher getPukDecryptCipher() {
        return lazyInitCipher(1, Cipher.DECRYPT_MODE, keyPair.getPublic());

    }

    public Cipher getPrkEncryptCipher() {
        return lazyInitCipher(2, Cipher.ENCRYPT_MODE, keyPair.getPrivate());
    }

    public Cipher getPrkDecryptCipher() {
        return lazyInitCipher(3, Cipher.DECRYPT_MODE, keyPair.getPrivate());
    }

    private Cipher lazyInitCipher(int index, int mode, Key key) {
        try {
            if (ciphers[index] == null) {
                ciphers[index] = Cipher.getInstance("RSA");
                ciphers[index].init(mode, key);
            }
            return ciphers[index];
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public static PublicKey fromEncodedPublic(byte[] encoded) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey fromEncodedPrivate(byte[] encoded) throws InvalidKeySpecException {
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encoded));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair fromPrivateKey(PrivateKey privateKey) throws InvalidKeySpecException {
        final RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) privateKey;
        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateCrtKey.getModulus(), privateCrtKey.getPublicExponent());
        final PublicKey publicKey;
        try {
            publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return new KeyPair(publicKey, privateKey);
    }
}