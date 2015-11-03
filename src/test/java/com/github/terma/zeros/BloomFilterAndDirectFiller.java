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

package com.github.terma.zeros;

import java.sql.SQLException;
import java.util.Random;

public class BloomFilterAndDirectFiller {

    public static final BloomFilterAndDirect database =
            new BloomFilterAndDirect(SomeData.class, "g", "r", "s", "c", "d", "o");
    private static final Random RANDOM = new Random();

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
        try {
            unsafeRun();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Filler finished");
    }

    private void unsafeRun() throws Exception {
        long start = System.currentTimeMillis();

        for (int i = 0; i < itemsToCreate; i++) {

            SomeData data = new SomeData();
            data.r = RANDOM.nextInt(Benchmark.R_MAX);
            data.g = RANDOM.nextInt(Benchmark.G_MAX);
            data.s = RANDOM.nextInt(Benchmark.S_MAX);
            data.o = RANDOM.nextInt(Benchmark.O_MAX);
            data.c = RANDOM.nextInt(Benchmark.C_MAX);
            data.d = RANDOM.nextInt(Benchmark.D_MAX);

            database.add(data);

            if (i % 10000 == 0) System.out.print(".");
        }
        System.out.println();

        time += System.currentTimeMillis() - start;
        count++;

        System.out.println("Created " + itemsToCreate + " count " + count + " average time " + time / count + " msec");
    }

}
