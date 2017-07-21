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

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Mac Air
 * <pre>
 * Benchmark                                          Mode  Cnt     Score   Error  Units
 * StringGetBytesBenchmark.createString               avgt        833.600          ms/op
 * StringGetBytesBenchmark.createStringGetBytes       avgt       1364.009          ms/op <<< was before
 * StringGetBytesBenchmark.createStringGetBytesAscII  avgt       1250.423          ms/op
 * StringGetBytesBenchmark.createStringGetBytesUtf8   avgt       1500.474          ms/op
 * </pre>
 */
@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(time = 15, iterations = 1)
@Measurement(time = 15, iterations = 1, batchSize = 6000000)
public class StringGetBytesBenchmark {

    private Charset ascIICharset = Charset.forName("ascII");
    private Random random = new Random();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include("." + StringGetBytesBenchmark.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void init() throws Exception {

    }

    @Benchmark
    public Object createString() {
        return "SOME STRING " + random.nextInt();
    }

    @Benchmark
    public Object createStringGetBytes() throws Exception {
        return ("SOME STRING " + random.nextInt()).getBytes();
    }

    @Benchmark
    public Object createStringGetBytesAscII() throws Exception {
        return ("SOME STRING " + random.nextInt()).getBytes(ascIICharset);
    }

    @Benchmark
    public Object createStringGetBytesUtf8() throws Exception {
        return Utf8Utils.stringToBytes(("SOME STRING " + random.nextInt()));
    }

}
