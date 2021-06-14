package pers.zhc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pers.zhc.web.Global

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class TestController {
    @Autowired
    private HttpServletRequest request
    @Autowired
    private HttpServletResponse response

    @RequestMapping("/demo")
    def hello() {
        def is = request.getInputStream()

        def resolved = Global.communication.resolve(is)
        println resolved.toString()
        return resolved.toString()
    }
}