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
