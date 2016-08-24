package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.data.Data;
import com.github.terma.fastselect.data.StringCompressedIntData;
import com.github.terma.fastselect.data.StringData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Mac Air SSD</h1>
 * <pre>
 * Benchmark                                                       (duplicateFactor)    (size)  Mode  Cnt      Score      Error  Units
 * CompressedStringVsStringLoadBenchmark.loadStringCompressedData                  3  16000000  avgt    5  14936.671 ± 8964.833  ms/op
 * CompressedStringVsStringLoadBenchmark.loadStringData                            3  16000000  avgt    5   1146.936 ± 1688.741  ms/op
 * CompressedStringVsStringLoadBenchmark.loadStringCompressedData                500  16000000  avgt    5    153.539 ±  200.239  ms/op
 * CompressedStringVsStringLoadBenchmark.loadStringData                          500  16000000  avgt    5   1077.554 ± 1542.845  ms/op
 */
@Fork(value = 1, jvmArgs = "-Xmx6g")
@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(timeUnit = TimeUnit.SECONDS, time = 5, iterations = 1)
@Measurement(timeUnit = TimeUnit.SECONDS, time = 5, iterations = 5)
public class CompressedStringVsStringLoadBenchmark {

    @Param("16000000")
    private int size;

    @Param("500")
    private int duplicateFactor;

    private File stringDataFile;
    private File stringCompressedIntDataFile;
    private StringData stringData;
    private StringCompressedIntData stringCompressedIntData;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include("." + CompressedStringVsStringLoadBenchmark.class.getSimpleName() + ".*").build();
        new Runner(opt).run();
    }

    private void save(final File file, Data data) throws IOException {
        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        final ByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, data.getDiskSpace());
        data.save(buffer);
        fc.close();
        System.out.println("Store " + data.getClass() + " with " + size + " file size " +
                (file.length() / 1024 / 1024) + "M " + "in " + (System.currentTimeMillis() - start) + " msec");
    }

    @Setup
    public void setup() throws IOException, InterruptedException, ExecutionException {
        stringData = new StringData(size);
        stringCompressedIntData = new StringCompressedIntData(size);

        stringDataFile = new File("string-data.bin");
        stringCompressedIntDataFile = new File("string-compressed-int-data.bin");

        if (!stringDataFile.exists() || !stringCompressedIntDataFile.exists()) {
            System.out.print("Preparing data");
            for (int i = 0; i < size; i++) {
                String string = "SOME KIND OF ID " + Math.round(i / duplicateFactor);
                stringData.add(string);
                stringCompressedIntData.add(string);
                if (i % 1000000 == 0) System.out.print(".");
            }
            System.out.println();
            System.out.println("unique val " + stringCompressedIntData.getValueToPosition().size());

            save(stringDataFile, stringData);
            save(stringCompressedIntDataFile, stringCompressedIntData);
        } else {
            System.out.println("Data prepared");
        }
    }

    @Benchmark
    public long loadStringData() throws Exception {
        return load(stringDataFile, stringData);
    }

    @Benchmark
    public long loadStringCompressedData() throws Exception {
        return load(stringCompressedIntDataFile, stringCompressedIntData);
    }

    private long load(final File file, final Data data) throws IOException, InterruptedException, ExecutionException {
        final FileChannel fc = new RandomAccessFile(file, "r").getChannel();
        final ByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        data.load(data.getClass().getName(), buffer, size);
        fc.close();
        return data.size();
    }

}
