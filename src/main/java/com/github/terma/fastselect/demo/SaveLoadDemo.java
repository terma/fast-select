package com.github.terma.fastselect.demo;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.FastSelectBuilder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * real load read x30000000 2746M in 22692 msec
 * no load read x30000000 2746M in 2692 msec
 * load cross longBuffer read x30000000 2746M in 9668 msec
 */
public class SaveLoadDemo {

    private static final int ONE_M = 1000 * 1000;
    private static final int MUL = 12;

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("use: java -jar fast-select.jar <file-path> <size in m> <load threads> <write|read>");
            System.exit(1);
        }

        final boolean write = "write".equals(args[3]);
        final String filePath = args[0];
        final File file = new File(filePath);
        final int size = Integer.parseInt(args[1]);
        final int threadCounts = Integer.parseInt(args[2]);

        if (write) createFile(file, size);
        else loadFastSelect(file, threadCounts);
    }

    private static void loadFastSelect(final File file, final int threadCounts) throws IOException {
        final long start = System.currentTimeMillis();
        final FastSelect<Data1> fastSelect = new FastSelectBuilder<>(Data1.class).create();
        final FileChannel fc1 = new RandomAccessFile(file, "r").getChannel();
        fastSelect.load(fc1, threadCounts);
        fc1.close();
        System.out.println("read x" + fastSelect.size() + " " + (fastSelect.mem() / 1024 / 1024) + "M " +
                "in " + (System.currentTimeMillis() - start) + " msec");
    }

    private static void createFile(final File file, final int size) throws IOException {
        System.out.println(new Date() + " creating file...");
        if (file.exists() && !file.delete()) {
            System.out.println("Can't remove file: " + file);
            System.exit(2);
        }

        FastSelect<Data1> fastSelect = new FastSelectBuilder<>(Data1.class).create();
        List<Data1> buffer = new ArrayList<>();
        for (int i = 0; i < size * ONE_M; i++) {
            Data1 data1 = new Data1();
            buffer.add(data1);

            if (buffer.size() % ONE_M == 0) {
                fastSelect.addAll(buffer);
                buffer.clear();
                System.out.print(".");
            }
        }
        fastSelect.addAll(buffer);
        System.out.println();
        System.out.println("cache created");

        final long start = System.currentTimeMillis();
        final FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        fastSelect.save(fc);
        fc.close();
        System.out.println(new Date() +
                " file created " + (size * ONE_M) + " items by " +
                MUL + " long, " +
                "size: " + (file.length() / 1024 / 1024) + "M " +
                "in " + (System.currentTimeMillis() - start) + " msec");
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class Data1 {
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
