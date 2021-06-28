package pers.zhc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

/**
 * @author bczhc
 */
@RestController
class Bc {
    @Autowired
    private HttpServletResponse response

    @GetMapping("/bc")
    def bc(@RequestParam(name = "expression", defaultValue = "0") String expression) {
        response.setHeader("Access-Control-Allow-Origin", "*")

        def runtime = Runtime.getRuntime()
        def exec = runtime.exec("/usr/bin/bc")
        def os = exec.getOutputStream()
        os.write(expression.getBytes())
        os.write("\n".getBytes())
        os.flush()
        os.close()
        def is = exec.getInputStream()
        def lines = is.readLines()
        is.close()

        def sb = new StringBuilder()
        lines.forEach {
            sb.append(it).append("\n")
        }
        sb.deleteCharAt(sb.length() - 1)
        def r = sb.toString()
        println r
        return [
                "status": "SUCCESS",
                "result": r
        ]
    }
}
