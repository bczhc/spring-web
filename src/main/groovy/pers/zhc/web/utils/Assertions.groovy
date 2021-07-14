package pers.zhc.web.utils

/**
 * @author bczhc
 */
class Assertions {
    static doAssertion(boolean condition) {
        if (!condition) {
            throw new AssertionError("Assertion failed")
        }
    }
}
