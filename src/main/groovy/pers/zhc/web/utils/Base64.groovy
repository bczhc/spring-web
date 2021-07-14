package pers.zhc.web.utils

import java.util.Base64.Decoder
import java.util.Base64.Encoder

/**
 * @author bczhc
 */
class Base64 {
    public Encoder encoder
    public Encoder urlEncoder
    public Decoder decoder
    public Decoder urlDecoder

    Base64() {
        encoder = java.util.Base64.encoder
        decoder = java.util.Base64.decoder
        urlEncoder = java.util.Base64.getUrlEncoder()
        urlDecoder = java.util.Base64.getUrlDecoder()
    }
}
