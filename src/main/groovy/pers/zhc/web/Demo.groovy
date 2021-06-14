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
        is.read(buf) == length
        is.close()

        def serverKeyPair = Communication.resolvePublicKeyResult(buf)


        def myKeyPair = RSA.generateKeyPair()
        def communication = new Communication(myKeyPair)


        def conn = communication.send(new URL("http://localhost:8080/demo"), serverKeyPair, [1, 2, 3, 4, 5] as byte[])
        def inputStream = conn.getInputStream()

        def baos = new ByteArrayOutputStream()
        int readLen
        def buf2 = new byte[1024]
        while ((readLen = inputStream.read(buf2)) != -1) {
            baos.write(buf2, 0, readLen)
        }
        inputStream.close()

        def resolved = communication.resolve(new ByteArrayInputStream(baos.toByteArray()))

        println new String(resolved.data)
    }
}
