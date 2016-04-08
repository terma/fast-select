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

import java.util.HashMap;
import java.util.Map;

public class ByteRequestTest {

    private ByteData data;
    private FastSelect.Column column;

    private static ByteRequest createRequest(FastSelect.Column column, int... values) {
        ByteRequest request = new ByteRequest("x", values);
        Map<String, FastSelect.Column> columnsByNames = new HashMap<>();
        columnsByNames.put("x", column);

        request.prepare(columnsByNames);
        return request;
    }

    @Before
    public void init() {
        column = new FastSelect.Column("x", byte.class, 100);
        data = (ByteData) column.data;
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkValueDontSupportNegativeValues() {
        data.add(Byte.MIN_VALUE);
        data.add((byte) 0);

        ByteRequest request = createRequest(column, Byte.MIN_VALUE);

        Assert.assertTrue(request.checkValue(0));
        Assert.assertFalse(request.checkValue(1));
    }

    @Test
    public void provideToString() {
        Assert.assertEquals("ByteRequest {name: 'x', values: [0, 127]}",
                createRequest(column, 0, Byte.MAX_VALUE).toString());
        Assert.assertEquals("ByteRequest {name: 'x', values: [0]}", createRequest(column, 0).toString());
        Assert.assertEquals("ByteRequest {name: 'x', values: []}", createRequest(column).toString());
    }

}
