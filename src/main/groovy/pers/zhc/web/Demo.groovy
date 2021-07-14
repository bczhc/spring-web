package pers.zhc.web

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.json.JSONObject

/**
 * @author bczhc
 */
class Demo {
    static {
        Global.init()
    }

    static def headMap = [
            "alg": "HS256",
            "typ": "JWT"
    ]
    static String headJSON = new JSONObject(headMap).toString()

    static void main(String[] args) {
        login("bczhc", "123")
    }

    static void login(String username, String password) {
        def secret = "hello".bytes

        if (username == "bczhc" && password == "123") {
            def payload = [
                    "username": username
            ]
            def payloadJSON = new JSONObject(payload).toString()

            def hmac256 = Algorithm.HMAC256(secret)

            def headJsonBase64 = Global.base64.urlEncoder.encode(headJSON.bytes)
            def payloadJsonBase64 = Global.base64.urlEncoder.encode(payloadJSON.bytes)
            def signature = hmac256.sign(headJsonBase64, payloadJsonBase64)

            def token = JWT.create()
                    .withHeader(headMap)
                    .withPayload(payload)
                    .sign(Algorithm.HMAC256(secret))

            println([
                    Global.base64.urlEncoder.encodeToString(signature),
                    token
            ])

            authenticate("token")
        }
    }

    static authenticate(String token) {
        try {
            def verifier = JWT.require(Algorithm.HMAC256("hello".bytes))
                    .build()
            def decodedJWT = verifier.verify(token)
        } catch (e) {
            e.printStackTrace()
        }
    }
}
