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

package com.github.terma.fastselect.data;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class IntDataTest {

    @Test
    public void provideAllocatedSize() {
        IntData data = new IntData(100);
        Assert.assertEquals(Data.DEFAULT_SIZE, data.allocatedSize());

        for (int i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(Data.DEFAULT_SIZE + 100, data.allocatedSize());
    }

    @Test
    public void supportCompact() {
        IntData data = new IntData(100);
        for (int i = 0; i < 17; i++) data.add(i);
        Assert.assertEquals(116, data.allocatedSize());

        data.compact();

        Assert.assertEquals(17, data.allocatedSize());
        for (int i = 0; i < data.size(); i++) Assert.assertEquals(i, data.get(i));
    }

    @Test
    public void provideMemSize() {
        IntData data = new IntData(100);
        Assert.assertEquals(92, data.mem());

        for (int i = 0; i < 50; i++) data.add(i);
        Assert.assertEquals(492, data.mem());
    }

    @Test
    public void provideInc() {
        Assert.assertEquals(33, new IntData(33).inc());
    }

    @Test
    public void load() throws IOException {
        IntData data = new IntData(100);
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        int[] t = new int[]{0, 1, 2, 3, 4};
        ByteBuffer b = fc.map(FileChannel.MapMode.READ_WRITE, 0, Data.INT_BYTES * t.length);
        for (int l : t) b.putInt(l);
        fc.force(true);

        data.load(fc, 5);
        f.delete();

        Assert.assertEquals(data.size(), 5);
    }

    @Test
    public void saveAndLoad() throws IOException {
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        IntData data = new IntData(100);
        data.add((byte) -1);
        data.add((byte) 2);
        data.add((byte) 0);
        data.add(Integer.MIN_VALUE);
        data.add(Integer.MAX_VALUE);
        data.save(fc);

        fc.position(0);
        IntData data1 = new IntData(100);
        data1.load(fc, 5);

        f.delete();

        Assert.assertEquals(data1.size(), 5);
        Assert.assertEquals(Integer.MIN_VALUE, data1.get(3));
        Assert.assertEquals(Integer.MAX_VALUE, data1.get(4));
    }

}
