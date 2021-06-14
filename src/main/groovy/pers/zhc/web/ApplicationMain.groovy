package pers.zhc.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import pers.zhc.web.secure.Communication
import pers.zhc.web.secure.RSA
import pers.zhc.web.secure.SHA256

/**
 * @author bczhc
 */
@SpringBootApplication
class ApplicationMain {
    static void main(String[] args) {
        println "hello, world"
        init()
        SpringApplication.run(ApplicationMain, args)
    }

    private static init() {
        Global.keyPair = RSA.generateKeyPair()
        Global.sha256 = new SHA256()
        Global.rsa = new RSA(Global.keyPair)
        Global.communication = new Communication(Global.keyPair)
    }
}
