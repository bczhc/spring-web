package pers.zhc.web

import javax.crypto.Cipher
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator

/**
 * @author bczhc
 */
class Demo {
    static void main(String[] args) {
        def keyPair = genKeyPair()
        def publicKey = keyPair.getPublic()
        def privateKey = keyPair.getPrivate()

        def c1 = encrypt("hello, world".bytes, publicKey)
        def c2 = encrypt(c1, privateKey)


        def p1 = decrypt(c2, publicKey)
        def p2 = decrypt(p1, privateKey)
        println new String(p2)
    }

    private static KeyPair genKeyPair() {
        def generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(1024)
        def keyPair = generator.generateKeyPair()
        return keyPair
    }

    private static byte[] encrypt(byte[] data, Key key) {
        return cipher(Cipher.ENCRYPT_MODE, data, key)
    }

    private static byte[] decrypt(byte[] data, Key key) {
        return cipher(Cipher.DECRYPT_MODE, data, key)
    }

    private static byte[] cipher(int mode, byte[] data, Key key) {
        def r = []
        def cipher = Cipher.getInstance("RSA")

        def lenLimit
        if (mode == Cipher.ENCRYPT_MODE) {
            lenLimit = 117
        } else {
            lenLimit = 128
        }
        def left = data.length % lenLimit
        def t = data.length.intdiv(lenLimit)
        def i = 0
        for (; i < t; ++i) {
            cipher.init(mode, key)
            r.addAll(cipher.doFinal(data, lenLimit * i, lenLimit))
        }
        if (left != 0) {
            cipher.init(mode, key)
            r.addAll(cipher.doFinal(data, lenLimit * i, left))
        }

        def a = new byte[r.size()]
        for (j in 0..<r.size()) {
            a[j] = (r[j] as Byte)
        }
        return a
    }
}
