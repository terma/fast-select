package com.github.terma.fastselect.callbacks;

import junit.framework.Assert;
import org.junit.Test;

public class CounterCallbackTest {

    @Test
    public void empty() {
        CounterCallback counterCallback = new CounterCallback();
        Assert.assertEquals(0, counterCallback.getCount());
    }

    @Test
    public void withData() {
        CounterCallback counterCallback = new CounterCallback();
        counterCallback.data(1, null);
        counterCallback.data(1, null);
        counterCallback.data(1, null);
        Assert.assertEquals(3, counterCallback.getCount());
    }

}
