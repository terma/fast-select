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

import com.github.terma.fastselect.data.MultiShortData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MultiShortRequestTest {

    private MultiShortData data;
    private FastSelect.Column column;

    private static MultiShortRequest createRequest(FastSelect.Column column, short... values) {
        MultiShortRequest request = new MultiShortRequest("x", values);
        request.column = column;
        Map<String, FastSelect.Column> columnsByNames = new HashMap<>();
        columnsByNames.put("x", column);

        request.prepare(columnsByNames);
        return request;
    }

    @Before
    public void init() {
        column = new FastSelect.Column("x", short[].class, 100);
        data = (MultiShortData) column.data;
    }

    @Test
    public void acceptValues() {
        data.add(new short[]{Short.MAX_VALUE});
        data.add(new short[]{6});

        MultiShortRequest request = createRequest(column, Short.MAX_VALUE);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptValuesWhenArraysAreEquals() {
        data.add(new short[]{Short.MIN_VALUE, 10});
        data.add(new short[]{6});

        MultiShortRequest request = createRequest(column, Short.MIN_VALUE, (short) 10);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptOnlyWhenAtLeastOneElementEquals() {
        data.add(new short[]{Short.MIN_VALUE, 10});
        data.add(new short[]{6});

        MultiShortRequest request = createRequest(column, Short.MIN_VALUE, (short) -1);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void nothingAcceptIfNoRequestValues() {
        data.add(new short[]{Short.MIN_VALUE, 10});
        data.add(new short[]{6});

        MultiShortRequest request = createRequest(column);

        Assert.assertFalse(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void checkBlocksAlwaysTrue() {
        MultiShortRequest request = createRequest(column);

        Assert.assertTrue(request.checkBlock(null));
    }

    @Test
    public void provideToString() {
        MultiShortRequest request = createRequest(column, (short) -22000, (short) 22001);

        Assert.assertEquals("MultiShortRequest {name: 'x', values: [-22000, 22001]}", request.toString());
    }

}
