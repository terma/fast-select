package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.callbacks.CounterCallback;
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
        counterCallback.data(1);
        counterCallback.data(1);
        counterCallback.data(1);
        Assert.assertEquals(3, counterCallback.getCount());
    }

}
