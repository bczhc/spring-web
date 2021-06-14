package pers.zhc.web


import pers.zhc.web.secure.Communication
import pers.zhc.web.secure.RSA

/**
 * @author bczhc
 */
class Demo {
    static void main(String[] args) {
        def url = new URL("http://localhost:8080/public-key")
        def connection = url.openConnection()
        def length = connection.getContentLength()

        def buf = new byte[length]
        def is = connection.inputStream
        assert is.read(buf) == length
        is.close()

        def serverKeyPair = Communication.resolvePublicKeyResult(buf)


        def myKeyPair = RSA.generateKeyPair()
        def communication = new Communication(myKeyPair)


        def inputStream = communication.send(new URL("http://localhost:8080/demo"), serverKeyPair, [1, 2, 3, 4, 5] as byte[])
        println inputStream.readLines()
        inputStream.close()
    }
}
