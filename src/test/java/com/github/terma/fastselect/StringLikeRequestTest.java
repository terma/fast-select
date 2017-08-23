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

import com.github.terma.fastselect.data.StringData;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StringLikeRequestTest {

    private StringData data;
    private ColumnRequest request;

    @Before
    public void init() {
        request = new StringLikeRequest("x", "AA");
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

    @Test
    public void acceptSubstringValue() {
        data.add("AAA");

        Assert.assertTrue(request.checkValue(0));
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
        Assert.assertEquals("StringLikeRequest {name: 'col', like: 'valLike'}", new StringLikeRequest("col", "valLike").toString());
    }

    @Test
    public void provideEqualsAndHashCode() {
        StringLikeRequest r = new StringLikeRequest("col", "valLike");
        Assert.assertEquals(r, r);
        Assert.assertEquals(r.hashCode(), r.hashCode());

        StringLikeRequest r1 = new StringLikeRequest("col", "valLike");
        StringLikeRequest r2 = new StringLikeRequest("col", "valLike");
        Assert.assertEquals(r1, r2);
        Assert.assertEquals(r2, r1);
        Assert.assertEquals(r1.hashCode(), r2.hashCode());

        Assert.assertFalse(new StringLikeRequest("col", "valLike")
                .equals(new StringLikeRequest("col", "")));
        Assert.assertFalse(new StringLikeRequest("col", "valLike")
                .equals(new StringLikeRequest("a", "valLike")));
        Assert.assertFalse(new StringLikeRequest("col", "valLike").hashCode() ==
                new StringLikeRequest("a", "valLike").hashCode());
    }

}
