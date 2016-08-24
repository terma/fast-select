package com.github.terma.fastselect.benchmark;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * <h2>Results:</h2>
 * <p>
 * <pre>
 * Mac Air SSD
 * Mon Aug 15 22:50:30 EDT 2016 file created 1600000000 items by 16 byte, size: 1525M in 8972 msec
 * hole (skip) 0
 * file size 1525 mb
 * items to load 1600000000
 * load as one 4321 msec
 * load as many x16 3939 msec
 * one win many on 78%
 * </pre>
 */
public class MappedBufferManyVsOneBenchmark {

    private static final int ONE_COLUMN = 100 * 1000 * 1000;
    private static final int MUL = 16;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        final byte[] data = new byte[MUL * ONE_COLUMN];

        final File file = File.createTempFile("mapped-buffer-many-vs-one", "bin");
        file.deleteOnExit();
        prepare(file, data);

        long hole = 0;
        long totalOneTime = 0;
        long totalManyTime = 0;
        int iterations = 5;
        for (int i = 0; i < iterations; i++) {
            totalOneTime += loadByMany(file, data);
            for (byte j : data) hole = Math.max(hole, j);

            totalManyTime += loadByOne(file, data);
            for (byte j : data) hole = Math.max(hole, j);
        }

        System.out.println("hole (skip) " + hole);
        System.out.println("file size " + file.length() / 1024 / 1024 + " mb");
        System.out.println("items to load " + MUL * ONE_COLUMN);
        System.out.println("load as one " + totalOneTime / iterations + " msec");
        System.out.println("load as many x" + MUL + " " + totalManyTime / iterations + " msec");
        System.out.println("one win many on " + Math.round(100.0 - 100.0 / (double) totalManyTime * (double) totalOneTime / (double) iterations) + "%");
    }

    private static long loadByMany(final File file, byte[] data) throws IOException, InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "r").getChannel();

        long total = 0;
        for (long j = 0; j < MUL; j++) {
            int leng = ONE_COLUMN;
            long offset = j * leng;
            final ByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, offset, leng);
            byteBuffer.get(data, (int) offset, leng);
            total += leng;
        }

        fc.close();
        Assert.assertEquals(MUL * ONE_COLUMN, total);
        return System.currentTimeMillis() - start;
    }

    private static long loadByOne(final File file, byte[] data) throws IOException, InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "r").getChannel();

        int hole = 0;
        final ByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, MUL * ONE_COLUMN);

        byteBuffer.get(data);

        for (byte i : data) hole = Math.max(hole, i);

        fc.close();
        return System.currentTimeMillis() - start;
    }

    private static void prepare(final File file, byte[] data) throws IOException {
        System.out.println(new Date() + " creating file...");

        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        fc.write(ByteBuffer.wrap(data));
        fc.close();
        System.out.println(new Date() +
                " file created " + (MUL * ONE_COLUMN) + " items by " +
                MUL + " byte, " +
                "size: " + (file.length() / 1024 / 1024) + "M " +
                "in " + (System.currentTimeMillis() - start) + " msec");
    }

}
