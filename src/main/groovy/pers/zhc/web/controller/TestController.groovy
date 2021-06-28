package pers.zhc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pers.zhc.web.Global
import pers.zhc.web.secure.ResolveException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets

@RestController
class TestController {
    @Autowired
    private HttpServletRequest request
    @Autowired
    private HttpServletResponse response

    @RequestMapping("/demo")
    def hello() {
        println response
        def is = request.getInputStream()

        def resolved
        try {
            resolved = Global.communication.resolve(is)
            is.close()
        } catch (ResolveException e) {
            e.printStackTrace()
            response.outputStream.println("Failed to resolve")
            response.outputStream.println(e.toString())
            return
        }

        def r = "received: ${new String(resolved.data)}"

        def outputStream = response.getOutputStream()
        def length = Global.communication.writePackedData(resolved.publicKey, outputStream, r.getBytes(StandardCharsets.UTF_8))
        response.setContentLengthLong(length)
        outputStream.close()
    }
}