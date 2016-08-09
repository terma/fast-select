package com.github.terma.fastselect.demo;

import com.github.terma.fastselect.data.Data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class IODemo {

    private static final int ONE_M = 1000 * 1000;
    private static final int MUL = 12;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        if (args.length < 3) {
            System.out.println("use: java -jar fast-select.jar <file-path> <size in m> <count of threads> <write|read>");
            System.exit(1);
        }

        final boolean write = "write".equals(args[3]);
        final String filePath = args[0];
        final File file = new File(filePath);
        final int size = Integer.parseInt(args[1]);
        final int threads = Integer.parseInt(args[2]);

        if (write) createFile(file, size);
        else load(file, threads);
    }

    private static void load(final File file, int threads) throws IOException, InterruptedException, ExecutionException {
        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "r").getChannel();

        ByteBuffer sizeBuffer = ByteBuffer.allocate(Data.INT_BYTES);
        fc.read(sizeBuffer);
        sizeBuffer.flip();
        final int size = sizeBuffer.getInt();

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<Long>> f = new ArrayList<>();
        for (long j = 0; j < MUL; j++) {
            long position = 4 + j * 8 * size;
            final ByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, position, 8 * size);
            f.add(executorService.submit(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    final long[] data = new long[size];
                    LongBuffer b = byteBuffer.asLongBuffer();
                    b.get(data);

                    long max = 0;
                    for (long i : data) max = Math.max(max, i);
                    return max;
                }
            }));
        }

        for (long i = 0; i < MUL; i++) {
            System.out.print(".");
            final long r = f.get((int) i).get();
            if (r != i) System.err.println(
                    "Corrupted data or reading, expected " + i + ", got " + r + "!");
        }
        System.out.println();
        executorService.shutdown();

        fc.close();
        System.out.println("read x" + (size / ONE_M) + "M by " + threads + " threads in "
                + (System.currentTimeMillis() - start) + " msec");
    }

    private static void createFile(final File file, final int size) throws IOException {
        System.out.println(new Date() + " creating file...");
        if (!file.delete()) {
            System.out.println("Can't remove file: " + file);
            System.exit(2);
        }

        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate(4).putInt(size * ONE_M).flip());
        for (long j = 0; j < MUL; j++) {
            final long position = 4 + 8 * j * size * ONE_M;
            ByteBuffer b = fc.map(FileChannel.MapMode.READ_WRITE, position, 8 * size * ONE_M);
            for (int i = 0; i < size * ONE_M; i++) b.putLong(j);
            System.out.println(position);
        }
        fc.close();
        System.out.println(new Date() +
                " file created " + (size * ONE_M) + " items by " +
                MUL + " long, " +
                "size: " + (file.length() / 1024 / 1024) + "M " +
                "in " + (System.currentTimeMillis() - start) + " msec");
    }

}
