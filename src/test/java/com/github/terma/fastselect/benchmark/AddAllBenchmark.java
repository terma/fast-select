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

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.FastSelectBuilder;
import com.github.terma.fastselect.data.StringCompressedByte;
import com.github.terma.fastselect.data.StringCompressedShort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark               (blockSize)  (volume)  Mode  Cnt  Score   Error  Units
 * AddAllBenchmark.create         1000   1000000  avgt       0.464           s/op
 * AddAllBenchmark.create         1000  10000000  avgt       7.136           s/op
 */
@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
public class AddAllBenchmark {

    @Param({"1000"})
    private int blockSize;

    @Param({"10000000"}) // "1000000", "10000000", "60000000"
    private int volume;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + AddAllBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Benchmark
    public Object create() throws Exception {
        FastSelect<Data1> fastSelect = new FastSelectBuilder<>(Data1.class).blockSize(blockSize).create();

        Random random = new Random();
        String[] strings = new String[] {"SOME STRING", "A", "OPA      BBBBB"};

        List<Data1> buffer = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            Data1 data1 = new Data1();
            String s = strings[random.nextInt(strings.length)];
            data1.string1 = s;
            data1.string2 = s;
            data1.string3 = s;
            data1.string4 = s;
            data1.string5 = s;
            data1.string6 = s;
            data1.string7 = s;
            data1.string8 = s;
            data1.string9 = s;
            data1.string10 = s;
            data1.string11 = s;
            data1.string12 = s;
            buffer.add(data1);

            if (buffer.size() > 10000) {
                fastSelect.addAll(buffer);
                buffer.clear();
            }
        }
        fastSelect.addAll(buffer);
        buffer.clear();

        return fastSelect.mem();
    }

    static class Data1 {
        public long long1;
        public long long2;
        public long long3;
        public long long4;
        public long long5;
        public long long6;
        @StringCompressedByte
        public String string1;
        @StringCompressedByte
        public String string2;
        @StringCompressedByte
        public String string3;
        @StringCompressedByte
        public String string4;
        @StringCompressedByte
        public String string5;
        @StringCompressedByte
        public String string6;
        @StringCompressedByte
        public String string7;
        @StringCompressedByte
        public String string8;
        @StringCompressedByte
        public String string9;
        @StringCompressedShort
        public String string10;
        @StringCompressedShort
        public String string11;
        @StringCompressedShort
        public String string12;
    }

}
