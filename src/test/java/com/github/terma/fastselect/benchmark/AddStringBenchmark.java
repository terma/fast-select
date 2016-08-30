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
 * <pre>
 * ### add strings
 * Benchmark                  (blockSize)  (volume)  Mode  Cnt  Score   Error  Units
 * AddStringBenchmark.create         1000  10000000  avgt    5  2.888 ± 0.381   s/op
 *
 * ### add strings and create block bit sec by bytes
 * AddStringBenchmark.create         1000  10000000  avgt    5  3.200 ± 0.064   s/op +312 msec
 *
 * ### improve bit set up
 * AddStringBenchmark.create         1000  10000000  avgt    5  3.024 ± 0.025   s/op +136 msec against original
 * </pre>
 */
@Fork(value = 1, jvmArgs = {"-Xmx6g"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 5)
public class AddStringBenchmark {

    @Param({"1000"})
    private int blockSize;

    @Param({"10000000"})
    private int volume;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + AddStringBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Benchmark
    public Object create() throws Exception {
        FastSelect<AddStringData> fastSelect = new FastSelectBuilder<>(AddStringData.class).blockSize(blockSize).create();

        Random random = new Random();
        String[] strings = new String[]{"SOME STRING", "A", "OPA      BBBBB", "1", "2"};

        List<AddStringData> buffer = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            AddStringData data = new AddStringData();
            data.string = strings[random.nextInt(strings.length)];
            buffer.add(data);

            if (buffer.size() > 10000) {
                fastSelect.addAll(buffer);
                buffer.clear();
            }
        }
        fastSelect.addAll(buffer);
        buffer.clear();

        return fastSelect.mem();
    }

    static class AddStringData {
        public String string;
    }

}
