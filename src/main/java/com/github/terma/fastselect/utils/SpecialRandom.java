package com.github.terma.fastselect.utils;

import java.util.Random;

public class SpecialRandom {

    private final Random random = new Random();
    private final int deviation;
    private final int portion;
    private final int max;

    private int base;
    private int counter;

    public SpecialRandom(int deviation, int portion, int max) {
        if (max / deviation < 2) throw new IllegalArgumentException(
                "Need to have at least 2 portion with distinct deviations!");

        this.deviation = deviation;
        this.portion = portion;
        this.max = max;
    }

    public int next() {
        int next = base + random.nextInt(deviation) + 1;
        counter++;

        if (counter >= portion) {
            counter = 0;

            final int oldBase = base;
            do {
                base = random.nextInt(max - base + 1);
            } while (base + deviation >= oldBase && base <= oldBase + deviation);
        }

        return next;
    }

}
