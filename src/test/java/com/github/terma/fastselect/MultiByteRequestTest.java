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

import com.github.terma.fastselect.data.MultiByteData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.BitSet;

public class MultiByteRequestTest {

    private MultiByteData data;
    private FastSelect.Column column;

    private static MultiByteRequest createRequest(FastSelect.Column column, byte... values) {
        MultiByteRequest request = new MultiByteRequest("x", values);
        request.column = column;
        request.prepare();
        return request;
    }

    @Before
    public void init() {
        column = new FastSelect.Column("x", byte[].class, 100);
        data = (MultiByteData) column.data;
    }

    @Test
    public void acceptValues() {
        data.add(new byte[]{Byte.MAX_VALUE});
        data.add(new byte[]{6});

        MultiByteRequest request = createRequest(column, Byte.MAX_VALUE);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptValuesWhenArraysAreEquals() {
        data.add(new byte[]{Byte.MIN_VALUE, 10});
        data.add(new byte[]{6});

        MultiByteRequest request = createRequest(column, Byte.MIN_VALUE, (byte) 10);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptOnlyWhenAtLeastOneElementEquals() {
        data.add(new byte[]{Byte.MIN_VALUE, 10});
        data.add(new byte[]{6});

        MultiByteRequest request = createRequest(column, Byte.MIN_VALUE, (byte) -1);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void nothingAcceptIfNoRequestValues() {
        data.add(new byte[]{Byte.MIN_VALUE, 10});
        data.add(new byte[]{6});

        MultiByteRequest request = createRequest(column);

        Assert.assertFalse(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void inBlocksAlwaysTrue() {
        MultiByteRequest request = createRequest(column);

        Assert.assertTrue(request.checkBlock((BitSet) null));
        Assert.assertTrue(request.checkBlock(new BitSet()));
    }

    @Test
    public void inRangeAlwaysTrue() {
        MultiByteRequest request = createRequest(column);

        Assert.assertTrue(request.checkBlock((Range) null));
        Assert.assertTrue(request.checkBlock(new Range()));
    }

    @Test
    public void provideToString() {
        MultiByteRequest request = createRequest(column, Byte.MIN_VALUE, Byte.MAX_VALUE);

        Assert.assertEquals("MultiByteRequest {name: 'x', values: [-128, 127]}", request.toString());
    }

}
