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
import com.github.terma.fastselect.Request;
import com.github.terma.fastselect.data.StringCompressedByte;
import com.github.terma.fastselect.data.StringCompressedShort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {"-Xmx3g", "-XX:CompileThreshold=1"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(time = 15, iterations = 1)
@Measurement(time = 15, iterations = 1)
public class CopyBenchmark {

    @Param({"1000000"}) // "10000000"
    private int volume;

    private FastSelect<OneData> oneFastSelect;
    private FastSelect<ScalarData> scalarFastSelect;
    private FastSelect<StringData> stringFastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + CopyBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        oneFastSelect = new FastSelectBuilder<>(OneData.class).inc(volume).create();
        List<OneData> one = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            OneData data = new OneData();
            data.intValue = i;
            one.add(data);
        }
        oneFastSelect.addAll(one);
        System.out.println("One column size: " + ((FastSelect) oneColumn()).size());

        scalarFastSelect = new FastSelectBuilder<>(ScalarData.class).inc(volume).create();
        List<ScalarData> scalar = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            ScalarData data = new ScalarData();
            data.byteValue = (byte) i;
            data.shortValue = (short) i;
            data.intValue = i;
            data.longValue = i;
            scalar.add(data);
        }
        scalarFastSelect.addAll(scalar);
        System.out.println("4 scalar columns size: " + ((FastSelect) scalarColumns()).size());

        stringFastSelect = new FastSelectBuilder<>(StringData.class).inc(volume).create();
        List<StringData> strings = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            StringData data = new StringData();
            data.longValue = (byte) i;
            data.stringData = "unique string data" + i;
            data.stringCompressedByteData = "string feet in byte " + (i % 100);
            data.stringCompressedShortData = "string feet in short " + (i % 30000);
            strings.add(data);
            if (i % 10000 == 0) {
                stringFastSelect.addAll(strings);
                strings.clear();
            }
        }
        stringFastSelect.addAll(strings);
        System.out.println("string columns size: " + ((FastSelect) stringColumns()).size());
    }

    @Benchmark
    public Object oneColumn() throws Exception {
        return oneFastSelect.copy(new Request[0]);
    }

    @Benchmark
    public Object scalarColumns() throws Exception {
        return scalarFastSelect.copy(new Request[0]);
    }

    @Benchmark
    public Object stringColumns() throws Exception {
        return stringFastSelect.copy(new Request[0]);
    }

    @SuppressWarnings("WeakerAccess")
    public static class OneData {
        public int intValue;
    }

    @SuppressWarnings("WeakerAccess")
    public static class ScalarData {
        public byte byteValue;
        public short shortValue;
        public int intValue;
        public long longValue;
    }

    @SuppressWarnings("WeakerAccess")
    public static class StringData {
        public long longValue;
        public String stringData;
        @StringCompressedByte
        public String stringCompressedByteData;
        @StringCompressedShort
        public String stringCompressedShortData;
    }

}
