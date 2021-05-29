package pers.zhc.web.utils;

import pers.zhc.tools.jni.JNI;
import pers.zhc.web.ApplicationMain;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * @author bczhc
 * struct:
 * _______________________________________________________________________________________________________________________________________________________________
 * | MagicNumber | SelfPublicKeyLength | SelfPublicKey | MsgDigestSignatureLength | MsgDigestSignature | MsgDigest | MsgKeyCiphertextLength | MsgKeyCiphertext | Message |
 * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
 */
public class MyCommunication {
    private final SHA256 sha256;
    private final RSA rsa;
    private final KeyGenerator aesKeyGenerator;

    private static final byte[] magicNumber = new byte[]{'b', 'c', 'z', 'h', 'c', 0, 0, 0};

    public MyCommunication(KeyPair selfKeyPair) {
        this.sha256 = new SHA256();
        this.rsa = new RSA(selfKeyPair);

        try {
            aesKeyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Key targetPublicKey, byte[] data) {
    }

    public byte[] resolve(InputStream in) {
        return null;
    }

    public int writePackedData(Key targetPublicKey, byte[] msg, OutputStream out) throws IOException {
        int totalLength = 0;

        out.write(magicNumber);
        totalLength += magicNumber.length;

        final byte[] publicKeyEncoded = rsa.getPublicKey().getEncoded();
        byte[] packedLengthDest = new byte[4];
        JNI.Struct.packInt(publicKeyEncoded.length, packedLengthDest, 0, JNI.Struct.MODE_BIG_ENDIAN);
        out.write(packedLengthDest);
        totalLength += packedLengthDest.length;

        out.write(publicKeyEncoded);
        totalLength += publicKeyEncoded.length;

        final byte[] msgDigest = sha256.digest(msg);
        final int msgDigestLength = msgDigest.length;
        final byte[] signature = sign(msgDigest);
        final int signatureLength = signature.length;
        JNI.Struct.packInt(signatureLength, packedLengthDest, 0, JNI.Struct.MODE_BIG_ENDIAN);
        out.write(packedLengthDest);
        totalLength += packedLengthDest.length;

        out.write(signature);
        totalLength += signature.length;

        out.write(msgDigest);
        totalLength += msgDigestLength;

        final byte[] aesKey = aesKeyGenerator.generateKey().getEncoded();
        final RSA newRsa = new RSA(targetPublicKey);
        final byte[] aesKeyCiphertext;
        try {
            aesKeyCiphertext = newRsa.getPukEncryptCipher().doFinal(aesKey);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
        JNI.Struct.packInt(aesKeyCiphertext.length, packedLengthDest, 0, JNI.Struct.MODE_BIG_ENDIAN);
        out.write(packedLengthDest);
        totalLength += packedLengthDest.length;

        out.write(aesKeyCiphertext);
        totalLength += aesKeyCiphertext.length;


        return totalLength;
    }

    private byte[] sign(byte[] data) {
        try {
            return rsa.getPrkEncryptCipher().doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sign data with server private key
     *
     * @return the signature
     */
    public static byte[] signData(byte[] data) throws RuntimeException {
        final byte[] digest = ApplicationMain.getSha256().digest(data);
        try {
            return ApplicationMain.getRsa().getPrkEncryptCipher().doFinal(digest);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
