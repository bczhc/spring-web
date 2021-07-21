package pers.zhc.web.utils

import org.springframework.util.DigestUtils

import java.security.MessageDigest

/**
 * @author bczhc
 */
class SHA1 {
    MessageDigest sha1

    byte[] digest(byte[] data) {
        return sha1.digest(data)
    }

    SHA1() {
        sha1 = MessageDigest.getInstance("SHA1")
    }
}
