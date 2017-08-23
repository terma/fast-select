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

package com.github.terma.fastselect.utils;

import junit.framework.Assert;
import org.junit.Test;

public class BlockRoundValueTest {

    @Test
    public void nextCorrectWorkInBlock() {
        BlockRoundValue value = new BlockRoundValue(2, 100, 1000);

        Assert.assertEquals(0, value.next());
        Assert.assertEquals(1, value.next());
        Assert.assertEquals(0, value.next());
        Assert.assertEquals(1, value.next());
        Assert.assertEquals(0, value.next());
    }

    @Test
    public void nextCorrectWorkBetweenBlocks() {
        BlockRoundValue value = new BlockRoundValue(2, 2, 1000);

        Assert.assertEquals(0, value.next());
        Assert.assertEquals(1, value.next());
        Assert.assertEquals(2, value.next());
        Assert.assertEquals(3, value.next());
        Assert.assertEquals(4, value.next());
        Assert.assertEquals(5, value.next());
    }

    @Test
    public void nextCorrectCheckMax() {
        BlockRoundValue value = new BlockRoundValue(2, 2, 4);

        Assert.assertEquals(0, value.next());
        Assert.assertEquals(1, value.next());
        Assert.assertEquals(2, value.next());
        Assert.assertEquals(3, value.next());
        Assert.assertEquals(0, value.next());
        Assert.assertEquals(1, value.next());
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenCreateWithCardinalityLessThanBlockCardinality() {
        new BlockRoundValue(5, 2, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsWhenCreateWithZeroCardinality() {
        new BlockRoundValue(0, 2, 0);
    }

}
