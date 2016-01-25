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
import com.github.terma.fastselect.callbacks.ListLimitCallback;
import com.github.terma.fastselect.callbacks.MultiGroupCountCallback;
import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.demo.DemoUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 30, iterations = 1)
public class DemoBenchmark {

    @Param({"1000"})
    private int blockSize;

    @Param({"1000000"}) // "10000000"
    private int volume;

    private FastSelect<DemoData> fastSelect;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + DemoBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    static FastSelect<DemoData> initDatabase(int blockSize, int volume) {
        return initDatabase(new int[]{blockSize}, volume);
    }

    static FastSelect<DemoData> initDatabase(int[] blockSizes, int volume) {
        return DemoUtils.createFastSelect(blockSizes, volume);
    }

    @Setup
    public void init() throws InterruptedException {
        fastSelect = initDatabase(blockSize, volume);

        // test run
        System.out.println(internalGroupByGAndR());
        System.out.println(internalGroupByBsIdAndR());
        System.out.println(internalDetailsByGAndRAndSorting());
    }

//    @Benchmark
//    public Object groupByGAndR() throws Exception {
//        return internalGroupByGAndR();
//    }

//    @Benchmark
//    public Object groupByBsIdAndR() throws Exception {
//        return internalGroupByBsIdAndR();
//    }

    @Benchmark
    public Object detailsByGAndRAndSorting() throws Exception {
        return internalDetailsByGAndRAndSorting();
    }

    private Object internalGroupByGAndR() {
        MultiGroupCountCallback callback = new MultiGroupCountCallback(
                fastSelect.getColumnsByNames().get("prg"),
                fastSelect.getColumnsByNames().get("prr")
        );
        fastSelect.select(DemoUtils.whereGAndR(), callback);
        return callback.getCounters();
    }

    private Object internalGroupByBsIdAndR() {
        MultiGroupCountCallback callback = new MultiGroupCountCallback(
                fastSelect.getColumnsByNames().get("bsid"),
                fastSelect.getColumnsByNames().get("prr")
        );
        fastSelect.select(DemoUtils.whereBsIdAndR(), callback);
        return callback.getCounters();
    }

    private int internalDetailsByGAndRAndSorting() {
        ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
        fastSelect.selectAndSort(DemoUtils.whereGAndR(), callback, "prr");
        return callback.getResult().size();
    }

}
