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
import java.util.List;

public class ObjectFastSelectFiller {

    public static ObjectFastSelect<SomeData> database;

    private final int blockSize;
    private final long itemsToCreate;
    private long time;

    public ObjectFastSelectFiller(int blockSize, int itemsToCreate) {
        this.blockSize = blockSize;
        this.itemsToCreate = itemsToCreate;
    }

    public void run() {
        System.out.println("Filler started");
        final long start = System.currentTimeMillis();

        database = new ObjectFastSelect(blockSize, SomeData.class, new String[]{"g", "r", "s", "c", "d", "o"});

        final List<SomeData> data = new ArrayList<>();
        int count = 0;

        opa:
        while (true) {
            for (int r = 0; r < Benchmark.R_MAX; r++) {
                for (int g = 0; g < Benchmark.G_MAX; g++) {
                    for (int s = 0; s < Benchmark.S_MAX; s++) {
                        for (int o = 0; o < Benchmark.O_MAX; o++) {
                            for (int c = 0; c < Benchmark.C_MAX; c++) {
                                for (int d = 0; d < Benchmark.D_MAX; d++) {
                                    if (count >= itemsToCreate) break opa;

                                    SomeData item = new SomeData();
                                    item.r = r;
                                    item.g = g;
                                    item.s = s;
                                    item.o = o;
                                    item.c = c;
                                    item.d = d;
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

    public static class SomeData {

        public int g;
        public int r;
        public int c;
        public int o;
        public int s;
        public int d;

    }

}
