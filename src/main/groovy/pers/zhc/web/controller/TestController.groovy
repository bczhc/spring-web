package pers.zhc.web.controller


import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pers.zhc.web.Global

@RestController
class TestController {

    @PostMapping("/login")
    def login(
            @RequestParam(name = "username", defaultValue = "") String username,
            @RequestParam(name = "password", defaultValue = "") String password
    ) {
        def verify = verify(username, password)
        if (!verify) {
            return [
                    "status": 1,
                    "msg:"  : "Failed to verify"
            ]
        }

        def random = Math.random().toString()

        def digest = Global.sha256.digest(random.getBytes())
        def signature = Global.rsa.prkEncryptCipher.doFinal(digest)

        return [
                "status"   : 0,
                "msg"      : "Verifying succeeded",
                "signature": signature
        ]
    }

    @RequestMapping("request")
    def request(@RequestParam(name = "token", defaultValue = "") String token) {
        if (token.isEmpty()) {
            return [
                    "status": 1,
                    "msg"   : "Empty token"
            ]
        }

        def tokenData
        try {
            tokenData = token.decodeBase64()
        } catch (e) {
            return [
                    "status"   : 2,
                    "msg"      : "Failed to decode Base64",
                    "exception": e
            ]
        }


        def decodedToken
        try {
            decodedToken = Global.rsa.getPukDecryptCipher().doFinal(tokenData)
        } catch (e) {
            return [
                    "status"   : 3,
                    "msg"      : "Invalid token",
                    "exception": e
            ]
        }

        return [
                "status"      : 0,
                "msg"         : "Authentication succeeded",
                "randomDigest": decodedToken
        ]
    }

    private static boolean verify(String username, String password) {
        return username == "bczhc" && password == "123"
    }
}