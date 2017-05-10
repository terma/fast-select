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

import com.github.terma.fastselect.utils.Utf8Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Benchmark                                                   (volume)  Mode  Cnt     Score     Error  Units
 * NoCaseLikeStringVsUtf8CaseFoldingBenchmark.string           10000000  avgt    5  2212.140 ± 165.010  ms/op
 * NoCaseLikeStringVsUtf8CaseFoldingBenchmark.utf8CaseFolding  10000000  avgt    5   471.462 ±  43.806  ms/op
 * </pre>
 */
@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 15, iterations = 5)
public class NoCaseLikeStringVsUtf8CaseFoldingBenchmark {

    @Param({"10000000"}) // "10000000"
    private int volume;

    private byte[][] data;
    private String like;
    private byte[] likeBytes;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." +
                NoCaseLikeStringVsUtf8CaseFoldingBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws InterruptedException {
        assertNoCaseLike();

        like = ("OMA " + volume / 2).toLowerCase();
        likeBytes = like.getBytes();

        data = new byte[volume][];
        for (int i = 0; i < data.length; i++)
            data[i] = ("RoMa and oMa " + i).getBytes();

        System.out.println("like: " + like);
        System.out.println("string: " + string());
        System.out.println("utf8 case folding: " + utf8CaseFolding());
    }

    private void assertNoCaseLike() {
        System.out.println();
        System.out.println("Check: ");
        String[] testLikes = new String[]{"Roma", "z", "A", "", "roma", "OmA", "Z"};
        byte[] test = "Roma".getBytes();
        for (String testLike : testLikes) {
            like = testLike.toLowerCase();
            likeBytes = like.getBytes();

            boolean utf8CaseFoldingResult = Utf8Utils.latinBytesContains(test, 0, test.length, likeBytes);
            boolean stringResult = stringCheck(test);
            if (stringResult != utf8CaseFoldingResult) {
                System.out.println("string: " + stringResult +
                        " while utf8 case folding: " + utf8CaseFoldingResult +
                        " on: " + new String(test) +
                        " for: " + testLike);
                System.exit(12);
            }
        }
        System.out.println();
    }

    @Benchmark
    public Object string() {
        int c = 0;
        for (byte[] v : data) if (stringCheck(v)) c++;
        return c;
    }

    private boolean stringCheck(byte[] bytes) {
        String string = new String(bytes);
        return string.toLowerCase().contains(like);
    }

    @Benchmark
    public Object utf8CaseFolding() {
        int c = 0;
        for (byte[] v : data) if (Utf8Utils.latinBytesContains(v, 0, v.length, likeBytes)) c++;
        return c;
    }

}
