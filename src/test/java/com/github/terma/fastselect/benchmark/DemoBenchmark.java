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
import com.github.terma.fastselect.utils.BlockRoundValue;
import com.github.terma.fastselect.utils.RoundValue;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1", "-XX:CompileCommand=print,*.FastSelect", "-prof perfasm:intelSyntax=true"})
@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1", "-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintAssembly"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
public class DemoBenchmark {

    static final Map<String, Class<? extends PlayerFactory<DemoData>>> ENGINE_FACTORIES =
            new HashMap<String, Class<? extends PlayerFactory<DemoData>>>() {{
                put("H2", PlayerFactoryH2.class);
                put("FastSelect", PlayerFactoryFastSelect.class);
                put("Oracle", PlayerFactoryOracle.class);
            }};

    @Param({"1000"})
    private int blockSize;

    @Param({"1000000"}) // "1000000", "10000000", "60000000"
    private int volume;

    @Param({"FastSelect"}) // "Oracle", "FastSelect", "H2"
    private String engine;

    private Player player;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + DemoBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    static void fill(int itemsToCreate, PlayerFactory<DemoData> playerFactory) throws Exception {
        final List<DemoData> data = new ArrayList<>();

        final BlockRoundValue bsIdRandom = new BlockRoundValue(
                DemoData.BS_ID_PORTION_DEVIATION, DemoData.BS_ID_PORTION, DemoData.BS_ID_MAX);

        final BlockRoundValue prgIdRandom = new BlockRoundValue(
                DemoData.G_ID_PORTION_DEVIATION, DemoData.G_ID_PORTION, DemoData.G_ID_MAX);
        final BlockRoundValue csgIdRandom = new BlockRoundValue(
                DemoData.G_ID_PORTION_DEVIATION, DemoData.G_ID_PORTION, DemoData.G_ID_MAX);

        RoundValue prrValue = new RoundValue(DemoData.R_MAX);
        RoundValue csrValue = new RoundValue(DemoData.R_MAX);

        for (int i = 0; i < itemsToCreate; i++) {
            DemoData item = new DemoData();
            item.prg = (byte) prgIdRandom.next();
            item.csg = (byte) csgIdRandom.next();

            item.prr = (byte) prrValue.next();
            item.csr = (byte) csrValue.next();

            item.bsid = bsIdRandom.next();

            data.add(item);

            if (i % 10000 == 0) {
                playerFactory.addData(data);
                data.clear();
            }
        }

        playerFactory.addData(data);
        data.clear();
    }

    @Setup
    public void init() throws Exception {
        final PlayerFactory<DemoData> playerFactory = ENGINE_FACTORIES.get(engine).newInstance();

        fill(volume, playerFactory);

        player = playerFactory.createPlayer();

        System.out.println("Check play:");
        System.out.println(player.playGroupByGAndR());
        System.out.println(player.playGroupByBsIdAndR());
        System.out.println(player.playDetailsByGAndRAndSorting());
    }

    //    @Benchmark
    @Threads(1)
    public Object groupByGAndR() throws Exception {
        return player.playGroupByGAndR();
    }

    //    @Benchmark
    @Threads(5)
    public Object groupByGAndR_5_Threads() throws Exception {
        return player.playGroupByGAndR();
    }

    //    @Benchmark
    @Threads(50)
    public Object groupByGAndR_50_Threads() throws Exception {
        return player.playGroupByGAndR();
    }

    //    @Benchmark
    public Object groupByBsIdAndR() throws Exception {
        return player.playGroupByBsIdAndR();
    }

    @Benchmark
    public Object detailsByGAndRAndSorting() throws Exception {
        return player.playDetailsByGAndRAndSorting();
    }

}
