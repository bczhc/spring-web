package pers.zhc.web.controller.apps.sometools

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pers.zhc.web.Global
import pers.zhc.web.IOUtils

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author bczhc
 */
@RestController
class ExternalJNI {
    @Autowired
    private HttpServletResponse response
    @Autowired
    private HttpServletRequest request

    private static def ABI = [
            "arm64-v8a",
            "armeabi-v7a",
            "x86",
            "x86_64"
    ]

    /**
     * Use secure communication
     * @param abi
     * @return
     */
    @RequestMapping("/some-tools-app/external-jni")
    def request(@RequestParam(name = "abi", defaultValue = "Unknown") String abi) {
        if (abi == "Unknown") {
            return [
                    "status" : 1,
                    "message": "Missing abi parameter"
            ]
        }
        if (!(abi in ABI)) {
            return [
                    "status" : 1,
                    "message": "Unknown abi"
            ]
        }

        // TODO ...

        def libFile = new File("./xxx")

        def fis = new FileInputStream(libFile)
        def bytes = IOUtils.streamToByteArray(fis)

        def outputStream = response.outputStream
        Global.communication.writePackedData(outputStream, bytes)
        fis.close()

        outputStream.close()
    }
}
