package pers.zhc.web

import pers.zhc.tools.jni.JNI
import sun.security.x509.X509Key

import javax.crypto.Cipher
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec

/**
 * @author bczhc
 */
class Demo {
    static void main(String[] args) {
        // get server's public key and verify

        def url = new URL("http://localhost:8080/public-key")
        def connection = url.openConnection()
        def length = connection.getContentLength()

        def lengthBuf = new byte[4]
        def is = connection.getInputStream()
        is.read(lengthBuf)
        def publicKeyLength = JNI.Struct.unpackInt(lengthBuf, 0, JNI.Struct.MODE_BIG_ENDIAN)
        def publicKeyBuf = new byte[publicKeyLength]
        is.read(publicKeyBuf)
        def signatureBuf = new byte[length - 4 - publicKeyLength]
        is.read(signatureBuf)
        is.close()

        def digest = MessageDigest.getInstance("SHA-256").digest(publicKeyBuf)

        def cipher = Cipher.getInstance("RSA")
        def generatePublic = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBuf))

        cipher.init(Cipher.DECRYPT_MODE, generatePublic)
        def decryptedSignature = cipher.doFinal(signatureBuf)
        if (Arrays.equals(decryptedSignature, digest)) {
            println "ok"
        }
    }
}
