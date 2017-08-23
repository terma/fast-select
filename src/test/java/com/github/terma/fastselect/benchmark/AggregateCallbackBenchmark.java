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
import com.github.terma.fastselect.Request;
import com.github.terma.fastselect.callbacks.AggregateCallback;
import com.github.terma.fastselect.callbacks.Aggregator;
import com.github.terma.fastselect.data.ByteData;
import com.github.terma.fastselect.demo.DemoData;
import oadd.org.apache.commons.lang.mutable.MutableInt;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = "-Xmx3g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(time = 15, iterations = 1)
@Measurement(time = 15, iterations = 1)
public class AggregateCallbackBenchmark {

    @Param({"1000"})
    private int blockSize;

    @Param({"1000000"})
    private int volume;

    @Param({"FastSelect"})
    private String impl;

    private FastSelect<DemoData> fastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("." + AggregateCallbackBenchmark.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        fastSelect = SingleGroupCountBenchmark.initDatabase(blockSize, volume);

        System.out.println(">>>> TRY TEST:");
        System.out.println(fullVolumeTwoColumns());
        System.out.println(">>>> TEST RESULT");
    }

    @Benchmark
    public Object fullVolumeTwoColumns() throws Exception {
        final ByteData prgData = fastSelect.getData("prg");
        AggregateCallback<MutableInt> counter = new AggregateCallback<>(
                new Aggregator<MutableInt>() {
                    @Override
                    public void aggregate(MutableInt agg, int position) {
                        agg.add(prgData.data[position]);
                    }

                    @Override
                    public MutableInt create(int position) {
                        return new MutableInt(prgData.data[position]);
                    }
                },
                fastSelect.getColumnsByNames().get("prg"),
                fastSelect.getColumnsByNames().get("prr")
        );
        fastSelect.select(new Request[0], counter);
        return counter.getResult();
    }

}
