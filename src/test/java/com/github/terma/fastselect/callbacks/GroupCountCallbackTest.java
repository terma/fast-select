package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.ArrayLayoutFastSelect;
import junit.framework.Assert;
import org.junit.Test;

import java.util.HashMap;

public class GroupCountCallbackTest {

    @Test
    public void empty() {
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);
        GroupCountCallback groupCountCallback = new GroupCountCallback(column1);
        Assert.assertEquals(new HashMap<>(), groupCountCallback.getCounters());
    }

    @Test
    public void nonEmpty() {
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);
        ((ArrayLayoutFastSelect.FastIntList) column1.data).add(1);

        GroupCountCallback multiGroupCountCallback = new GroupCountCallback(column1);
        multiGroupCountCallback.data(0);
        Assert.assertEquals(new HashMap<Integer, Integer>() {{
            put(1, 1);
        }}, multiGroupCountCallback.getCounters());
    }

    @Test
    public void nonEmptyMulti() {
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);
        ((ArrayLayoutFastSelect.FastIntList) column1.data).add(1);
        ((ArrayLayoutFastSelect.FastIntList) column1.data).add(5);

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
        ArrayLayoutFastSelect.Column column1 = new ArrayLayoutFastSelect.Column("1", int.class);

        GroupCountCallback groupCountCallback = new GroupCountCallback(column1);
        groupCountCallback.data(66);
    }

}
