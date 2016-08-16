package com.github.terma.fastselect.benchmark;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MappedBufferManyVsOneBenchmark {

    private static final int ONE_M = 10 * 1000 * 1000;
    private static final int MUL = 16;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final File file = File.createTempFile("mapped-buffer-many-vs-one", "bin");
        file.deleteOnExit();
        prepare(file);

        long hole = 0;
        long totalOneTime = 0;
        long totalManyTime = 0;

        int iterations = 5;
        for (int i = 0; i < iterations; i++) {
            TimePlusHole timePlusHole;
            timePlusHole = loadByMany(file);
            totalOneTime += timePlusHole.time;
            hole += timePlusHole.hole;

            timePlusHole = loadByOne(file);
            totalManyTime += timePlusHole.time;
            hole += timePlusHole.hole;
        }

        System.out.println("hole (skip) " + hole);
        System.out.println("items to load " + MUL * ONE_M);
        System.out.println("load as one " + totalOneTime / iterations + " msec");
        System.out.println("load as many x" + MUL + " " + totalManyTime / iterations + " msec");
        long oneWin = (totalManyTime - totalOneTime);
        System.out.println("one win many on " + Math.round((100.0 / (double) totalManyTime * (double) totalOneTime) / (double) iterations) + "%");
    }

    private static TimePlusHole loadByMany(final File file) throws IOException, InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "r").getChannel();

        int total = 0;
        long hole = 0;
        for (long j = 0; j < MUL; j++) {
            final ByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, j * ONE_M, ONE_M);

            final byte[] data = new byte[ONE_M];
            byteBuffer.get(data);

            for (byte i : data) hole = Math.max(hole, i);
            total += data.length;
        }

        fc.close();
        Assert.assertEquals(MUL * ONE_M, total);
        return new TimePlusHole(System.currentTimeMillis() - start, hole);
    }

    private static TimePlusHole loadByOne(final File file) throws IOException, InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "r").getChannel();

        int hole = 0;
        final ByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, MUL * ONE_M);
        final byte[] data = new byte[MUL * ONE_M];
        byteBuffer.get(data);

        for (byte i : data) hole = Math.max(hole, i);

        fc.close();
        return new TimePlusHole(System.currentTimeMillis() - start, hole);
    }

    private static void prepare(final File file) throws IOException {
        System.out.println(new Date() + " creating file...");

        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        byte[] data = new byte[MUL * ONE_M];
        fc.write(ByteBuffer.wrap(data));
        fc.close();
        System.out.println(new Date() +
                " file created " + (MUL * ONE_M) + " items by " +
                MUL + " int, " +
                "size: " + (file.length() / 1024 / 1024) + "M " +
                "in " + (System.currentTimeMillis() - start) + " msec");
    }

    private static class TimePlusHole {
        private final long time;
        private final long hole;

        private TimePlusHole(long time, long hole) {
            this.time = time;
            this.hole = hole;
        }

        public long getTime() {
            return time;
        }

        public long getHole() {
            return hole;
        }

    }

}
