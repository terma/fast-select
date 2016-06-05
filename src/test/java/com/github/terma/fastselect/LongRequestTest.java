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

import com.github.terma.fastselect.data.LongData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class LongRequestTest {

    private LongData data;
    private FastSelect.Column column;

    private static LongRequest createRequest(FastSelect.Column column, long... values) {
        LongRequest request = new LongRequest("x", values);
        request.column = column;

        Map<String, FastSelect.Column> columnsByNames = new HashMap<>();
        columnsByNames.put("x", column);

        request.prepare(columnsByNames);
        return request;
    }

    @Before
    public void init() {
        column = new FastSelect.Column("x", long.class, 100);
        data = (LongData) column.data;
    }

    @Test
    public void acceptValues() {
        data.add(5);
        data.add(6);

        LongRequest longRequest = createRequest(column, 5);

        Assert.assertTrue(longRequest.checkValue(0));
        Assert.assertFalse(longRequest.checkValue(1));
    }

    @Test
    public void acceptByOrMultipleValues() {
        data.add(5);
        data.add(6);

        LongRequest longRequest = createRequest(column, 5, 6, 7);

        Assert.assertTrue(longRequest.checkValue(0));
        Assert.assertTrue(longRequest.checkValue(1));
    }

    @Test
    public void acceptAgeValues() {
        data.add(Long.MIN_VALUE);
        data.add(Long.MAX_VALUE);

        LongRequest longRequest = createRequest(column, Long.MAX_VALUE, Long.MIN_VALUE);

        Assert.assertTrue(longRequest.checkValue(0));
        Assert.assertTrue(longRequest.checkValue(1));
    }

    @Test
    public void acceptIfInRange() {
        LongRequest longRequest = createRequest(column, 19000, Long.MAX_VALUE);

        Range range = new Range(Long.MIN_VALUE, Long.MAX_VALUE);
        Assert.assertTrue(longRequest.checkBlock(new BlockMock(range)));
    }

    @Test
    public void acceptIfLeftValueInRange() {
        LongRequest longRequest = createRequest(column, 1, 20);

        Range range = new Range(20, Long.MAX_VALUE);
        Assert.assertTrue(longRequest.checkBlock(new BlockMock(range)));
    }

    @Test
    public void acceptIfRightValueInRange() {
        LongRequest longRequest = createRequest(column, -1, 20);

        Range range = new Range(-90, -1);
        Assert.assertTrue(longRequest.checkBlock(new BlockMock(range)));
    }

    @Test
    public void notAcceptIfOutOfRangeLeft() {
        LongRequest longRequest = createRequest(column, 1, 20);

        Range range = new Range(-90, 0);
        Assert.assertFalse(longRequest.checkBlock(new BlockMock(range)));
    }

    @Test
    public void notAcceptIfOutOfRangeRight() {
        LongRequest longRequest = createRequest(column, -2, -1);

        Range range = new Range(0, 12);
        Assert.assertFalse(longRequest.checkBlock(new BlockMock(range)));
    }

    @Test
    public void provideToString() {
        LongRequest longRequest = createRequest(column, -2, -1);

        Assert.assertEquals("{name: x, values: [-2, -1]}", longRequest.toString());
    }

}
