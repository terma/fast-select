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

package com.github.terma.fastselect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastSelectFiller {

    public static FastSelect<RealData> database;

    private final int blockSize;
    private final long itemsToCreate;
    private long time;

    public FastSelectFiller(int blockSize, int itemsToCreate) {
        this.blockSize = blockSize;
        this.itemsToCreate = itemsToCreate;
    }

    public void run() {
        System.out.println("Filler started");
        final long start = System.currentTimeMillis();

        database = new FastSelect<>(blockSize, RealData.class, Arrays.asList(
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

        final List<RealData> data = new ArrayList<>();
        int count = 0;

        opa:
        while (true) {
            for (int r = 0; r < CountBenchmark.R_MAX; r++) {
                for (int g = 0; g < CountBenchmark.G_MAX; g++) {
                    for (int s = 0; s < CountBenchmark.S_MAX; s++) {
                        for (int o = 0; o < CountBenchmark.O_MAX; o++) {
                            for (int c = 0; c < CountBenchmark.C_MAX; c++) {
                                for (int d = 0; d < CountBenchmark.D_MAX; d++) {
                                    if (count >= itemsToCreate) break opa;

                                    RealData item = new RealData();
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

        time += System.currentTimeMillis() - start;

        System.out.println("Volume: " + itemsToCreate + " average time " + time + " msec");
        System.out.println("Filler finished");
    }

    /**
     * Example layout
     */
    public static class RealData {

        public byte g;
        public byte r;
        public byte c;
        public byte m;
        public byte o;
        public byte s;
        public short d;
        public long uid1;
        public long uid2;

    }

}
