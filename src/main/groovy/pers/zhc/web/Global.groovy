package pers.zhc.web

import pers.zhc.web.secure.Communication
import pers.zhc.web.secure.RSA
import pers.zhc.web.secure.SHA256
import pers.zhc.web.utils.Base64

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

    public static String[] libPaths

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
                "libsDir"       : "server.libs.dir",
                "privateKeyPath": "server.privateKey.path"
        ]

        propertiesKeys.forEach { k, v ->
            if (properties.get(v) == null) {
                throw new MissingPropertyException("Missing property $v, please define it in server.properties.")
            }
        }

        def libsDir = properties.get(propertiesKeys.libsDir) as String
        def privateKeyPath = properties.get(propertiesKeys.privateKeyPath) as String

        loadLibs(libsDir)

        def privateKeyFile = new File(privateKeyPath)
        def privateKeyEncoded = privateKeyFile.readBytes()
        def keyPair = RSA.fromPrivateKey(RSA.fromEncodedPrivate(privateKeyEncoded))
        checkKeyPair(keyPair)

        Global.keyPair = keyPair
        sha256 = new SHA256()
        rsa = new RSA(Global.keyPair)
        communication = new Communication(Global.keyPair)

        base64 = new Base64()
    }

    private static loadLibs(String libsDir) {
        def libPaths = []
        // TODO loadLibs, not using hardcoding
        [
                "libmagic.so",
                "libmyLib.so",
                "libMain.so"
        ].forEach {
            def libFile = new File(libsDir, it)
            libPaths.add(libFile.getAbsolutePath())
        }
        Global.libPaths = libPaths
    }

    static checkKeyPair(KeyPair keyPair) {
        def rsa = new RSA(keyPair)
        def bytes = "hello".getBytes()
        def cipherText = rsa.getPukEncryptCipher().doFinal(bytes)
        def plainText = rsa.getPrkDecryptCipher().doFinal(cipherText)
        assert plainText == bytes
    }
}
