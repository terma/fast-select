package com.github.terma.fastselect.utils;

import org.junit.Assert;
import org.junit.Test;

public class Utf8UtilsTest {

    @Test
    public void stringToBytesEmptyAndBack() {
        Assert.assertArrayEquals(new byte[]{}, Utf8Utils.stringToBytes(""));
        Assert.assertEquals("", Utf8Utils.bytesToString(new byte[0]));
    }

    @Test
    public void ascIIStringToBytesAndBack() {
        Assert.assertArrayEquals(new byte[]{97, 98, 122}, Utf8Utils.stringToBytes("abz"));
        Assert.assertEquals("abz", Utf8Utils.bytesToString(new byte[]{97, 98, 122}));
    }

    @Test
    public void nonAscIIStringToBytes() {
        Assert.assertArrayEquals(new byte[]{-47, -123, -48, -66, -48, -71}, Utf8Utils.stringToBytes("хой"));
        Assert.assertEquals("хой", Utf8Utils.bytesToString(new byte[]{-47, -123, -48, -66, -48, -71}));
    }

    @Test
    public void mixedSringToBytes() {
        Assert.assertArrayEquals(new byte[]{-48, -71, 90}, Utf8Utils.stringToBytes("йZ"));
        Assert.assertEquals("йZ", Utf8Utils.bytesToString(new byte[]{-48, -71, 90}));
    }

    @Test(expected = NullPointerException.class)
    public void nullStringToBytes() {
        Utf8Utils.stringToBytes(null);
    }

    @Test(expected = NullPointerException.class)
    public void nullBytesToString() {
        Utf8Utils.bytesToString(null);
    }

}
