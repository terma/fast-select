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

import com.github.terma.fastselect.data.ByteData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class ByteBetweenRequestTest {

    private ByteData data;
    private AbstractRequest request;

    @Before
    public void init() {
        request = new ByteBetweenRequest("x", (byte) 1, (byte) 10);
        FastSelect.Column column = new FastSelect.Column("x", byte.class, 1000);
        data = (ByteData) column.data;
        request.column = column;
        request.prepare();
    }

    @Test
    public void acceptValuesInRange() {
        data.add((byte) -90);
        data.add((byte) 5);
        data.add((byte) 6);
        data.add((byte) 1000);

        Assert.assertTrue(request.checkValue(1));
        Assert.assertTrue(request.checkValue(2));
    }

    @Test
    public void declineValuesOutOfRange() {
        data.add((byte) -90);
        data.add((byte) 1000);

        Assert.assertFalse(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptValuesOnBorders() {
        data.add((byte) 1);
        data.add((byte) 10);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertTrue(request.checkValue(1));
    }

    @Test
    public void acceptInBlockIfRangeInBetween() {
        Range range = new Range();
        range.min = 2;
        range.max = 4;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void acceptInBlockIfRangeCoverBetween() {
        Range range = new Range();
        range.min = 0;
        range.max = 11;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void acceptInBlockIfRangeCoverMin() {
        Range range = new Range();
        range.min = -90;
        range.max = 1;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void acceptInBlockIfRangeCoverMax() {
        Range range = new Range();
        range.min = 10;
        range.max = 100;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void notAcceptInBlockIfRangeLeftFrom() {
        Range range = new Range();
        range.min = -90;
        range.max = 0;

        Assert.assertFalse(request.inBlock(range));
    }

    @Test
    public void notAcceptInBlockIfRangeRightFrom() {
        Range range = new Range();
        range.min = 11;
        range.max = 1000;

        Assert.assertFalse(request.inBlock(range));
    }

}
