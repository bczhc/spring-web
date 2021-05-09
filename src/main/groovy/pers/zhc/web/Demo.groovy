package pers.zhc.web

/**
 * @author bczhc
 */
class Demo {
    static void main(String[] args) {
        def url = new URL("http://localhost:8080/hello?name=bczhc")
        def connection = url.openConnection()
        connection.setDoOutput(true)
        def os = connection.getOutputStream()
        os.write([1, 2, 3] as byte[])
        def is = connection.getInputStream()
        def line = is.readLines()[0]
        is.close()

        println line
    }
}
