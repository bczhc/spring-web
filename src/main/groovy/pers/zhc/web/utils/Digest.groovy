package pers.zhc.web.utils

/**
 * @author bczhc
 */
class Digest {
    private static String complete(String byteHex) {
        if (byteHex.length() == 1) {
            return "0" + byteHex
        }
        return byteHex
    }

    static String toHexString(byte[] digest) {
        def sb = new StringBuilder()

        for (d in digest) {
            def i = d as int
            def byteHex = Integer.toHexString(i < 0 ? (i + 256) : i)
            sb.append(complete(byteHex))
        }
        return sb.toString()
    }
}
