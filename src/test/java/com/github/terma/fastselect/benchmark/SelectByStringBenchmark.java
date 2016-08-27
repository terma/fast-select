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

import com.github.terma.fastselect.*;
import com.github.terma.fastselect.callbacks.CounterCallback;
import com.github.terma.fastselect.data.IntStringData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                                 (volume)  Mode  Cnt     Score     Error  Units
 *
 * ### Naive implementation
 * SelectByStringBenchmark.byMultipleString  10000000  avgt    5  1523.173 ±  80.155  ms/op
 * SelectByStringBenchmark.byString          10000000  avgt    5   612.183 ±  72.304  ms/op
 *
 * ### Use direct reference on underlide MultiByteData structure for byString
 * SelectByStringBenchmark.byMultipleString  10000000  avgt    5  1563.511 ± 118.125  ms/op
 * SelectByStringBenchmark.byString          10000000  avgt    5   549.469 ±  79.973  ms/op -15%
 *
 * ### Compare against of full byte array (no copy one item to array) for byString
 * SelectByStringBenchmark.byMultipleString  10000000  avgt    5  1537.653 ± 127.824  ms/op
 * SelectByStringBenchmark.byString          10000000  avgt    5   215.349 ±   9.563  ms/op -60% (zero GC overhead)
 *
 * ### Use direct compare in multiple request as for string
 * SelectByStringBenchmark.byMultipleString  10000000  avgt    5   262.558 ±  26.569  ms/op -83% (zero GC overhead)
 * SelectByStringBenchmark.byString          10000000  avgt    5   204.015 ±  15.540  ms/op
 * </pre>
 */
@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 5)
public class SelectByStringBenchmark {

    @Param({"10000000"}) // "10000000"
    private int volume;

    private FastSelect<IntStringData> fastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + SelectByStringBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws InterruptedException {
        fastSelect = new FastSelectBuilder<>(IntStringData.class).inc(volume).create();

        List<IntStringData> example = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            example.add(new IntStringData(1, "UNIQUE string " + i));
        }
        fastSelect.addAll(example);

        // test run
        System.out.println("Result by string: " + byString());
        System.out.println("Result by multiple string: " + byMultipleString());
    }

    private ColumnRequest[] createStringRequest() {
        return new ColumnRequest[]{new StringRequest("value2", "UNIQUE string " + volume / 2)};
    }

    private ColumnRequest[] createMultipleStringRequest() {
        return new ColumnRequest[]{new StringMultipleRequest("value2",
                "UNIQUE string " + volume / 2, "UNIQUE string 10")};
    }

    @Benchmark
    public Object byString() {
        CounterCallback counter = new CounterCallback();
        fastSelect.select(createStringRequest(), counter);
        return counter.getCount();
    }

    @Benchmark
    public Object byMultipleString() {
        CounterCallback counter = new CounterCallback();
        fastSelect.select(createMultipleStringRequest(), counter);
        return counter.getCount();
    }

}
