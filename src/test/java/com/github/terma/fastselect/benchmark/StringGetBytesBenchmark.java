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

package com.github.terma.fastselect.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Mac Air
 * <pre>
 * Benchmark                         (volume)  Mode  Cnt     Score      Error  Units
 * StringGetBytesBenchmark.getBytes  10000000  avgt    5  2428.723 ± 3187.575  ms/op
 * </pre>
 */
@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 5)
public class StringGetBytesBenchmark {

    private String[] in;
    private byte[][] out;

    @Param("10000000")
    private int volume;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("." + StringGetBytesBenchmark.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        Random random = new Random();
        in = new String[volume];
        out = new byte[volume][];
        for (int i = 0; i < volume; i++) {
            in[i] = "Some String " + random.nextInt();
        }
    }

    @Benchmark
    public Object getBytes() throws Exception {
        for (int i = 0; i < volume; i++) out[i] = in[i].getBytes();
        return out;
    }

}
