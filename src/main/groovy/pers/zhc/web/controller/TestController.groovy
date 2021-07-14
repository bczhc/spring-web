package pers.zhc.web.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pers.zhc.web.Global

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class TestController {
    @Autowired
    private HttpServletRequest request
    @Autowired
    private HttpServletResponse response

    private def secret = Global.keyPair.private.encoded
    private def algorithm = Algorithm.HMAC256(secret)

    @RequestMapping("/token")
    def token(@RequestParam(name = "token", defaultValue = "") String token) {
        try {
            def verifier = JWT.require(Algorithm.HMAC256("hello".bytes))
                    .build()
            def decodedJWT = verifier.verify(token)
            return decodedJWT
        } catch (e) {
            return e
        }
    }

    class ReturnMsg {
        def status
        def msg
        def data

        ReturnMsg(int status, String msg, data) {
            this.status = status
            this.msg = msg
            this.data = data
        }

        ReturnMsg(int status, String msg) {
            this(status, msg, null)
        }
    }

    def headMap = [
            "typ": "JWT",
            "alg": "HS256"
    ]

    @PostMapping("/login")
    def login(
            @RequestParam(name = "username", defaultValue = "") String username,
            @RequestParam(name = "password", defaultValue = "") String password
    ) {
        if (loginUser(username, password)) {
            def payloadMap = [
                    "username": username,
                    "ist"     : System.currentTimeMillis()
            ]

            def token = JWT.create()
                    .withHeader(headMap)
                    .withPayload(payloadMap)
                    .sign(algorithm)

            response.addCookie(new Cookie("token", token))

            return new ReturnMsg(0, "Login succeeded", [
                    "token": token
            ])
        }

        return new ReturnMsg(1, "Wrong username or password")
    }

    @RequestMapping("request")
    def request() {
        String token

        def cookies = request.getCookies()
        cookies.indices.forEach { i ->
            if (cookies[i].name == "token") {
                token = cookies[i].value
            }
        }

        if (token == null) {
            return new ReturnMsg(1, "Missing token", null)
        }

        def verifier = JWT.require(algorithm).build()
        def decodedJWT
        try {
            decodedJWT = verifier.verify(token)
        } catch (e) {
            return new ReturnMsg(2, "Verifying failed", [
                    "error": e.toString()
            ])
        }

        def payloadJSON = new JSONObject(new String(Global.base64.urlDecoder.decode(decodedJWT.payload)))
        def username = payloadJSON["username"] as String

        return new ReturnMsg(0, "Authentication succeeded", [
                "username": username
        ])
    }

    static def loginUser(String username, String password) {
        return username == "bczhc" && password == "123"
    }
}