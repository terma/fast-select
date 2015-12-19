package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.data.FastIntData;
import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;

public class GroupCountCallbackTest {

    @Test
    public void empty() {
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);
        GroupCountCallback groupCountCallback = new GroupCountCallback(column1);
        Assert.assertEquals(new HashMap<>(), groupCountCallback.getCounters());
    }

    @Test
    public void nonEmpty() {
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);
        ((FastIntData) column1.data).add(1);

        GroupCountCallback multiGroupCountCallback = new GroupCountCallback(column1);
        multiGroupCountCallback.data(0);
        Assert.assertEquals(new HashMap<Integer, Integer>() {{
            put(1, 1);
        }}, multiGroupCountCallback.getCounters());
    }

    @Test
    public void nonEmptyMulti() {
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);
        ((FastIntData) column1.data).add(1);
        ((FastIntData) column1.data).add(5);

        GroupCountCallback multiGroupCountCallback = new GroupCountCallback(column1);
        multiGroupCountCallback.data(0);
        multiGroupCountCallback.data(1);
        multiGroupCountCallback.data(1);
        multiGroupCountCallback.data(1);
        Assert.assertEquals(new HashMap<Integer, Integer>() {{
            put(1, 1);
            put(5, 3);
        }}, multiGroupCountCallback.getCounters());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void throwExceptionIfNotExistentPosition() {
        FastSelect.Column column1 = new FastSelect.Column("1", int.class);

        GroupCountCallback groupCountCallback = new GroupCountCallback(column1);
        groupCountCallback.data(66);
    }

}
