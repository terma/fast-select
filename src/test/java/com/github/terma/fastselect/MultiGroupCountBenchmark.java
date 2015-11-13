/*
Copyright 2015 Artem Stasiuk

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

package com.github.terma.fastselect;

import com.github.terma.fastselect.callbacks.MultiGroupCountCallback;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
public class MultiGroupCountBenchmark {

    @Param({"1000"})
    private int blockSize;

    @Param({"10000000"})
    private int volume;

    @Param({"FastSelect"})
    private String impl;

    private FastSelect fastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MultiGroupCountBenchmark.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {
        fastSelect = com.github.terma.fastselect.Benchmark.initDatabase(blockSize, volume);

        System.out.println(">>>> TRY TEST:");
        System.out.println(test());
        System.out.println(">>>> TEST RESULT");
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Object test() throws Exception {
        MultiGroupCountCallback counter = new MultiGroupCountCallback(
                FastSelectFiller.database.getColumnsByNames().get("g"),
                FastSelectFiller.database.getColumnsByNames().get("r")
        );
        fastSelect.select(com.github.terma.fastselect.Benchmark.createWhere(), counter);
        return counter.getCounters();
    }

}
