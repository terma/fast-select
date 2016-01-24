package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.data.IntData;
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
        FastSelect.Column column = new FastSelect.Column("x", int.class);
        new MultiGroupCountCallback(column);
    }

    @Test
    public void empty() {
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);
        FastSelect.Column column2 = new FastSelect.Column("2", int.class);
        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        Assert.assertEquals(new HashMap<>(), multiGroupCountCallback.getCounters());
    }

    @Test
    public void nonEmpty() {
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);
        FastSelect.Column column2 = new FastSelect.Column("2", int.class);
        ((IntData) column1.data).add(1);
        ((IntData) column2.data).add(2);

        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        multiGroupCountCallback.data(0, null);
        Assert.assertEquals(new HashMap<Integer, Object>() {{
            put(1, new HashMap<Integer, Object>() {{
                put(2, 1);
            }});
        }}, multiGroupCountCallback.getCounters());
    }

    @Test
    public void nonEmptyMulti() {
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);
        FastSelect.Column column2 = new FastSelect.Column("2", int.class);
        ((IntData) column1.data).add(1);
        ((IntData) column2.data).add(2);

        ((IntData) column1.data).add(5);
        ((IntData) column2.data).add(10);

        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        multiGroupCountCallback.data(0, null);
        multiGroupCountCallback.data(1, null);
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
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);
        FastSelect.Column column2 = new FastSelect.Column("2", int.class);

        MultiGroupCountCallback multiGroupCountCallback = new MultiGroupCountCallback(column1, column2);
        multiGroupCountCallback.data(66, null);
    }

}
