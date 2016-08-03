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

public class StringCompressedShortDataTest {

    @Test
    public void load() throws IOException {
        StringCompressedShortData data = new StringCompressedShortData(100);
        File f = Files.createTempFile("a", "b").toFile();
        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(2).flip());

        byte[] string1 = "ABACA".getBytes();
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(string1.length).flip());
        fc.write(ByteBuffer.wrap(string1));

        byte[] string2 = "XY".getBytes();
        fc.write((ByteBuffer) ByteBuffer.allocate((int) Data.INT_BYTES).putInt(string2.length).flip());
        fc.write(ByteBuffer.wrap(string2));

        fc.write((ByteBuffer) ByteBuffer.allocate(100)
                .putShort((short) 0).putShort((short) 1).putShort((short) 1)
                .putShort((short) 1).putShort((short) 1).flip());

        fc.position(0);
        data.load(fc, 5);
        f.delete();

        Assert.assertEquals(data.size(), 5);
        Assert.assertEquals("ABACA", data.get(0));
        Assert.assertEquals("XY", data.get(1));
    }

}
