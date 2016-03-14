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
import com.github.terma.fastselect.callbacks.CounterCallback;
import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.demo.DemoUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1"})
//@Fork(value = 1, jvmArgs = {
//        "-Xmx7g",
//        "-XX:CompileThreshold=1",
//        "-XX:+UnlockDiagnosticVMOptions",
//        "-XX:+LogCompilation",
//        "-XX:LogFile=/Users/terma/Projects/processing-prototype/perf-asm.log",
//        "-XX:+PrintAssembly",
//        "-XX:+PrintInterpreter",
//        "-XX:+PrintNMethods",
//        "-XX:+PrintNativeNMethods",
//        "-XX:+PrintSignatureHandlers",
//        "-XX:+PrintAdapterHandlers",
//        "-XX:+PrintStubCode",
//        "-XX:+PrintCompilation",
//        "-XX:+PrintInlining",
//        "-XX:+TraceClassLoading",
//        "-XX:PrintAssemblyOptions=syntax"
//})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
public class SingleGroupCountBenchmark {

    @Param({"1000"})
    private int blockSize;

    @Param({"1000000"}) // "10000000"
    private int volume;

    @Param({"FastSelect"})
    private String impl;

    private FastSelect<DemoData> fastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + SingleGroupCountBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    static FastSelect<DemoData> initDatabase(int blockSize, int volume) {
        return DemoUtils.createFastSelect(blockSize, volume);
    }

    @Setup
    public void init() throws InterruptedException {
        fastSelect = initDatabase(blockSize, volume);

        // test run
        CounterCallback counter = new CounterCallback();
        fastSelect.select(DemoUtils.whereGAndR(), counter);
        System.out.println(counter.toString());
    }

//    @Benchmark
//    public Object groupAndCountFiltered10G5R4C20S40D() throws Exception {
//        GroupCountCallback counter = new GroupCountCallback(
//                fastSelect.getColumnsByNames().get("r"));
//        fastSelect.select(DemoUtils.whereGAndR(), counter);
//        return counter.getCounters();
//    }

    @Benchmark
    public Object countFiltered10G5R4C20S40D() throws Exception {
        CounterCallback counter = new CounterCallback();
        fastSelect.select(DemoUtils.whereGAndR(), counter);
        return counter.getCount();
    }

}
