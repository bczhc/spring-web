package pers.zhc.web;

/**
 * @author bczhc
 * Since writing `System.load(...)` in Groovy code doesn't work, do this in Java instead
 */
public class JNILoader {
    public static void load(String path) {
        System.load(path);
    }
}
