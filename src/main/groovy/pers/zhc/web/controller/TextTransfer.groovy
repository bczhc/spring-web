package pers.zhc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author bczhc
 */
@RestController
class TextTransfer {
    @Autowired
    private HttpServletRequest request
    @Autowired
    private HttpServletResponse response
    private String text = ""

    @RequestMapping("/text-transfer")
    def main() {
        response.setHeader("Access-Control-Allow-Origin", "*")

        def text = request.getParameter("text")
        if (text == null) {
            //noinspection GroovyVariableNotAssigned
            return [
                    "result": this.text
            ]
        }
        this.text = text
        return [
                "result": this.text
        ]
    }
}
