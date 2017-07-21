package com.github.terma.fastselect.utils;

import java.nio.charset.Charset;

public final class Utf8Utils {

    private final static Charset CHARSET = Charset.forName("utf-8");

    private Utf8Utils() {
        throw new UnsupportedOperationException("Just util class no instances!");
    }

    public static byte[] stringToBytes(String string) {
        return string.getBytes(CHARSET);
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes, CHARSET);
    }

}
