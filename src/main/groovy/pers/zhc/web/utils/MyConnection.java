package pers.zhc.web.utils;

import pers.zhc.web.ApplicationMain;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author bczhc
 * struct:
 * _____________________________________________________________________________________
 * | MagicNumber | PublicKey | MsgDigestSignature | MsgDigest | CipherMsgKey | Message |
 * -------------------------------------------------------------------------------------
 */
public class MyConnection {
    public static void send(byte[] data) {

    }

    /**
     * Sign data with server private key
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
