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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BloomFilterAndDirectFiller {

    public static FastSelect database;

    private final int blockSize;
    private final long itemsToCreate;
    private int count;
    private long time;

    public BloomFilterAndDirectFiller(int blockSize, int itemsToCreate) {
        this.blockSize=blockSize;
        this.itemsToCreate = itemsToCreate;
    }

    public BloomFilterAndDirectFiller(int itemsToCreate) {
        this.blockSize = 1000;
        this.itemsToCreate = itemsToCreate;
    }

    public void prepareDatabase() throws SQLException {
        System.out.println("Database prepared");
    }

    public void run() {
        System.out.println("Filler started");
        final long start = System.currentTimeMillis();

        final List<SomeData> data = new ArrayList<>();

        opa:
        for (int r = 0; r < Benchmark.R_MAX; r++) {
            for (int g = 0; g < Benchmark.G_MAX; g++) {
                for (int s = 0; s < Benchmark.S_MAX; s++) {
                    for (int o = 0; o < Benchmark.O_MAX; o++) {
                        for (int c = 0; c < Benchmark.C_MAX; c++) {
                            for (int d = 0; d < Benchmark.D_MAX; d++) {
                                if (data.size() >= itemsToCreate) break opa;

                                SomeData item = new SomeData();
                                item.r = r;
                                item.g = g;
                                item.s = s;
                                item.o = o;
                                item.c = c;
                                item.d = d;
                                data.add(item);

                                if (data.size() % 10000 == 0) System.out.print(".");
                            }
                        }
                    }
                }

            }
        }

//        Collections.shuffle(data); // to be more like real
        database = new FastSelect(blockSize, SomeData.class, data, new String[]{"g", "r", "s", "c", "d", "o"});

        System.out.println();

        time += System.currentTimeMillis() - start;
        count++;

        System.out.println("Created " + itemsToCreate + " count " + count + " average time " + time / count + " msec");
        System.out.println("Filler finished");
    }

}
