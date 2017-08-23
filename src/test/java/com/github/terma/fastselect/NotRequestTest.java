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

import java.util.HashMap;
import java.util.Map;

public class NotRequestTest {

    private Request request = Mockito.mock(Request.class);

    @Test
    public void provideToString() {
        Mockito.when(request.toString()).thenReturn("x = 1");
        Assert.assertEquals("not x = 1", new NotRequest(request).toString());
    }

    /**
     * For clarification why <code>NotRequest</code> doesn't use statistic goto {@link NotRequest}
     */
    @Test
    public void checkBlockAlwaysReturnTrue() {
        Block block = Mockito.mock(Block.class);

        NotRequest notRequest = new NotRequest(request);

        Assert.assertTrue(notRequest.checkBlock(block));
        Mockito.verifyZeroInteractions(request);

        Assert.assertTrue(notRequest.checkBlock(block));
        Mockito.verifyZeroInteractions(request);
    }

    @Test
    public void checkValueReturnNotResult() {
        NotRequest notRequest = new NotRequest(request);

        Mockito.when(notRequest.checkValue(Mockito.anyInt())).thenReturn(false);

        Assert.assertTrue(notRequest.checkValue(12));
        Mockito.verify(request).checkValue(12);
        Mockito.reset(request);

        Mockito.when(notRequest.checkValue(100)).thenReturn(true);
        Assert.assertFalse(notRequest.checkValue(100));
        Mockito.verify(request).checkValue(100);
        Mockito.verifyNoMoreInteractions(request);
    }

    @Test
    public void callPrepareForOriginalRequest() {
        NotRequest notRequest = new NotRequest(request);

        Map<String, FastSelect.Column> map = new HashMap<>();
        notRequest.prepare(map);

        Mockito.verify(request).prepare(map);
    }

}
