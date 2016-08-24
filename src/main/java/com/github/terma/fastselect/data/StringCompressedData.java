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

import com.github.terma.fastselect.utils.IOUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

@SuppressWarnings("WeakerAccess")
abstract public class StringCompressedData implements Data {

    protected void saveDictionary(final FileChannel fileChannel, final String[] values) throws IOException {
        int predictedSize = Data.INT_BYTES;
        for (String string : values) {
            predictedSize += Data.INT_BYTES + Data.SHORT_BYTES * (string == null ? 0 : string.length());
        }

        ByteBuffer buffer = ByteBuffer.allocate(predictedSize);

        buffer.putInt(values.length);
        for (String string : values) {
            IOUtils.writeString(buffer, string);
        }
        buffer.flip();
        fileChannel.write(buffer);
    }

    protected void saveDictionary(final FileChannel fileChannel, final List<String> values) throws IOException {
        int predictedSize = Data.INT_BYTES;
        for (String string : values) {
            predictedSize += Data.INT_BYTES + Data.SHORT_BYTES * (string == null ? 0 : string.length());
        }

        ByteBuffer buffer = ByteBuffer.allocate(predictedSize);

        buffer.putInt(values.size());
        for (String string : values) {
            IOUtils.writeString(buffer, string);
        }
        buffer.flip();
        fileChannel.write(buffer);
    }

}
