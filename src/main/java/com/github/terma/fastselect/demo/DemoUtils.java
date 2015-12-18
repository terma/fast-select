/*
Copyright 2015 Artem Stasiuk

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

package com.github.terma.fastselect.demo;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.MultiRequest;
import com.github.terma.fastselect.utils.MemMeter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DemoUtils {

    public static final int G_MAX = 100;
    public static final int R_MAX = 7;
    public static final int C_MAX = 7;
    public static final int O_MAX = 3;
    public static final int S_MAX = 100;
    public static final int D_MAX = 100;

    public static FastSelect<DemoData> createFastSelect(int blockSize, int itemsToCreate) {
        System.out.println("Filler started");

        final MemMeter memMeter = new MemMeter();
        final long start = System.currentTimeMillis();

        FastSelect<DemoData> database = new FastSelect<>(blockSize, DemoData.class, Arrays.asList(
                new FastSelect.Column("r", byte.class),
                new FastSelect.Column("g", byte.class),
                new FastSelect.Column("s", byte.class),
                new FastSelect.Column("o", byte.class),
                new FastSelect.Column("c", byte.class),
                new FastSelect.Column("m", byte.class),
                new FastSelect.Column("d", short.class),
                new FastSelect.Column("uid1", long.class),
                new FastSelect.Column("uid2", long.class)
        ));

        final List<DemoData> data = new ArrayList<>();
        int count = 0;

        opa:
        while (true) {
            for (int r = 1; r < R_MAX; r++) {
                for (int g = 1; g < G_MAX; g++) {
                    for (int s = 1; s < S_MAX; s++) {
                        for (int o = 1; o < O_MAX; o++) {
                            for (int c = 1; c < C_MAX; c++) {
                                for (int d = 1; d < D_MAX; d++) {
                                    if (count >= itemsToCreate) break opa;

                                    DemoData item = new DemoData();
                                    item.r = (byte) r;
                                    item.g = (byte) g;
                                    item.s = (byte) s;
                                    item.o = (byte) o;
                                    item.c = (byte) c;
                                    item.m = (byte) c;
                                    item.d = (short) d;
                                    data.add(item);
                                    count++;

                                    if (count % 10000 == 0) {
                                        database.addAll(data);
                                        data.clear();
                                        System.out.print(".");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        database.addAll(data);
        data.clear();

        System.out.println();

        final long time = System.currentTimeMillis() - start;

        System.out.println("FastSelect prepared, volume: " + itemsToCreate + ", mem used: "
                + memMeter.getUsedMb() + "Mb, preparation time " + time + " msec");
        System.out.println("Filler finished");

        return database;
    }

    public static MultiRequest[] createWhere() {
        return new MultiRequest[]{
                new MultiRequest("g", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}),
                new MultiRequest("r", new int[]{1, 2, 3, 4, 5, 6}),
                new MultiRequest("c", new int[]{1, 2, 3, 4}),
                new MultiRequest("s", new int[]{1, 19, 18, 17, 16, 15, 14, 13, 12, 11, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}),
                new MultiRequest("d", new int[]{1, 90, 99, 5, 34, 22, 26, 8, 5, 6, 7, 5, 6, 34, 35, 36, 37, 38, 39, 21, 70, 71, 74, 76, 78, 79, 10, 11, 22, 33, 44, 55, 66})
        };
    }
}
