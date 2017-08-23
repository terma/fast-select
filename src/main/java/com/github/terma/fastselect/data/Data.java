/*
Copyright 2015-2017 Artem Stasiuk

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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public interface Data {

    int STORAGE_FORMAT_VERSION = 1;

    int DEFAULT_SIZE = 16;

    int OBJECT_HEADER_BYTES = 16;
    int REFERENCE_BYTES = 8;
    int SHORT_BYTES = 2;
    int INT_BYTES = 4;
    int LONG_BYTES = 8;
    int DOUBLE_BYTES = 8;

    /**
     * @return - amount of bytes which column data will take in {@link FileChannel}
     */
    int getDiskSpace();

    /**
     * Opposite to {@link Data#load(String, ByteBuffer, int)}
     * <p>
     * To save entire {@link com.github.terma.fastselect.FastSelect} use
     * {@link com.github.terma.fastselect.FastSelect#save(FileChannel)}
     *
     * @param buffer - b
     * @throws IOException - IO exception
     */
    void save(ByteBuffer buffer) throws IOException;

    /**
     * Opposite to {@link Data#save(ByteBuffer)}
     * <p>
     * To load entire {@link com.github.terma.fastselect.FastSelect} use
     * {@link com.github.terma.fastselect.FastSelect#load(FileChannel, int)}
     *
     * @param dataClass - dc
     * @param buffer    - b
     * @param size      - s
     * @throws IOException - IO exception
     */
    void load(String dataClass, ByteBuffer buffer, int size) throws IOException;

    /**
     * Returns value store at position for that data (column)
     * Type of value will be exact of type your field in data class.
     *
     * @param position - position
     * @return - value store at position
     */
    Object get(int position);

    int compare(int position1, int position2);

    int hashCode(int position);

    /**
     * Init data with default value for requested size.
     *
     * @param size - requested size
     */
    void init(int size);

    void compact();

    int size();

    int allocatedSize();

    /**
     * Approximate size of Data structure in heap memory.
     *
     * @return - size in bytes
     */
    long mem();

    int inc();

    Data copy(byte[] needToCopy);

}
