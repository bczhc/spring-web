package pers.zhc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest

@RestController
class TestController {
    @Autowired
    private HttpServletRequest request

    @RequestMapping("/hello")
    def hello() {
        def is = request.getInputStream()
        def buf = new byte[1024];
        is.read(buf)
        def s = Arrays.toString(buf)
        println s
        return s
    }
}