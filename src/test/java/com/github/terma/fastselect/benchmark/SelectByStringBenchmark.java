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

import com.github.terma.fastselect.AbstractRequest;
import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.StringRequest;
import com.github.terma.fastselect.callbacks.CounterCallback;
import com.github.terma.fastselect.data.IntStringData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = "-Xmx7g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
public class SelectByStringBenchmark {

    @Param({"1000000"}) // "10000000"
    private int volume;

    private FastSelect<IntStringData> fastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + SelectByStringBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws InterruptedException {
        fastSelect = new FastSelect<>(IntStringData.class);

        List<IntStringData> example = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            example.add(new IntStringData(1, "UNIQUE string " + i));
        }
        fastSelect.addAll(example);

        // test run
        CounterCallback counter = new CounterCallback();
        fastSelect.select(createWhere(), counter);
        System.out.println("Request: " + Arrays.toString(createWhere()));
        System.out.println("Result: " + counter.toString());
    }

    private AbstractRequest[] createWhere() {
        return new AbstractRequest[]{new StringRequest("value2", "UNIQUE string " + volume / 2)};
    }

    @Benchmark
    public Object countFilterOneItemByString() throws Exception {
        CounterCallback counter = new CounterCallback();
        fastSelect.select(createWhere(), counter);
        return counter.getCount();
    }

}
