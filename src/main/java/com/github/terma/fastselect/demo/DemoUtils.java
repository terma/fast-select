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

package com.github.terma.fastselect.demo;

import com.github.terma.fastselect.AbstractRequest;
import com.github.terma.fastselect.ByteRequest;
import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.IntRequest;
import com.github.terma.fastselect.utils.MemMeter;
import com.github.terma.fastselect.utils.SpecialRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class DemoUtils {

    public static FastSelect<DemoData> createFastSelect(int[] blockSizes, int itemsToCreate) {
        System.out.println("Filler started");

        final MemMeter memMeter = new MemMeter();
        final long start = System.currentTimeMillis();

        FastSelect<DemoData> database = new FastSelect<>(blockSizes, DemoData.class);

        final List<DemoData> data = new ArrayList<>();
        final Random random = new Random();

        final SpecialRandom bsIdRandom = new SpecialRandom(
                DemoData.BS_ID_PORTION_DEVIATION, DemoData.BS_ID_PORTION, DemoData.BS_ID_MAX);

        final SpecialRandom prgIdRandom = new SpecialRandom(
                DemoData.G_ID_PORTION_DEVIATION, DemoData.G_ID_PORTION, DemoData.G_ID_MAX);
        final SpecialRandom csgIdRandom = new SpecialRandom(
                DemoData.G_ID_PORTION_DEVIATION, DemoData.G_ID_PORTION, DemoData.G_ID_MAX);

        for (int i = 0; i < itemsToCreate; i++) {
            DemoData item = new DemoData();
            item.prg = (byte) prgIdRandom.next();
            item.csg = (byte) csgIdRandom.next();

            item.prr = (byte) (random.nextInt(DemoData.R_MAX) + 1);
            item.csr = (byte) (random.nextInt(DemoData.R_MAX) + 1);

            /*
            make distribution more realistic.
            Instead of normal use small deviation in near blocks than make hure shift for next
            portion.
             */
            item.bsid = bsIdRandom.next();

            data.add(item);

            if (i % 1000 == 0) {
                database.addAll(data);
                data.clear();
                System.out.print(".");
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

    public static AbstractRequest[] whereGAndR() {
        return new AbstractRequest[]{
                new ByteRequest("prg", new int[]{1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89}),
                new ByteRequest("prr", new int[]{1, 2, 3, 4, 5, 6})
        };
    }

    public static AbstractRequest[] whereBsIdAndR() {
        return new AbstractRequest[]{
                new ByteRequest("prr", new int[]{1, 2, 3, 4, 5, 6}),
                new IntRequest("bsid", getBsIds())
        };
    }

    public static int[] getBsIds() {
        int[] bsIds = new int[10000];
        for (int i = 0; i < bsIds.length; i++) bsIds[i] = i + 40000;
        return bsIds;
    }

}
