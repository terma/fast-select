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

import com.github.terma.fastselect.callbacks.CounterCallback;
import com.github.terma.fastselect.callbacks.GroupCountCallback;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 0, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
public class Benchmark {

    public static final int G_MAX = 100;
    public static final int R_MAX = 5;
    public static final int C_MAX = 6;
    public static final int O_MAX = 2;
    public static final int S_MAX = 100;
    public static final int D_MAX = 100;

    @Param({"1000"}) // "100000"
    private int blockSize;

    @Param({"1000000"}) // "10000000"
    private int volume;

    @Param({"FastSelect"})
    private String impl;

    private FastSelect fastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + Benchmark.class.getSimpleName() + ".*")
//                .addProfiler(HotspotMemoryProfiler.class)
//                .addProfiler(GCProfiler.class)
                .build();
        new Runner(opt).run();
    }

    public static MultiRequest[] createWhere() {
        return new MultiRequest[]{
                new MultiRequest("g", new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}),
                new MultiRequest("r", new int[]{0, 1, 2, 3, 4}),
                new MultiRequest("c", new int[]{0, 2, 3, 4}),
                new MultiRequest("s", new int[]{0, 19, 18, 17, 16, 15, 14, 13, 12, 11, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}),
                new MultiRequest("d", new int[]{0, 90, 99, 5, 34, 22, 26, 8, 5, 6, 7, 5, 6, 34, 35, 36, 37, 38, 39, 21, 70, 71, 74, 76, 78, 79, 10, 11, 22, 33, 44, 55, 66})
        };
    }

    public static FastSelect initDatabase(int blockSize, int volume) {
        final MemMeter memMeter = new MemMeter();

        new FastSelectFiller(blockSize, volume).run();
        FastSelect fastSelect = FastSelectFiller.database;

        System.out.println("Used mem: " + memMeter.getUsedMb() + " mb, volume: " + volume);
        return fastSelect;
    }

    @Setup
    public void init() throws InterruptedException {
        final MemMeter memMeter = new MemMeter();

        new FastSelectFiller(blockSize, volume).run();
        fastSelect = FastSelectFiller.database;

        System.out.println("Used mem: " + memMeter.getUsedMb() + " mb, volume: " + volume);
    }

    //    @org.openjdk.jmh.annotations.Benchmark
    public int countByFiltered10G5R4C20S40D() throws Exception {
        CounterCallback counterCallback = new CounterCallback();
        fastSelect.select(createWhere(), counterCallback);
        return counterCallback.getCount();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Object groupAndCountFiltered10G5R4C20S40D() throws Exception {
        GroupCountCallback counter = new GroupCountCallback(
                FastSelectFiller.database.getColumnsByNames().get("r"));
        fastSelect.select(createWhere(), counter);
        return counter.getCounters();
    }

}
