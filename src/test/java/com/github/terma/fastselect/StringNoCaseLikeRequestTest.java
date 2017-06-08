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

public class StringNoCaseLikeRequestTest {

    private StringData data;
    private ColumnRequest request;

    @Before
    public void init() {
        request = new StringNoCaseLikeRequest("x", "AA");
        FastSelect.Column column = new FastSelect.Column("x", String.class, 1000);
        data = (StringData) column.data;
        Map<String, FastSelect.Column> columnsByNames = new HashMap<>();
        columnsByNames.put("x", column);

        request.prepare(columnsByNames);
    }

    @Test
    public void acceptSameValue() {
        data.add("AA");

        Assert.assertTrue(request.checkValue(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantBeUsedToSearchNull() {
        new StringNoCaseLikeRequest("x", null);
    }

    @Test
    public void acceptSubstringValue() {
        data.add("AAA");

        Assert.assertTrue(request.checkValue(0));
    }

    @Test
    public void acceptDifferentCase() {
        data.add("Aa");

        Assert.assertTrue(request.checkValue(0));
    }

    @Test
    public void notAcceptNotSubstring() {
        data.add("G");

        Assert.assertFalse(request.checkValue(0));
    }

    @Test
    public void alwaysAcceptInBlock() {
        Assert.assertTrue(request.checkBlock(null));
    }

    @Test
    public void provideUsefulToString() {
        Assert.assertEquals("StringNoCaseLikeRequest {name: x, like: aa}", request.toString());
    }


    @Test
    public void provideEqualsAndHashCode() {
        StringNoCaseLikeRequest r = new StringNoCaseLikeRequest("col", "valLike");
        Assert.assertEquals(r, r);
        Assert.assertEquals(r.hashCode(), r.hashCode());

        StringNoCaseLikeRequest r1 = new StringNoCaseLikeRequest("col", "valLike");
        StringNoCaseLikeRequest r2 = new StringNoCaseLikeRequest("col", "valLike");
        Assert.assertEquals(r1, r2);
        Assert.assertEquals(r2, r1);
        Assert.assertEquals(r1.hashCode(), r2.hashCode());

        Assert.assertFalse(new StringNoCaseLikeRequest("col", "VALlike")
                .equals(new StringNoCaseLikeRequest("col", "")));
        Assert.assertFalse(new StringNoCaseLikeRequest("col", "valLike")
                .equals(new StringNoCaseLikeRequest("a", "valLike")));
        Assert.assertFalse(new StringNoCaseLikeRequest("col", "valLike").hashCode() ==
                new StringNoCaseLikeRequest("a", "valLike").hashCode());
    }

}
