/*
Copyright 2015-2017 Artem Stasiuk

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

import com.github.terma.fastselect.data.DoubleData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class DoubleBetweenRequestTest {

    private DoubleData data;
    private ColumnRequest request;

    @Before
    public void init() {
        request = new DoubleBetweenRequest("x", 1, 10);
        final FastSelect.Column column = new FastSelect.Column("x", double.class, 100);
        data = (DoubleData) column.data;
        request.column = column;
        request.prepare(new HashMap<String, FastSelect.Column>() {{
            put("x", column);
        }});
    }

    @Test
    public void acceptValuesInRange() {
        data.add(5);
        data.add(6);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertTrue(request.checkValue(1));
    }

    @Test
    public void acceptValuesWithFractionInRange() {
        data.add(5.000123);
        data.add(6.0000333);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertTrue(request.checkValue(1));
    }

    @Test
    public void declineValuesOutOfRange() {
        data.add(-90);
        data.add(1000);

        Assert.assertFalse(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void acceptValuesOnBorders() {
        data.add(1);
        data.add(10);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertTrue(request.checkValue(1));
    }

    @Test
    public void acceptInBlockIfRangeInBetween() {
        Range range = new Range();
        range.min = 2;
        range.max = 4;

        Assert.assertTrue(request.checkBlock(new BlockMock(range)));
    }

    @Test
    public void acceptInBlockIfRangeCoverBetween() {
        Range range = new Range();
        range.min = 0;
        range.max = 11;

        Assert.assertTrue(request.checkBlock(new BlockMock(range)));
    }

    @Test
    public void acceptInBlockIfRangeCoverMin() {
        Range range = new Range();
        range.min = -90;
        range.max = 1;

        Assert.assertTrue(request.checkBlock(new BlockMock(range)));
    }

    @Test
    public void alwaysAcceptBlock() {
        Assert.assertTrue(request.checkBlock(null));
    }

    @Test
    public void provideToString() {
        Assert.assertEquals("{name: 'col', min: -12.12, max: 8.222200001E7}",
                new DoubleBetweenRequest("col", -12.12, 82222000.01).toString());
    }

}
