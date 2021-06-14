package pers.zhc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pers.zhc.tools.jni.JNI
import pers.zhc.web.Global
import pers.zhc.web.secure.Communication

import javax.servlet.http.HttpServletResponse

/**
 * @author bczhc
 */
@RestController
class PublicKey {
    @Autowired
    private HttpServletResponse response

    /**
     * struct:
     * ____________________________________________________
     * | PublicKeyLength | PublicKey | PublicKeySignature |
     * ----------------------------------------------------
     */
    @GetMapping("/public-key")
    def publicKey() {
        def publicKey = Global.keyPair.getPublic().getEncoded()
        def length = publicKey.length

        def packedDest = new byte[4]
        JNI.Struct.packInt(length, packedDest, 0, JNI.Struct.MODE_BIG_ENDIAN)

        def signature = Communication.signData(publicKey)

        def totalLength = packedDest.length + length + signature.length

        response.setContentLength(totalLength)
        def os = response.getOutputStream()
        os.write(packedDest)
        os.write(publicKey)
        os.write(signature)
        os.flush()
        os.close()
    }
}
