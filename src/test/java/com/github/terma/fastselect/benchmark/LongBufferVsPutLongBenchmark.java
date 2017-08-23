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

import com.github.terma.fastselect.data.Data;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * with buffer creation
 * <pre>
 * Benchmark                                         (size)  Mode  Cnt    Score   Error  Units
 * LongBufferVsPutLongBenchmark.putLong            10000000  avgt       201.586          ms/op
 * LongBufferVsPutLongBenchmark.putLongByLongView  10000000  avgt       203.562          ms/op
 * </pre>
 * Without buffer creation
 * <pre>
 * Benchmark                                         (size)  Mode  Cnt    Score   Error  Units
 * LongBufferVsPutLongBenchmark.putLong            10000000  avgt        90.122          ms/op
 * LongBufferVsPutLongBenchmark.putLongByIndex     10000000  avgt       114.299          ms/op
 * LongBufferVsPutLongBenchmark.putLongByLongView  10000000  avgt        58.951          ms/op
 * </pre>
 * with direct buffer
 * <pre>
 * Benchmark                                         (size)  Mode  Cnt   Score   Error  Units
 * LongBufferVsPutLongBenchmark.putLong            10000000  avgt       24.911          ms/op
 * LongBufferVsPutLongBenchmark.putLongByIndex     10000000  avgt       24.476          ms/op
 * LongBufferVsPutLongBenchmark.putLongByLongView  10000000  avgt       40.040          ms/op
 * </pre>
 */
@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
public class LongBufferVsPutLongBenchmark {

    private Random random = new Random();

    @Param({"10000000"})
    private int size;

    private long[] data;

    private ByteBuffer buffer;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("." + LongBufferVsPutLongBenchmark.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        data = new long[size];
        for (int i = 0; i < size; i++) data[i] = random.nextLong();
        buffer = ByteBuffer.allocateDirect(Data.LONG_BYTES * size);
    }

    @Benchmark
    public Object putLong() throws Exception {
        buffer.position(0);
        for (long l : data) buffer.putLong(l);
        return buffer;
    }

    @Benchmark
    public Object putLongByIndex() throws Exception {
        buffer.position(0);
        for (int i = 0; i < data.length; i++) buffer.putLong(data[i]);
        return buffer;
    }

    @Benchmark
    public Object putLongByLongView() throws Exception {
        buffer.position(0);
        buffer.asLongBuffer().put(data);
        return buffer;
    }

}
