package pers.zhc.web.controller


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class TestController {
    @Autowired
    private HttpServletRequest request
    @Autowired
    private HttpServletResponse response

    @RequestMapping("/hello1")
    def hello1() {
        def headers = response.getHeader("Content-Type")
        println headers
        return [1, 2]
    }

    @GetMapping("/hello2")
    def hello2() {
        def headers = response.getHeader("Content-Type")
        println headers
        return ["a": 2]
    }
}