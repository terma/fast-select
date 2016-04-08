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

import com.github.terma.fastselect.data.StringData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StringRequestTest {

    private Map<String, FastSelect.Column> columnsByNames = new HashMap<>();
    private StringData data;
    private ColumnRequest request;

    @Before
    public void init() {
        request = new StringRequest("x", "AA");
        FastSelect.Column column = new FastSelect.Column("x", String.class, 1000);
        data = (StringData) column.data;
        columnsByNames.put("x", column);
        request.prepare(columnsByNames);
    }

    @Test
    public void checkBlockAlwaysTrue() {
        Assert.assertTrue(request.checkBlock(null));
    }

    @Test
    public void acceptSameValue() {
        data.add("AA");
        Assert.assertTrue(request.checkValue(0));
    }

    @Test
    public void supportCheckValueNullAsEmpty() {
        request = new StringRequest("x", "");
        request.prepare(columnsByNames);

        data.add(null);
        data.add("A");
        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test(expected = NullPointerException.class)
    public void throwExceptionWhenRequestByNull() {
        new StringRequest("x", null);
    }

    @Test
    public void supportCheckValueEmptyAsEmpty() {
        request = new StringRequest("x", "");
        request.prepare(columnsByNames);

        data.add("");
        data.add("A");
        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void notAcceptSubstringValue() {
        data.add("AAA");
        Assert.assertFalse(request.checkValue(0));
    }

    @Test
    public void notAcceptNotSubstring() {
        data.add("G");
        Assert.assertFalse(request.checkValue(0));
    }

    @Test
    public void notAcceptDifferentCase() {
        data.add("aa");
        Assert.assertFalse(request.checkValue(0));
    }

    @Test
    public void provideToString() {
        Assert.assertEquals("col = 'valLike'", new StringRequest("col", "valLike").toString());
    }

}
