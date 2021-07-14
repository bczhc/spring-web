package pers.zhc.web


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author bczhc
 */
@SpringBootApplication
class ApplicationMain {
    static void main(String[] args) {
        println "hello, world"
        try {
            Global.init()
        } catch (e) {
            e.printStackTrace()
            return
        }
        SpringApplication.run(ApplicationMain, args)
    }
}
