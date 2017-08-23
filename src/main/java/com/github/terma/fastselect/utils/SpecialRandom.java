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
