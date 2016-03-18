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

import com.github.terma.fastselect.data.MultiLongData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

public class MultiLongRequestTest {

    private MultiLongData data;
    private FastSelect.Column column;

    private static MultiLongRequest createRequest(FastSelect.Column column, long... values) {
        MultiLongRequest request = new MultiLongRequest("x", values);
        request.column = column;
        request.prepare();
        return request;
    }

    @Before
    public void init() {
        column = new FastSelect.Column("x", long[].class, 100);
        data = (MultiLongData) column.data;
    }

    @Test
    public void acceptValues() {
        data.add(new long[]{Long.MAX_VALUE});
        data.add(new long[]{6});

        MultiLongRequest request = createRequest(column, Long.MAX_VALUE);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptValuesWhenArraysAreEquals() {
        data.add(new long[]{Long.MIN_VALUE, 10});
        data.add(new long[]{6});

        MultiLongRequest request = createRequest(column, Long.MIN_VALUE, 10);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptOnlyWhenAtLeastOneElementEquals() {
        data.add(new long[]{Long.MIN_VALUE, 10});
        data.add(new long[]{6});

        MultiLongRequest request = createRequest(column, Long.MIN_VALUE, -1);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void nothingAcceptIfNoRequestValues() {
        data.add(new long[]{Short.MIN_VALUE, 10});
        data.add(new long[]{6});

        MultiLongRequest request = createRequest(column);

        Assert.assertFalse(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void inBlocksAlwaysTrue() {
        MultiLongRequest request = createRequest(column);

        Assert.assertTrue(request.checkBlock((BitSet) null));
        Assert.assertTrue(request.checkBlock(new BitSet()));
    }

    @Test
    public void inRangeAlwaysTrue() {
        MultiLongRequest request = createRequest(column);

        Assert.assertTrue(request.checkBlock((Range) null));
        Assert.assertTrue(request.checkBlock(new Range()));
    }

    @Test
    public void provideToString() {
        MultiLongRequest request = createRequest(column, Long.MIN_VALUE, Long.MAX_VALUE);

        Assert.assertEquals("MultiLongRequest {name: 'x', values: [-9223372036854775808, 9223372036854775807]}", request.toString());
    }

}
