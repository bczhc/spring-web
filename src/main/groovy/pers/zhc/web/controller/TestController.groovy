package pers.zhc.web.controller

import org.springframework.beans.factory.annotation.Autowired
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

    @RequestMapping("/demo")
    def hello() {
        def is = request.getInputStream()
        def buf = new byte[1024]
        def readLen
        def totalLen = 0
        while ((readLen = is.read(buf) != -1)) {
            totalLen += readLen
        }
        return [
                "msg"              : "Hallo du Lama!",
                "inputStreamLength": totalLen
        ]
    }
}