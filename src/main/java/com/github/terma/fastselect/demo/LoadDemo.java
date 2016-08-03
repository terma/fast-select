package com.github.terma.fastselect.demo;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.FastSelectBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * real load read x30000000 2746M in 22692 msec
 * no load read x30000000 2746M in 2692 msec
 * load cross longBuffer read x30000000 2746M in 9668 msec
 */
public class LoadDemo {

    private static final int ONE_M = 1000 * 1000;
    private static final int MUL = 12;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("use: java -jar fast-select.jar <file-path> <size in m> <write|read>");
            System.exit(1);
        }

        final boolean write = "write".equals(args[2]);
        final String filePath = args[0];
        final File file = new File(filePath);
        final int size = Integer.parseInt(args[1]);

        if (write) createFile(file, size);
        else loadFastSelect(file);
    }

    private static void loadFastSelect(File file) throws IOException {
        long start1 = System.currentTimeMillis();
        FastSelect<Data> fastSelect = new FastSelectBuilder<>(Data.class).create();
        FileChannel fc1 = new RandomAccessFile(file, "r").getChannel();
        fastSelect.load(fc1);
        fc1.close();
        System.out.println("read x" + fastSelect.size() + " " + (fastSelect.mem() / 1024 / 1024) + "M " +
                "in " + (System.currentTimeMillis() - start1) + " msec");
    }

    private static void createFile(File file, int size) throws IOException {
        System.out.println(new Date() + " creating file...");
        file.delete();

        final long[] data = new long[size * ONE_M];
        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate(4).putInt(size * ONE_M).flip());
        for (long j = 0; j < MUL; j++) {
//            LongBuffer b = fc.map(FileChannel.MapMode.READ_WRITE, fc.position(), 8 * data.length).asLongBuffer();
            ByteBuffer b = fc.map(FileChannel.MapMode.READ_WRITE, fc.position(), 8 * data.length);
//            b.put(data);
            for (long l : data) b.putLong(l);
            System.out.println(fc.position() + b.position());
            fc.position(fc.position() + b.position());
        }
        fc.close();
        System.out.println(new Date() +
                " file created " + (size * ONE_M) + " items by " +
                MUL + " long, " +
                "size: " + (file.length() / 1024 / 1024) + "M " +
                "in " + (System.currentTimeMillis() - start) + " msec");
    }

    public static class Data {
        public long l1;
        public long l2;
        public long l3;
        public long l4;
        public long l5;
        public long l6;
        public long l7;
        public long l8;
        public long l9;
        public long l10;
        public long l11;
        public long l12;
    }

}
