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

import java.util.ArrayList;
import java.util.List;

public class SpecialRandomTest {

    @Test
    public void shouldGenerateNextWithExpectedDeviationInOnePortion() {
        final int deviation = 2;

        SpecialRandom specialRandom = new SpecialRandom(deviation, 10, 5);

        List<Integer> result = new ArrayList<>();
        result.add(specialRandom.next());
        result.add(specialRandom.next());
        result.add(specialRandom.next());
        result.add(specialRandom.next());
        result.add(specialRandom.next());

        int first = result.get(0);
        for (int i = 1; i < result.size(); ++i) {
            Assert.assertTrue(Math.abs(result.get(i) - first) <= deviation);
        }
    }

    @Test
    public void shouldGenerateNext() {
        final int deviation = 2;
        final int portion = 2;

        SpecialRandom specialRandom = new SpecialRandom(deviation, portion, 5);

        List<Integer> result = new ArrayList<>();
        result.add(specialRandom.next());
        result.add(specialRandom.next());
        result.add(specialRandom.next());

        int first = result.get(0);
        int firstInNextPortion = result.get(2);
        Assert.assertTrue(firstInNextPortion > first + portion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoSpaceForTwoDistinctPortions() {
        new SpecialRandom(2, 2, 3);
    }

}
