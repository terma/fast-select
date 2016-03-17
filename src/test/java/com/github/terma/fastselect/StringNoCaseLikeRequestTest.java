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

import java.util.BitSet;

public class StringNoCaseLikeRequestTest {

    private StringData data;
    private AbstractRequest request;

    @Before
    public void init() {
        request = new StringNoCaseLikeRequest("x", "AA");
        FastSelect.Column column = new FastSelect.Column("x", String.class, 1000);
        data = (StringData) column.data;
        request.column = column;
        request.prepare();
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
        Assert.assertTrue(request.inBlock((BitSet) null));
        Assert.assertTrue(request.inBlock(new BitSet()));
    }

    @Test
    public void provideUsefulToString() {
        Assert.assertEquals("StringNoCaseLikeRequest {name: x, like: aa}", request.toString());
    }

}
