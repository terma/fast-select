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
 * Benchmark                                   (volume)  Mode  Cnt     Score     Error  Units
 *
 * ### Naive implementation
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5  1523.173 ±  80.155  ms/op
 * SelectByStringBenchmark.byString            10000000  avgt    5   612.183 ±  72.304  ms/op
 *
 * ### Use direct reference on underlide MultiByteData structure for byString
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5  1563.511 ± 118.125  ms/op
 * SelectByStringBenchmark.byString            10000000  avgt    5   549.469 ±  79.973  ms/op -15%
 *
 * ### Compare against of full byte array (no copy one item to array) for byString
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5  1537.653 ± 127.824  ms/op
 * SelectByStringBenchmark.byString            10000000  avgt    5   215.349 ±   9.563  ms/op -60% (zero GC overhead)
 *
 * ### Use direct compare in multiple request as for string
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5   262.558 ±  26.569  ms/op -83% (zero GC overhead)
 * SelectByStringBenchmark.byString            10000000  avgt    5   204.015 ±  15.540  ms/op
 *
 * ### let's try no case like request for string
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5   319.229 ± 16.949  ms/op
 * SelectByStringBenchmark.byNoCaseLikeString  10000000  avgt    5  3253.785 ± 85.318  ms/op
 * SelectByStringBenchmark.byString            10000000  avgt    5   236.411 ±  8.620  ms/op
 *
 * ### cache reference on string data in like
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5   299.250 ±  6.759  ms/op
 * SelectByStringBenchmark.byNoCaseLikeString  10000000  avgt    5  3118.695 ± 70.248  ms/op -almost nothing =(((
 * SelectByStringBenchmark.byString            10000000  avgt    5   247.298 ± 45.352  ms/op
 *
 * ### !!! changes in test case remove dynamic string creating for request
 * ### that's save ~177 msec per test
 * ### below correct test case with static string for request so count just real search
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5   122.894 ±  17.723  ms/op
 * SelectByStringBenchmark.byNoCaseLikeString  10000000  avgt    5  2870.800 ± 245.973  ms/op
 * SelectByStringBenchmark.byString            10000000  avgt    5    93.380 ±   4.780  ms/op
 *
 * ### using block meta data (set of bytes) for string
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5   118.639 ±   1.775  ms/op
 * SelectByStringBenchmark.byNoCaseLikeString  10000000  avgt    5  2998.899 ± 168.395  ms/op
 * SelectByStringBenchmark.byString            10000000  avgt    5    14.196 ±   1.122  ms/op -85%
 *
 * ### using block meta data for multi string
 * SelectByStringBenchmark.byMultipleString    10000000  avgt    5    18.413 ±   0.833  ms/op -85%
 * SelectByStringBenchmark.byNoCaseLikeString  10000000  avgt    5  2867.359 ± 189.627  ms/op
 * SelectByStringBenchmark.byString            10000000  avgt    5    13.625 ±   6.596  ms/op
 *
 * ### fast no case like for latin no case
 * Benchmark                                        (volume)  Mode  Cnt     Score     Error  Units
 * SelectByStringBenchmark.byMultipleString         10000000  avgt    5    18.496 ±   1.188  ms/op
 * SelectByStringBenchmark.byNoCaseLikeString       10000000  avgt    5  2868.618 ± 247.770  ms/op
 * SelectByStringBenchmark.byLatinNoCaseLikeString  10000000  avgt    5   662.078 ±  24.220  ms/op -77%
 * SelectByStringBenchmark.byString                 10000000  avgt    5    13.197 ±   6.609  ms/op
 *
 * ### use block meta info for latin no case
 * SelectByStringBenchmark.byLatinNoCaseLikeString  10000000  avgt    5    96.600 ±   4.065  ms/op -75%
 * SelectByStringBenchmark.byMultipleString         10000000  avgt    5    18.097 ±   1.792  ms/op
 * SelectByStringBenchmark.byNoCaseLikeString       10000000  avgt    5  2851.732 ± 154.023  ms/op
 * SelectByStringBenchmark.byString                 10000000  avgt    5    12.916 ±   6.011  ms/op
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

        char[] chars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'N', 'M', 'O', 'R', 'S', 'Q', 'Y', 'Z'};
        int j = -1;

        List<IntStringData> example = new ArrayList<>();
        for (int i = 0; i < volume; i++) {
            if (i % 500 == 0) {
                j++;
                if (j == chars.length) j = 0;
            }
            example.add(new IntStringData(1, "UNIQUE string " + chars[j] + i));
//            example.add(new IntStringData(1, "UNIQUE string " + i));
        }
        fastSelect.addAll(example);

        // test run
        System.out.println("Result by string: " + byString());
        System.out.println("Blocks by string: " + fastSelect.blockTouch(createStringRequest()));
        System.out.println("Result by multi string: " + byMultipleString());
        System.out.println("Blocks by multi string: " + fastSelect.blockTouch(createMultipleStringRequest()));
        System.out.println("Result by no case like string: " + byNoCaseLikeString());
        System.out.println("Result by latin no case like string: " + byLatinNoCaseLikeString());
    }

    private Request[] createStringRequest() {
        return new ColumnRequest[]{new StringRequest("value2", "UNIQUE string B501")};
    }

    private Request[] createMultipleStringRequest() {
        return new ColumnRequest[]{new StringMultipleRequest("value2", "UNIQUE string B501", "UNIQUE string A10")};
    }

    private Request[] createNoCaseLikeString() {
        return new ColumnRequest[]{new StringNoCaseLikeRequest("value2", "strIng B501")};
//        return new Request[]{StringNoCaseLikeRequest.create("value2", "strIng B501")};
    }

    private Request[] createLatinNoCaseLikeString() {
//        return new ColumnRequest[]{new StringNoCaseLikeRequest("value2", "strIng B501")};
        return new Request[]{StringNoCaseLikeRequest.create("value2", "strIng B501")};
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

    @Benchmark
    public Object byNoCaseLikeString() {
        CounterCallback counter = new CounterCallback();
        fastSelect.select(createNoCaseLikeString(), counter);
        return counter.getCount();
    }

    @Benchmark
    public Object byLatinNoCaseLikeString() {
        CounterCallback counter = new CounterCallback();
        fastSelect.select(createLatinNoCaseLikeString(), counter);
        return counter.getCount();
    }

}
