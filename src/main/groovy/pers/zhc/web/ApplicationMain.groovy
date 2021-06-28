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
        init(args)
        SpringApplication.run(ApplicationMain, args)
    }

    private static init(String[] args) {
        if (args.length > 1) {
            System.err.println("Unexpected argument length")
            System.out.println("Usage:\n  Command [lib-path]")
            System.exit(1)
        }
        if (args.length == 1) {
            Global.LIB_PATH = args[0]
        }
        checkLib()

        Global.keyPair = RSA.generateKeyPair()
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
}
