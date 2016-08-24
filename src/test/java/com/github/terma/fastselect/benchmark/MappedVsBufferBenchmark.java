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

import com.github.terma.fastselect.data.Data;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                       (column)    (size)  Mode  Cnt     Score   Error  Units
 * MappedVsBufferBenchmark.buffer         1   1000000  avgt         39.870          ms/op
 * MappedVsBufferBenchmark.buffer         1  10000000  avgt        422.242          ms/op
 * MappedVsBufferBenchmark.buffer         5   1000000  avgt        163.284          ms/op
 * MappedVsBufferBenchmark.buffer         5  10000000  avgt       2111.580          ms/op
 * MappedVsBufferBenchmark.buffer        10   1000000  avgt        316.420          ms/op
 * MappedVsBufferBenchmark.buffer        10  10000000  avgt       4496.194          ms/op
 * MappedVsBufferBenchmark.mapped         1   1000000  avgt         57.650          ms/op
 * MappedVsBufferBenchmark.mapped         1  10000000  avgt        531.927          ms/op
 * MappedVsBufferBenchmark.mapped         5   1000000  avgt        267.112          ms/op
 * MappedVsBufferBenchmark.mapped         5  10000000  avgt       2432.712          ms/op
 * MappedVsBufferBenchmark.mapped        10   1000000  avgt        524.181          ms/op
 * MappedVsBufferBenchmark.mapped        10  10000000  avgt       4787.045          ms/op
 * </pre>
 */
@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
public class MappedVsBufferBenchmark {

    private Random random = new Random();

    @Param({"1", "5", "10"})
    private int column;

    @Param({"1000000", "10000000"})
    private int size;

    private long[][] data;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("." + MappedVsBufferBenchmark.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        data = new long[column][];
        for (int i = 0; i < column; i++) {
            data[i] = new long[size];
            for (int j = 0; j < size; j++) data[i][j] = random.nextLong();
        }
    }

    @Benchmark
    public Object mapped() throws Exception {
        File file = Files.createTempFile("f", "f").toFile();
        long r = 0;
        try (FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel()) {
            for (int i = 0; i < column; i++) {
                ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, i * Data.LONG_BYTES * size, Data.LONG_BYTES * size);
                byteBuffer.asLongBuffer().put(data[i]);
                r += byteBuffer.position();
            }
        }
        file.delete();
        return r;
    }

    @Benchmark
    public Object buffer() throws Exception {
        File file = Files.createTempFile("f", "f").toFile();
        long r = 0;
        try (FileChannel fileChannel = new RandomAccessFile(file, "rw").getChannel()) {
            for (int i = 0; i < column; i++) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(Data.LONG_BYTES * size);
                byteBuffer.asLongBuffer().put(data[i]);
                fileChannel.write(byteBuffer);
                r += byteBuffer.position();
            }
        }
        file.delete();
        return r;
    }

}
