/*
Copyright 2015-2016 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.fastselect;

import com.github.terma.fastselect.data.IntData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class IntRequestTest {

    private IntData data;
    private FastSelect.Column column;

    private static IntRequest createRequest(FastSelect.Column column, int... values) {
        IntRequest request = new IntRequest("x", values);
        Map<String, FastSelect.Column> columnsByNames = new HashMap<>();
        columnsByNames.put("x", column);

        request.prepare(columnsByNames);
        return request;
    }

    @Before
    public void init() {
        column = new FastSelect.Column("x", int.class, 100);
        data = (IntData) column.data;
    }

    @Test
    public void checkValueNegative() {
        data.add(Integer.MIN_VALUE);
        data.add(-1);

        Assert.assertTrue(createRequest(column, Integer.MIN_VALUE).checkValue(0));
        Assert.assertTrue(createRequest(column, -1).checkValue(1));
        Assert.assertFalse(createRequest(column, 0).checkValue(0));
    }

    @Test
    public void checkValueZero() {
        data.add(0);

        Assert.assertFalse(createRequest(column, -1).checkValue(0));
        Assert.assertTrue(createRequest(column, 0).checkValue(0));
        Assert.assertFalse(createRequest(column, 1).checkValue(0));
    }

    @Test
    public void checkValuePositive() {
        data.add(1);
        data.add(Integer.MAX_VALUE);

        Assert.assertFalse(createRequest(column, Integer.MIN_VALUE).checkValue(0));
        Assert.assertTrue(createRequest(column, 1).checkValue(0));
        Assert.assertTrue(createRequest(column, Integer.MAX_VALUE).checkValue(1));
    }

    @Test
    public void provideToString() {
        Assert.assertEquals("IntRequest {name: 'x', values: [0, 2147483647]}",
                createRequest(column, 0, Integer.MAX_VALUE).toString());
        Assert.assertEquals("IntRequest {name: 'x', values: [0]}", createRequest(column, 0).toString());
        Assert.assertEquals("IntRequest {name: 'x', values: []}", createRequest(column).toString());
    }

}
