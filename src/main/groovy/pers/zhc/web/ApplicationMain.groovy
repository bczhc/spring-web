package pers.zhc.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import pers.zhc.web.utils.RSA
import pers.zhc.web.utils.SHA256

import java.security.KeyPair

/**
 * @author bczhc
 */
@SpringBootApplication
class ApplicationMain {
    static KeyPair keyPair
    static SHA256 sha256
    static RSA rsa

    static void main(String[] args) {
        println "hello, world"
        init()
        SpringApplication.run(ApplicationMain, args)
    }

    private static init() {
        keyPair = RSA.generateKeyPair()
        sha256 = new SHA256()
        rsa = new RSA(keyPair)
    }

}
