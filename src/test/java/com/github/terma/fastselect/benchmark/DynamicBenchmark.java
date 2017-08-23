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

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.FastSelectBuilder;
import com.github.terma.fastselect.data.StringCompressedByte;
import com.github.terma.fastselect.data.StringCompressedShort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ### initial result
 *
 * Looks not bad as we can add around 14m of items per min
 *
 * Benchmark                                    (volume)   Mode  Cnt   Score   Error    Units
 * DynamicBenchmark.adding100PerTimeUpToVolume   1000000  thrpt       14.292          ops/min
 * DynamicBenchmark.adding1PerTimeUpToVolume     1000000  thrpt        7.696          ops/min
 */
@Fork(value = 1, jvmArgs = {"-Xmx3g", "-XX:CompileThreshold=1"})
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MINUTES)
@State(Scope.Benchmark)
@Warmup(time = 15, iterations = 1)
@Measurement(time = 15, iterations = 1)
public class DynamicBenchmark {

    @Param({"1000000"}) // "10000000"
    private int volume;

    private List<DynamicData> data;
    private Random random;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." +
                DynamicBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        random = new Random();
        data = new ArrayList<>(volume);
        for (int j = 0; j < volume; j++) data.add(createData());

        System.out.println(adding1PerTimeUpToVolume());
        System.out.println(adding100PerTimeUpToVolume());
    }

    private DynamicData createData() {
        DynamicData dynamicData = new DynamicData();
        dynamicData.column1 = random.nextInt();
        dynamicData.uniqueId = "str " + random.nextInt();
        dynamicData.column4 = "zzz " + random.nextInt(100);
        dynamicData.column5 = "zzz " + random.nextInt(30000);
        dynamicData.column6 = "zzz " + random.nextInt(30000);
        dynamicData.column7 = "zzz " + random.nextInt(30000);
        dynamicData.column8 = "zzz " + random.nextInt(30000);
        dynamicData.column9 = "zzz " + random.nextInt(30000);
        dynamicData.column10 = "zzz " + random.nextInt(30000);
        dynamicData.column11 = "zzz " + random.nextInt();
        return dynamicData;
    }

    @Benchmark
    public Object adding1PerTimeUpToVolume() throws Exception {
        FastSelect<DynamicData> fastSelect = new FastSelectBuilder<>(DynamicData.class).create();
        for (int i = 0; i < volume; i++)
            fastSelect.addAll(Collections.singletonList(data.get(i)));
        return fastSelect.size();
    }

    @Benchmark
    public Object adding100PerTimeUpToVolume() throws Exception {
        FastSelect<DynamicData> fastSelect = new FastSelectBuilder<>(DynamicData.class).create();
        Iterator<DynamicData> it = data.iterator();
        while (it.hasNext()) {
            while (it.hasNext()) {
                List<DynamicData> data = new ArrayList<>(100);
                for (int j = 0; j < 100; j++) data.add(it.next());
                fastSelect.addAll(data);
            }
        }
        return fastSelect.size();
    }

    @SuppressWarnings("unused")
    public static class DynamicData {
        public byte delete;
        public long column1;
        public long column2;
        public long column3;
        public String uniqueId;
        @StringCompressedByte
        public String column4;
        @StringCompressedShort
        public String column5;
        @StringCompressedShort
        public String column6;
        @StringCompressedShort
        public String column7;
        @StringCompressedShort
        public String column8;
        @StringCompressedShort
        public String column9;
        @StringCompressedShort
        public String column10;
        public String column11;
        public short column12;
        public short column13;
        public short column14;
        public short column15;
        public short column16;
        public short column17;
    }

}
