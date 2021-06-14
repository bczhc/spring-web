package pers.zhc.web

import pers.zhc.web.secure.Communication
import pers.zhc.web.secure.RSA
import pers.zhc.web.secure.SHA256

import java.security.KeyPair

/**
 * @author bczhc
 */
class Global {
    public static KeyPair keyPair
    public static SHA256 sha256
    public static RSA rsa
    public static Communication communication
}
