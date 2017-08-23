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

import com.github.terma.fastselect.data.ByteData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = "-Xmx2g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(time = 15, iterations = 1)
@Measurement(time = 15, iterations = 1)
public class HashCodeBenchmark {

    private static final int SIZE = 10000;

    private ByteData data;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("." + HashCodeBenchmark.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        data = new ByteData(SIZE);
        for (int i = 0; i < SIZE; i++) data.add((byte) i);

        System.out.println();
        System.out.println("objectAndObjectsHashCode " + objectAndObjectsHashCode());
        System.out.println("dataHashCode " + dataHashCode());
        if (!objectAndObjectsHashCode().equals(dataHashCode())) throw new IllegalArgumentException();
    }

    @Benchmark
    public Object objectAndObjectsHashCode() throws Exception {
        long acc = 0;
        for (int i = 0; i < data.size; i++) acc += Objects.hashCode(data.get(i));
        return acc;
    }

    @Benchmark
    public Object dataHashCode() throws Exception {
        long acc = 0;
        for (int i = 0; i < data.size; i++) acc += data.hashCode(i);
        return acc;
    }

}
