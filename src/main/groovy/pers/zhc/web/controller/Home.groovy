package pers.zhc.web.controller


import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author bczhc
 */
@RestController
class Home {
    @RequestMapping("/")
    def home() {
        return "<h1>Home</h1>"
    }
}
