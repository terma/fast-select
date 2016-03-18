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

public class ShortRequestTest {

    private ShortData data;
    private FastSelect.Column column;

    private static ShortRequest createRequest(FastSelect.Column column, short... values) {
        ShortRequest request = new ShortRequest("x", values);
        request.column = column;
        request.prepare();
        return request;
    }

    @Before
    public void init() {
        column = new FastSelect.Column("x", short.class, 100);
        data = (ShortData) column.data;
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkValueDontSupportNegativeValues() {
        data.add(Short.MIN_VALUE);
        data.add((short) 0);

        ShortRequest request = createRequest(column, Short.MIN_VALUE);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void provideToString() {
        Assert.assertEquals("ShortRequest {name: 'x', values: [0, 32767]}",
                createRequest(column, (short) 0, Short.MAX_VALUE).toString());
        Assert.assertEquals("ShortRequest {name: 'x', values: [0]}", createRequest(column, (short) 0).toString());
        Assert.assertEquals("ShortRequest {name: 'x', values: []}", createRequest(column).toString());
    }

}
