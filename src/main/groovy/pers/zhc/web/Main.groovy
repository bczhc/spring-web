package pers.zhc.web

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author bczhc
 */
@SpringBootApplication
class Main {
    static void main(String[] args) {
        println "hello, world"
        SpringApplication.run(Main, args)
    }
}
