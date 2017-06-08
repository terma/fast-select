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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

//@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1", "-XX:CompileCommand=print,*.FastSelect", "-prof perfasm:intelSyntax=true"})
//@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1", "-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintAssembly"})
@Fork(value = 1, jvmArgs = {"-Xmx3g", "-XX:CompileThreshold=1"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(time = 5, iterations = 1)
@Measurement(time = 10, iterations = 1)
public class DemoBenchmark {

    static final Map<String, Class<? extends PlayerFactory<DemoData>>> ENGINE_FACTORIES =
            new HashMap<String, Class<? extends PlayerFactory<DemoData>>>() {{
                put("H2", PlayerFactoryH2.class);
                put("FastSelect", PlayerFactoryFastSelect.class);
                put("Oracle", PlayerFactoryOracle.class);
                put("ApacheDrill", PlayerFactoryApacheDrill.class);
                put("MongoDb", PlayerFactoryMongoDb.class);
            }};

    @Param({"1000"})
    private int blockSize;

    @Param({"1000000"}) // 1000, 1000000, 10000000, 60000000
    private int volume;

    @Param({"FastSelect"}) // MongoDb, Oracle, FastSelect", H2
    private String engine;

    private Player player;

    public static void main(String[] args) throws RunnerException, IOException {
        final File stateFile = File.createTempFile("fast-select", "performance-benchmark");
        stateFile.deleteOnExit();

        Options opt = new OptionsBuilder()
                .include("." + DemoBenchmark.class.getSimpleName() + ".*")
                .jvmArgsAppend("-Dstate=" + stateFile.getAbsolutePath())
                .build();
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
        final BlockRoundValue stringRandom = new BlockRoundValue(1000, 200000, 50000);

        RoundValue prrValue = new RoundValue(DemoData.R_MAX);
        RoundValue csrValue = new RoundValue(DemoData.R_MAX);
        Random longValue = new Random();

        playerFactory.startAddData();

        for (int i = 0; i < itemsToCreate; i++) {
            DemoData item = new DemoData();
            item.prg = (byte) prgIdRandom.next();
            item.csg = (byte) csgIdRandom.next();

            item.prr = (byte) prrValue.next();
            item.csr = (byte) csrValue.next();

            item.vlc = longValue.nextLong();
            item.vch = longValue.nextLong();

            item.tr = "String like value " + stringRandom.next();

            item.bsid = bsIdRandom.next();

            data.add(item);

            if (i % 10000 == 0) {
                playerFactory.addData(data);
                data.clear();
            }
        }

        playerFactory.addData(data);
        playerFactory.finishAddData();
        data.clear();
    }

    @Setup
    public void init() throws Exception {
        System.out.println();
        File stateFile = new File(System.getProperty("state"));
        Properties stateProperties = new Properties();
        stateProperties.load(new FileInputStream(stateFile));

        final PlayerFactory<DemoData> playerFactory = ENGINE_FACTORIES.get(engine).newInstance();

        if (playerFactory.isDurable()) {
            if (stateProperties.containsKey(engine)) {
                System.out.println("durable use prepared data...");
            } else {
                System.out.println("durable prepare data first time...");
                fill(volume, playerFactory);
                stateProperties.put(engine, "true");
            }
        } else {
            System.out.println("not durable prepare data...");
            fill(volume, playerFactory);
        }

        player = playerFactory.createPlayer();

        if (!stateProperties.containsKey(engine + "-check-play")) {
            System.out.println("first time check play:");

            Method[] methods = Player.class.getDeclaredMethods();
            Arrays.sort(methods, new Comparator<Method>() {
                @Override
                public int compare(Method o1, Method o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (Method method : methods) {
                Object value = method.invoke(player);

                String string = method.getName();
                while (string.length() < 35) string = string + " ";

                if (value != null) System.out.println(string + " => " + value);
                else System.out.println(string + " => not implemented");
            }

            stateProperties.put(engine + "-check-play", "true");
        }

        stateProperties.store(new FileOutputStream(stateFile), "");
    }

    @Benchmark
    public Object groupByWhereSimple() throws Exception {
        return player.groupByWhereSimple();
    }

    @Benchmark
    public Object groupByWhereManySimple() throws Exception {
        return player.groupByWhereManySimple();
    }

    @Benchmark
    public Object groupByWhereIn() throws Exception {
        return player.groupByWhereIn();
    }

    @Benchmark
    public Object groupByWhereHugeIn() throws Exception {
        return player.groupByWhereHugeIn();
    }

    @Benchmark
    public Object groupByWhereManyIn() throws Exception {
        return player.groupByWhereManyIn();
    }

    @Benchmark
    @Threads(5)
    public Object groupByWhereManyIn5Threads() throws Exception {
        return player.groupByWhereManyIn();
    }

    @Benchmark
    @Threads(50)
    public Object groupByWhereManyIn50Threads() throws Exception {
        return player.groupByWhereManyIn();
    }

    @Benchmark
    public Object groupByWhereRange() throws Exception {
        return player.groupByWhereRange();
    }

    @Benchmark
    public Object groupByWhereManyRange() throws Exception {
        return player.groupByWhereManyRange();
    }

    @Benchmark
    public Object groupByWhereStringLike() throws Exception {
        return player.groupByWhereStringLike();
    }

    @Benchmark
    public Object groupByWhereSpareStringLike() throws Exception {
        return player.groupByWhereSpareStringLike();
    }

    @Benchmark
    public Object groupByWhereManyStringLike() throws Exception {
        return player.groupByWhereManyStringLike();
    }

    @Benchmark
    public Object groupByWhereString() throws Exception {
        return player.groupByWhereString();
    }

    @Benchmark
    public Object groupByWhereManyString() throws Exception {
        return player.groupByWhereManyString();
    }

    @Benchmark
    public Object groupByWhereSimpleRangeInStringLike() throws Exception {
        return player.groupByWhereSimpleRangeInStringLike();
    }

    @Benchmark
    public Object groupByWhereManyHugeIn() throws Exception {
        return player.groupByWhereManyHugeIn();
    }

    @Benchmark
    public Object selectLimit() throws Exception {
        return player.selectLimit();
    }

    @Benchmark
    public Object selectOrderByLimit() throws Exception {
        return player.selectOrderByLimit();
    }

}
