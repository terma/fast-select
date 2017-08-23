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

package com.github.terma.fastselect.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.roaringbitmap.RoaringBitmap;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = "-Xmx7g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
public class DirectAccessArrayVsBitSetVsRoaringBitmapBenchmark {

    @Param({"1000000", "10000000"}) // "10000000"
    private int volume;

    private byte[] array;
    private BitSet bitSet;
    private RoaringBitmap roaringBitmap;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." +
                DirectAccessArrayVsBitSetVsRoaringBitmapBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        Random random = new Random();

        array = new byte[volume];
        for (int i = 0; i < volume; i++) array[i] = (byte) (random.nextBoolean() ? 0 : 1);

        bitSet = new BitSet();
        for (int i = 0; i < volume; i++) if (array[i] != 0) bitSet.set(i);

        roaringBitmap = new RoaringBitmap();
        for (int i = 0; i < volume; i++) if (array[i] != 0) roaringBitmap.add(i);
    }

    @Benchmark
    public Object array() throws Exception {
        int s = 0;
        for (int i = 0; i < volume; i++) s += array[i];
        return s;
    }

    @Benchmark
    public Object bitSet() throws Exception {
        int s = 0;
        for (int i = 0; i < volume; i++) s += bitSet.get(i) ? 1 : 0;
        return s;
    }

    @Benchmark
    public Object roaringBitmap() throws Exception {
        int s = 0;
        for (int i = 0; i < volume; i++) s += roaringBitmap.contains(i) ? 1 : 0;
        return s;
    }

}
