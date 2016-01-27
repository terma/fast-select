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

import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.utils.MemMeter;
import com.github.terma.fastselect.utils.SpecialRandom;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

//@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1", "-XX:CompileCommand=print,*.FastSelect", "-prof perfasm:intelSyntax=true"})
@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 5, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 5, iterations = 1)
public class DemoBenchmark {

    private static final Map<String, Class<? extends PlayerFactory<DemoData>>> ENGINE_FACTORIES =
            new HashMap<String, Class<? extends PlayerFactory<DemoData>>>() {{
        put("H2", PlayerFactoryH2.class);
        put("FastSelect", PlayerFactoryFastSelect.class);
    }};

    @Param({"1000"})
    private int blockSize;

    @Param({"10000"}) // "10000000"
    private int volume;

    @Param({"FastSelect", "H2"}) // "FastSelect"
    private String engine;

    private Player player;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + DemoBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    private static void fill(int itemsToCreate, PlayerFactory<DemoData> playerFactory) throws Exception {
        System.out.println("Filler started");

        final MemMeter memMeter = new MemMeter();
        final long start = System.currentTimeMillis();

        final List<DemoData> data = new ArrayList<>();
        final Random random = new Random();

        final SpecialRandom bsIdRandom = new SpecialRandom(
                DemoData.BS_ID_PORTION_DEVIATION, DemoData.BS_ID_PORTION, DemoData.BS_ID_MAX);

        final SpecialRandom prgIdRandom = new SpecialRandom(
                DemoData.G_ID_PORTION_DEVIATION, DemoData.G_ID_PORTION, DemoData.G_ID_MAX);
        final SpecialRandom csgIdRandom = new SpecialRandom(
                DemoData.G_ID_PORTION_DEVIATION, DemoData.G_ID_PORTION, DemoData.G_ID_MAX);

        for (int i = 0; i < itemsToCreate; i++) {
            DemoData item = new DemoData();
            item.prg = (byte) prgIdRandom.next();
            item.csg = (byte) csgIdRandom.next();

            item.prr = (byte) (random.nextInt(DemoData.R_MAX) + 1);
            item.csr = (byte) (random.nextInt(DemoData.R_MAX) + 1);

            /*
            make distribution more realistic.
            Instead of normal use small deviation in near blocks than make hure shift for next
            portion.
             */
            item.bsid = bsIdRandom.next();

            data.add(item);

            if (i % 1000 == 0) {
                playerFactory.addData(data);
                data.clear();
                System.out.print(".");
            }
        }

        playerFactory.addData(data);
        data.clear();

        System.out.println();

        final long time = System.currentTimeMillis() - start;

        System.out.println("FastSelect prepared, volume: " + itemsToCreate + ", mem used: "
                + memMeter.getUsedMb() + "Mb, preparation time " + time + " msec");
        System.out.println("Filler finished");
    }

    @Setup
    public void init() throws Exception {
        PlayerFactory<DemoData> playerFactory = ENGINE_FACTORIES.get(engine).newInstance();

        fill(volume, playerFactory);

        player = playerFactory.createPlayer();

        // test run
        System.out.println(player.playGroupByGAndR());
//        System.out.println(internalGroupByBsIdAndR());
//        System.out.println(internalDetailsByGAndRAndSorting());
    }

    @Benchmark
    public Object groupByGAndR() throws Exception {
        return player.playGroupByGAndR();
    }

//    @Benchmark
//    public Object groupByBsIdAndR() throws Exception {
//        return internalGroupByBsIdAndR();
//    }

//    @Benchmark
//    public Object detailsByGAndRAndSorting() throws Exception {
//        return internalDetailsByGAndRAndSorting();
//    }

//    private Object internalGroupByGAndR() {
////        MultiGroupCountCallback callback = new MultiGroupCountCallback(
////                fastSelect.getColumnsByNames().get("prg"),
////                fastSelect.getColumnsByNames().get("prr")
////        );
//        CounterCallback callback = new CounterCallback();
//        fastSelect.select(DemoUtils.whereGAndR(), callback);
//        return callback;
//    }

//    private Object internalGroupByBsIdAndR() {
//        MultiGroupCountCallback callback = new MultiGroupCountCallback(
//                fastSelect.getColumnsByNames().get("bsid"),
//                fastSelect.getColumnsByNames().get("prr")
//        );
//        fastSelect.select(DemoUtils.whereBsIdAndR(), callback);
//        return callback.getCounters();
//    }

//    private int internalDetailsByGAndRAndSorting() {
//        ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
//        fastSelect.selectAndSort(DemoUtils.whereGAndR(), callback, "prr");
//        return callback.getResult().size();
//    }

}
