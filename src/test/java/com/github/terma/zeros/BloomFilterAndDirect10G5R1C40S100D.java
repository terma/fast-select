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

import java.util.List;
import java.util.Random;

public class BloomFilterAndDirect10G5R1C40S100D implements Scenario {

    private static final Random RANDOM = new Random();

    private int[] randomArray(int size, int max) {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = RANDOM.nextInt(max);
        }
        return result;
    }

    @Override
    public int execute() throws Exception {
        int queried = 0;

        MultiRequest[] requests = new MultiRequest[]{
                new MultiRequest("g", randomArray(10, Benchmark.G_MAX)),
                new MultiRequest("r", randomArray(5, Benchmark.G_MAX)),
                new MultiRequest("c", randomArray(4, Benchmark.G_MAX)),
                new MultiRequest("s", randomArray(20, Benchmark.G_MAX)),
                new MultiRequest("d", randomArray(100, Benchmark.G_MAX))
        };

        List r = BloomFilterAndDirectFiller.database.select(requests);

        for (Object r1 : r) {
            queried++;
        }

        return queried;
    }

    @Override
    public void prepare() throws Exception {

    }

}
