package pers.zhc.web


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author bczhc
 */
@SpringBootApplication
@RestController
class Main {
    static void main(String[] args) {
        SpringApplication.run(Main, args)
    }

    @GetMapping("/")
    String response() {
        def a = []
        (0..<10000).forEach {
            a[it] = Object.newInstance().toString()
        }
        return a
    }
}
