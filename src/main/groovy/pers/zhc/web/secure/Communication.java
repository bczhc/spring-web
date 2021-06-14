package pers.zhc.web.secure;

import pers.zhc.tools.jni.JNI;
import pers.zhc.web.Global;

import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * @author bczhc
 * struct:
 * ____________________________________________________________________________________________________________________________________________________________________________________________________________________________________
 * | MagicNumber (8) | SenderPublicKeyLength (4) | SenderPublicKey | MsgDigest (32) | MsgDigestSignatureLength (4) | MsgDigestSignature | MsgKeyCiphertextLength (4) | MsgKeyCiphertext | MessageCiphertextLength | MessageCiphertext |
 * ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 */
public class Communication {
    private final SHA256 sha256;
    private final RSA rsa;
    private final KeyGenerator aesKeyGenerator;
    private final KeyPair senderKeyPair;

    private static final byte[] magicNumber = new byte[]{'b', 'c', 'z', 'h', 'c', 0, 0, 0};

    public Communication(KeyPair senderKeyPair) {
        this.sha256 = new SHA256();
        this.senderKeyPair = senderKeyPair;
        this.rsa = new RSA(senderKeyPair);

        try {
            aesKeyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * send data to the server
     *
     * @param url             target
     * @param targetPublicKey target public key
     * @param data            data
     * @return connection
     * @throws IOException IOException
     */
    public URLConnection send(URL url, Key targetPublicKey, byte[] data) throws IOException {
        final URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        final OutputStream os = connection.getOutputStream();
        writePackedDate(targetPublicKey, os, data);
        os.close();

        return connection;
    }

    /**
     * Write packed data to an output stream
     *
     * @param targetPublicKey target public key
     * @param to              target output stream
     * @param data            data to packed
     * @return total length of the packed data
     */
    @SuppressWarnings("DuplicatedCode")
    public long writePackedDate(Key targetPublicKey, OutputStream to, byte[] data) {
        try {
            long length = 0;
            final byte[] lengthBuf = new byte[4];

            // MagicNumber
            to.write(magicNumber);
            length += magicNumber.length;

            final byte[] senderPubKeyBuf = senderKeyPair.getPublic().getEncoded();
            final int senderKeyPairLength = senderPubKeyBuf.length;

            JNI.Struct.packInt(senderKeyPairLength, lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
            // SenderPublicKeyLength
            to.write(lengthBuf);
            length += lengthBuf.length;
            // SenderPublicKey
            to.write(senderPubKeyBuf);
            length += senderPubKeyBuf.length;

            final byte[] digest = sha256.digest(data);
            // MsgDigest
            to.write(digest);
            length += digest.length;

            final byte[] digestSignature = rsa.getPrkEncryptCipher().doFinal(digest);
            final int digestSignatureLength = digestSignature.length;
            JNI.Struct.packInt(digestSignatureLength, lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
            // MsgDigestSignatureLength
            to.write(lengthBuf);
            length += lengthBuf.length;
            // MsgDigestSignature
            to.write(digestSignature);
            length += digestSignature.length;

            final SecretKey aesKey = aesKeyGenerator.generateKey();
            final byte[] aesKeyByte = aesKey.getEncoded();
            final byte[] aesKeyCiphertext = new RSA(targetPublicKey).getPukEncryptCipher().doFinal(aesKeyByte);

            final int aesKeyCiphertextLength = aesKeyCiphertext.length;
            JNI.Struct.packInt(aesKeyCiphertextLength, lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
            // MsgKeyCiphertextLength
            to.write(lengthBuf);
            length += lengthBuf.length;
            // MsgKeyCiphertext
            to.write(aesKeyCiphertext);
            length += aesKeyCiphertext.length;
            to.flush();

            final byte[] messageCiphertext = AES.encrypt(aesKey, data);

            JNI.Struct.packInt(messageCiphertext.length, lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
            // MessageCiphertextLength
            to.write(lengthBuf);
            length += lengthBuf.length;
            // MessageCiphertext
            to.write(messageCiphertext);
            length += messageCiphertext.length;
            to.flush();
            return length;
        } catch (IOException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Resolved {
        public PublicKey publicKey;
        public byte[] data;

        public Resolved(PublicKey publicKey, byte[] data) {
            this.publicKey = publicKey;
            this.data = data;
        }
    }

    /**
     * Resolve packed data
     *
     * @param in input stream
     * @return resolved data
     * @throws ResolveException failed to resolve
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Resolved resolve(InputStream in) throws ResolveException, IOException {
        final byte[] lengthBuf = new byte[4];
        final byte[] byte8Buf = new byte[8];

        in.read(byte8Buf);
        if (!Arrays.equals(byte8Buf, magicNumber)) {
            throw new ResolveException("Unknown magic number");
        }

        in.read(lengthBuf);
        final int pubKeyLength = JNI.Struct.unpackInt(lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
        final byte[] pubKeyBuf = new byte[pubKeyLength];
        in.read(pubKeyBuf);
        final PublicKey pubKey;
        try {
            pubKey = RSA.fromEncodedPublic(pubKeyBuf);
        } catch (InvalidKeySpecException e) {
            throw new ResolveException(e);
        }

        final byte[] digest = new byte[32];
        in.read(digest);

        in.read(lengthBuf);
        final int digestSignatureLength = JNI.Struct.unpackInt(lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
        final byte[] digestSignature = new byte[digestSignatureLength];
        in.read(digestSignature);

        final byte[] signatureDecrypted;
        try {
            signatureDecrypted = new RSA(pubKey).getPukDecryptCipher().doFinal(digestSignature);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new ResolveException(e);
        }
        if (!Arrays.equals(digest, signatureDecrypted)) {
            throw new ResolveException("Authorization failed");
        }

        in.read(lengthBuf);
        final int aesKeyCiphertextLength = JNI.Struct.unpackInt(lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
        final byte[] aesKeyCiphertextBuf = new byte[aesKeyCiphertextLength];
        in.read(aesKeyCiphertextBuf);
        final byte[] aesKeyBuf;
        try {
            aesKeyBuf = rsa.getPrkDecryptCipher().doFinal(aesKeyCiphertextBuf);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new ResolveException(e);
        }
        final SecretKey aesKey = AES.fromEncodedKey(aesKeyBuf);

        in.read(lengthBuf);
        final int messageCiphertextLength = JNI.Struct.unpackInt(lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);

        final byte[] messageCiphertext = new byte[messageCiphertextLength];
        in.read(messageCiphertext);
        final byte[] message;
        try {
            message = AES.decrypt(aesKey, messageCiphertext);
        } catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new ResolveException(e);
        }

        return new Resolved(pubKey, message);
    }

    /**
     * Sign data with server private key
     *
     * @return the signature
     */
    public static byte[] signData(byte[] data) throws RuntimeException {
        final byte[] digest = Global.sha256.digest(data);
        try {
            return Global.rsa.getPrkEncryptCipher().doFinal(digest);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolve the public key from the result data sent from the server
     *
     * @return the public key
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static PublicKey resolvePublicKeyResult(byte[] data) throws InvalidDataException {
        // get server's public key and verify
        try {
            final int length = data.length;
            final ByteArrayInputStream is = new ByteArrayInputStream(data);
            byte[] lengthBuf = new byte[4];
            is.read(lengthBuf, 0, 4);
            int publicKeyLength = JNI.Struct.unpackInt(lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN);
            byte[] publicKeyBuf = new byte[publicKeyLength];
            is.read(publicKeyBuf);
            final int signatureLen = length - 4 - publicKeyLength;
            byte[] signatureBuf = new byte[signatureLen];
            is.read(signatureBuf);
            is.close();

            byte[] digest = MessageDigest.getInstance("SHA-256").digest(publicKeyBuf);

            Cipher cipher = Cipher.getInstance("RSA");
            PublicKey publicKey = RSA.fromEncodedPublic(publicKeyBuf);

            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedSignature = cipher.doFinal(signatureBuf);
            if (Arrays.equals(decryptedSignature, digest)) {
                return publicKey;
            } else {
                throw new VerificationFailedException();
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException | InvalidKeyException e) {
            throw new InvalidDataException(e);
        }
    }
}
