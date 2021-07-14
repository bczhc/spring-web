package pers.zhc.web

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import pers.zhc.web.secure.Communication
import pers.zhc.web.secure.RSA
import pers.zhc.web.secure.SHA256

import java.security.KeyPair

/**
 * @author bczhc
 */
@SpringBootApplication
class ApplicationMain {
    static void main(String[] args) {
        println "hello, world"

        try {
            init()
        } catch (e) {
            e.printStackTrace()
            return
        }
        SpringApplication.run(ApplicationMain, args)
    }

    private static init() {
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
        Global.sha256 = new SHA256()
        Global.rsa = new RSA(Global.keyPair)
        Global.communication = new Communication(Global.keyPair)
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
