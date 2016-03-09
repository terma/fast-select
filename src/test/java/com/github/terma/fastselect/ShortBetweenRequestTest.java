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

import com.github.terma.fastselect.data.ShortData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class ShortBetweenRequestTest {

    private ShortData data;
    private AbstractRequest request;

    @Before
    public void init() {
        request = new ShortBetweenRequest("x", (short) 1, (short) 10);
        FastSelect.Column column = new FastSelect.Column("x", short.class);
        data = (ShortData) column.data;
        request.column = column;
        request.prepare();
    }

    @Test
    public void acceptValuesInRange() {
        data.add((short) -90);
        data.add((short) 5);
        data.add((short) 6);
        data.add((short) 1000);

        Assert.assertTrue(request.checkValue(1));
        Assert.assertTrue(request.checkValue(2));
    }

    @Test
    public void declineValuesOutOfRange() {
        data.add((short) -90);
        data.add((short) 1000);

        Assert.assertFalse(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptValuesOnBorders() {
        data.add((short) 1);
        data.add((short) 10);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertTrue(request.checkValue(1));
    }

    @Test
    public void acceptInBlockIfRangeInBetween() {
        IntRange range = new IntRange();
        range.min = 2;
        range.max = 4;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void acceptInBlockIfRangeCoverBetween() {
        IntRange range = new IntRange();
        range.min = 0;
        range.max = 11;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void acceptInBlockIfRangeCoverMin() {
        IntRange range = new IntRange();
        range.min = -90;
        range.max = 1;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void acceptInBlockIfRangeCoverMax() {
        IntRange range = new IntRange();
        range.min = 10;
        range.max = 100;

        Assert.assertTrue(request.inBlock(range));
    }

    @Test
    public void notAcceptInBlockIfRangeLeftFrom() {
        IntRange range = new IntRange();
        range.min = -90;
        range.max = 0;

        Assert.assertFalse(request.inBlock(range));
    }

    @Test
    public void notAcceptInBlockIfRangeRightFrom() {
        IntRange range = new IntRange();
        range.min = 11;
        range.max = 1000;

        Assert.assertFalse(request.inBlock(range));
    }

}
