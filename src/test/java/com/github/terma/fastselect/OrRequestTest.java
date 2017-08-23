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

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class OrRequestTest {

    private Request request1 = Mockito.mock(Request.class);
    private Request request2 = Mockito.mock(Request.class);
    private Request request3 = Mockito.mock(Request.class);

    @Test
    public void createWithNoneRequests() {
        new OrRequest();
    }

    @Test
    public void provideToStringForEmpty() {
        Assert.assertEquals("OrRequest []", new OrRequest().toString());
    }

    @Test
    public void provideToString() {
        Mockito.when(request1.toString()).thenReturn("Blue Oyster Cult");
        Mockito.when(request2.toString()).thenReturn("Oasis");

        Assert.assertEquals("OrRequest [Blue Oyster Cult, Oasis]",
                new OrRequest(request1, request2).toString());
    }

    @Test
    public void checkBlockUpToFirstTrue() {
        Block block = Mockito.mock(Block.class);

        Mockito.when(request1.checkBlock(block)).thenReturn(false);
        Mockito.when(request2.checkBlock(block)).thenReturn(true);
        Mockito.when(request3.checkBlock(block)).thenReturn(true);

        Assert.assertTrue(new OrRequest(request1, request2, request3).checkBlock(block));

        Mockito.verify(request1).checkBlock(block);
        Mockito.verify(request2).checkBlock(block);
        Mockito.verifyZeroInteractions(request3);
    }

    @Test
    public void checkBlockFalseIfAllFalse() {
        Block block = Mockito.mock(Block.class);

        Mockito.when(request1.checkBlock(block)).thenReturn(false);
        Mockito.when(request2.checkBlock(block)).thenReturn(false);

        Assert.assertFalse(new OrRequest(request1, request2).checkBlock(block));
    }

    @Test
    public void checkBlockFalseForEmpty() {
        Block block = Mockito.mock(Block.class);
        Assert.assertFalse(new OrRequest().checkBlock(block));
    }

    @Test
    public void checkValueUpToFirstTrue() {
        final int position = 45;

        Mockito.when(request1.checkValue(position)).thenReturn(false);
        Mockito.when(request2.checkValue(position)).thenReturn(true);
        Mockito.when(request3.checkValue(position)).thenReturn(true);

        Assert.assertTrue(new OrRequest(request1, request2, request3).checkValue(position));

        Mockito.verify(request1).checkValue(position);
        Mockito.verify(request2).checkValue(position);
        Mockito.verifyZeroInteractions(request3);
    }

    @Test
    public void checkValueFalseIfAllFalse() {
        final int position = 123124;

        Mockito.when(request1.checkValue(position)).thenReturn(false);
        Mockito.when(request2.checkValue(position)).thenReturn(false);

        Assert.assertFalse(new OrRequest(request1, request2).checkValue(position));

        Mockito.verify(request1).checkValue(position);
        Mockito.verify(request2).checkValue(position);
    }

    @Test
    public void checkValueForEmptyFalse() {
        final int position = 123124;

        Assert.assertFalse(new OrRequest().checkValue(position));
    }

}
