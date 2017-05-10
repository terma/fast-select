package com.github.terma.fastselect.utils;

@SuppressWarnings("WeakerAccess")
public final class Utf8Utils {

    private Utf8Utils() {
        throw new UnsupportedOperationException("Util class!");
    }

    public static boolean isLatinOnly(final byte[] utf8Bytes) {
        for (byte b : utf8Bytes) if (isNonLatin(b)) return false;
        return true;
    }

    public static boolean isNonLatin(final byte utf8Byte) {
        return utf8Byte < 0; // if not latin character
    }

    public static void latinToLowerCase(byte[] utf8Bytes) {
        for (int i = 0; i < utf8Bytes.length; i++) {
            final byte b = utf8Bytes[i];
            if (b >= 0x41 && b <= 0x5A) utf8Bytes[i] = (byte) (b + 0x20); // only latin to lower case
        }
    }

    public static byte latinToLowerCase(byte utf8Byte) {
        return utf8Byte >= 0x41 && utf8Byte <= 0x5A ? (byte) (utf8Byte + 0x20) : utf8Byte;
    }

    public static byte latinToUpperCase(byte utf8Byte) {
        return utf8Byte >= 0x61 && utf8Byte <= 0x7A ? (byte) (utf8Byte - 0x20) : utf8Byte;
    }

    public static boolean latinBytesContains(
            final byte[] bytes, final int start, final int end, final byte[] likeBytes) {
        final int l1 = end - start;
        if (l1 < likeBytes.length) return false;
        int l = end - likeBytes.length;
        opa:
        for (int i = start; i <= l; i++) { // go cross source bytes
            // comp
            int t = i;
            for (byte likeByte : likeBytes) { // on each source byte start check substring
                byte low = bytes[t];
                if (Utf8Utils.isNonLatin(low)) return false; // if not latin character
                if (low >= 0x41 && low <= 0x5A) low = (byte) (low + 0x20); // only latin to lower case
                if (likeByte != low) continue opa; // stop compare try from next pos
                t++;
            }
            return true;
        }
        return false;
    }

}
