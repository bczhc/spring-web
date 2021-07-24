package pers.zhc.web

import pers.zhc.jni.JNI
import pers.zhc.web.secure.Communication
import pers.zhc.web.secure.RSA
import pers.zhc.web.secure.SHA256
import pers.zhc.web.utils.Base64
import pers.zhc.web.utils.SHA1

import java.security.KeyPair

/**
 * @author bczhc
 */
class Global {
    public static KeyPair keyPair
    public static SHA256 sha256
    public static RSA rsa
    public static Communication communication
    public static Base64 base64
    public static SHA1 sha1

    static init() throws Exception {
        def propertiesFile = new File("./server.properties")
        if (!propertiesFile.exists()) {
            assert propertiesFile.createNewFile()
        }
        def is = new FileInputStream(propertiesFile)
        def properties = new Properties()
        properties.load(is)
        is.close()

        def propertiesKeys = [
                "libPath"       : "server.lib.path",
                "privateKeyPath": "server.privateKey.path"
        ]

        propertiesKeys.forEach { k, v ->
            if (properties.get(v) == null) {
                throw new MissingPropertyException("Missing property $v, please define it in server.properties.")
            }
        }

        def libPath = properties.get(propertiesKeys.libPath) as String
        def privateKeyPath = properties.get(propertiesKeys.privateKeyPath) as String

        JNILoader.load(libPath)

        def privateKeyFile = new File(privateKeyPath)
        def privateKeyEncoded = privateKeyFile.readBytes()
        def keyPair = RSA.fromPrivateKey(RSA.fromEncodedPrivate(privateKeyEncoded))
        checkKeyPair(keyPair)

        Global.keyPair = keyPair
        sha256 = new SHA256()
        rsa = new RSA(Global.keyPair)
        communication = new Communication(Global.keyPair)
        sha1 = new SHA1()
        base64 = new Base64()

        checkJNI()
    }

    private static checkJNI() {
        def n = 12345678
        def b = new byte[4]
        JNI.Struct.packInt(n, b, 0, JNI.Struct.MODE_BIG_ENDIAN)
        assert n == JNI.Struct.unpackInt(b, 0, JNI.Struct.MODE_BIG_ENDIAN)
    }

    static checkKeyPair(KeyPair keyPair) {
        def rsa = new RSA(keyPair)
        def bytes = "hello".getBytes()
        def cipherText = rsa.getPukEncryptCipher().doFinal(bytes)
        def plainText = rsa.getPrkDecryptCipher().doFinal(cipherText)
        assert plainText == bytes
    }
}
