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
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static com.github.terma.fastselect.benchmark.DemoBenchmark.ENGINE_FACTORIES;
import static com.github.terma.fastselect.benchmark.DemoBenchmark.fill;

@Fork(value = 1, jvmArgs = {"-Xmx7g", "-XX:CompileThreshold=1"})
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 5, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 5, iterations = 1)
public class CreatingBenchmark {

    @Param({"1000"})
    private int blockSize;

    @Param({"1000000"}) // "1000000", "10000000", "60000000"
    private int volume;

    @Param({"FastSelect"}) // "Oracle", "FastSelect", "H2"
    private String engine;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + CreatingBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Benchmark
    public Object create() throws Exception {
        final PlayerFactory<DemoData> playerFactory = ENGINE_FACTORIES.get(engine).newInstance();

        fill(volume, playerFactory);

        final Player player = playerFactory.createPlayer();

//        System.out.println("Check play:");
//        System.out.println(player.groupByWhereManyIn());
//        System.out.println(player.groupByWhereManyHugeIn());
//        System.out.println(player.selectOrderByLimit());
        return player.groupByWhereManyIn();
    }

}
