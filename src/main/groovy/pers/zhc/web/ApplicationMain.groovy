package pers.zhc.web

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
            init(args)
        } catch (e) {
            if (e instanceof IllegalArgumentException) {
                return
            } else {
                e.printStackTrace()
            }
        }
        SpringApplication.run(ApplicationMain, args)
    }

    private static init(String[] args) {
        def privateKeyFile = new File("server-java-private")
        if (!privateKeyFile.exists()) {
            privateKeyFile = null
            println "\"./server-java-private\" not found"
        }

        def libFile = new File("libbczhc.so")
        if (!libFile.exists()) {
            libFile = null
            println "\"./libbczhc.so\" not found"
        }

        if ((args.length >= 1 && (args[0] == "-h" || args[0] == "--help")) || (privateKeyFile == null || libFile == null)) {
            System.err.println("Usage: command <lib-path> <private-key>")
            throw new IllegalArgumentException()
        }

        Global.LIB_PATH = libFile
        checkLib()

        def privateKeyEncoded = privateKeyFile.readBytes()
        def keyPair = RSA.fromPrivateKey(RSA.fromEncodedPrivate(privateKeyEncoded))
        checkKeyPair(keyPair)

        Global.keyPair = keyPair
        Global.sha256 = new SHA256()
        Global.rsa = new RSA(Global.keyPair)
        Global.communication = new Communication(Global.keyPair)
    }

    private static checkLib() {
        final File libFile = new File(Global.LIB_PATH)
        if (!libFile.exists()) {
            System.err.println("Cannot find \"${Global.LIB_PATH}\"")
            System.exit(1)
        }
    }

    static checkKeyPair(KeyPair keyPair) {
        def rsa = new RSA(keyPair)
        def bytes = "hello".getBytes()
        def cipherText = rsa.getPukEncryptCipher().doFinal(bytes)
        def plainText = rsa.getPrkDecryptCipher().doFinal(cipherText)
        assert plainText == bytes
    }
}
