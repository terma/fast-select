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
import java.util.Random;

public class BloomFilterAndDirectFiller {

    private static final Random RANDOM = new Random();

    public static FastSelect database;

    private final long itemsToCreate;
    private int count;
    private long time;

    public BloomFilterAndDirectFiller(int itemsToCreate) {
        this.itemsToCreate = itemsToCreate;
    }

    public void prepareDatabase() throws SQLException {
        System.out.println("Database prepared");
    }

    public void run() {
        System.out.println("Filler started");
        final long start = System.currentTimeMillis();

        final List<SomeData> data = new ArrayList<>();

        for (int i = 0; i < itemsToCreate; i++) {

            SomeData item = new SomeData();
            item.r = RANDOM.nextInt(Benchmark.R_MAX);
            item.g = RANDOM.nextInt(Benchmark.G_MAX);
            item.s = RANDOM.nextInt(Benchmark.S_MAX);
            item.o = RANDOM.nextInt(Benchmark.O_MAX);
            item.c = RANDOM.nextInt(Benchmark.C_MAX);
            item.d = RANDOM.nextInt(Benchmark.D_MAX);

            data.add(item);

            if (i % 10000 == 0) System.out.print(".");
        }
        database = new FastSelect(SomeData.class, data, new String[]{"g", "r", "s", "c", "d", "o"});

        System.out.println();

        time += System.currentTimeMillis() - start;
        count++;

        System.out.println("Created " + itemsToCreate + " count " + count + " average time " + time / count + " msec");
        System.out.println("Filler finished");
    }

}
