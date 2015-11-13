package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.ArrayLayoutFastSelect;
import com.github.terma.fastselect.callbacks.MultiGroupCountCallback;
import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;

public class MultiGroupCountCallbackTest {

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void throwExceptionIfNoGroup() {
        new MultiGroupCountCallback();
    }

    @Test(expected = NegativeArraySizeException.class)
    public void throwExceptionIfOneGroup() {
        ArrayLayoutFastSelect.Column column = new ArrayLayoutFastSelect.Column("x", int.class);
        new MultiGroupCountCallback(column);
    }

    @Test
    public void empty() {
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);
        ArrayLayoutFastSelect.Column column2 = new ArrayLayoutFastSelect.Column("2", int.class);
        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        Assert.assertEquals(new HashMap<>(), multiGroupCountCallback.getCounters());
    }

    @Test
    public void nonEmpty() {
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);
        ArrayLayoutFastSelect.Column column2 = new ArrayLayoutFastSelect.Column("2", int.class);
        ((ArrayLayoutFastSelect.FastIntList) column1.data).add(1);
        ((ArrayLayoutFastSelect.FastIntList) column2.data).add(2);

        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        multiGroupCountCallback.data(0);
        Assert.assertEquals(new HashMap<Integer, Object>() {{
            put(1, new HashMap<Integer, Object>() {{
                put(2, 1);
            }});
        }}, multiGroupCountCallback.getCounters());
    }

    @Test
    public void nonEmptyMulti() {
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);
        ArrayLayoutFastSelect.Column column2 = new ArrayLayoutFastSelect.Column("2", int.class);
        ((ArrayLayoutFastSelect.FastIntList) column1.data).add(1);
        ((ArrayLayoutFastSelect.FastIntList) column2.data).add(2);

        ((ArrayLayoutFastSelect.FastIntList) column1.data).add(5);
        ((ArrayLayoutFastSelect.FastIntList) column2.data).add(10);

        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        multiGroupCountCallback.data(0);
        multiGroupCountCallback.data(1);
        Assert.assertEquals(new HashMap<Integer, Object>() {{
            put(1, new HashMap<Integer, Object>() {{
                put(2, 1);
            }});
            put(5, new HashMap<Integer, Object>() {{
                put(10, 1);
            }});
        }}, multiGroupCountCallback.getCounters());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void throwExceptionIfNotExistentPosition() {
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);
        ArrayLayoutFastSelect.Column column2 = new ArrayLayoutFastSelect.Column("2", int.class);

        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        multiGroupCountCallback.data(66);
    }

}
