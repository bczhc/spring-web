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
        def sc = new Scanner(System.in)
        while (true) {
            def expression = sc.nextLine()
            def result = Eval.me(expression)
            println result
        }
//        SpringApplication.run(Main, args)
    }
}
