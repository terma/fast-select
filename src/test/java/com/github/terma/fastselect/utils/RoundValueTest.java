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

package com.github.terma.fastselect.utils;

import junit.framework.Assert;
import org.junit.Test;

public class RoundValueTest {

    @Test
    public void alwaysStartFromZero() {
        Assert.assertEquals(0, new RoundValue(11).next());
        Assert.assertEquals(0, new RoundValue(11).next());
        Assert.assertEquals(0, new RoundValue(11).next());
    }

    @Test
    public void incrementWhenNext() {
        RoundValue roundValue = new RoundValue(11);
        Assert.assertEquals(0, roundValue.next());
        Assert.assertEquals(1, roundValue.next());
        Assert.assertEquals(2, roundValue.next());
    }

    @Test
    public void nextAfterMaxWillBeZero() {
        RoundValue roundValue = new RoundValue(2);
        Assert.assertEquals(0, roundValue.next());
        Assert.assertEquals(1, roundValue.next());
        Assert.assertEquals(2, roundValue.next());
        Assert.assertEquals(0, roundValue.next());
    }

    @Test
    public void zeroOrNegativeMaxAlwaysProduceZero() {
        RoundValue roundValue = new RoundValue(0);
        Assert.assertEquals(0, roundValue.next());
        Assert.assertEquals(0, roundValue.next());
    }

}
