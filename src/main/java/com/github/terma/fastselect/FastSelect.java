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

package com.github.terma.fastselect;

import com.github.terma.fastselect.callbacks.*;
import com.github.terma.fastselect.data.*;
import com.github.terma.fastselect.utils.IOUtils;
import com.github.terma.fastselect.utils.MethodHandlerRepository;
import com.github.terma.fastselect.utils.ThreadUtils;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Compact in-memory storage with fast search.
 * <p>
 * In Memory implementation for database table. Supports fast search by any combination of columns.
 * Internal storage has constant size for mem overhead.
 * <p>
 * Storage: Column oriented based on array
 * For example for normal Java object you have 12-16 bytes on object header.
 * Plus fields alignment depends on JVM and hardware architectures. Could be 4-8 bytes alignment.
 * Which means you for one object with one int field. You need to allocate 16 bytes on header and 8 bytes
 * on field on x64 machine with SunJVM. <a href="http://shipilev.net/blog/2014/heapdump-is-a-lie/">Details about JVM object mem layout</a>
 * When you add new object class internally extract required fields from that object and add this to
 * particular column data. <a href="http://the-paper-trail.org/blog/columnar-storage/">Columnar Storage</a>
 * <p>
 * As result you don't need to spend mem on millions of object headers and alignment.
 * The downside of that each time when you need to get data back as object some time need to be spend on
 * recreation of your object. Mem as well. You can use references field in data at least for current implementation.
 * <h3>Implementation</h3>
 * <h4>Architecture</h4>
 * <pre>
 *                Columns
 *                0     n
 *  Position 0 |----| |---| &lt; Block 0
 *             | 11 | | 1 |     Start position: 0, size: 4
 *             |  0 | | 2 |     Column 0 &gt; bloom filter: 0,0,0,0
 *             | 44 | | 3 |     Column n &gt; range: [1,3]
 *             | 44 | | 3 |
 *             | 12 | | 0 | &lt; Block m
 *             | 22 | | 0 |     Start position: 0, size: 1
 *           m |----| |---|     ...
 * </pre>
 * <h4>Search Algorithm</h4>
 * <pre>
 *      Start
 *       |
 *      Take next block &lt;----------------------------------- --------
 *       |                                                 |        |
 *      Yes                                               No       No Blocks
 *       |                                                 |        |
 *   -&gt; Requested values present in range or bloom filter --        |
 *   |  (depends on column type) &lt;-----                             |
 *   |   |                            |                             |
 *   |  Yes                          No                             |
 *   |   |                            |                             |
 *   -&gt; Take position j in block &lt;----------------                  |
 *   |   |                                       |                  |
 *   |  Yes                                      No                 |
 *   |   |                                       |                  |
 *   |  If data in position accepted by request --                  |
 *   |   |                                                          |
 *   |  Yes                                                         |
 *   |   |                                                          |
 *   |  Call Callback with position                                 |
 *   |   |                                                          |
 *   -- Goto next position in block or next block                   |
 *                                                                  |
 *                                                                 End
 * </pre>
 * Fast Search based two step search algorithm (<a href="https://en.wikipedia.org/wiki/Bloom_filter">Bloom Filter</a>
 * + direct scan within block)
 * <h4>Marshalling and unmarshalling.</h4>
 * Because storage uses non object layout. That's provide huge improvement for memory and performance.
 * Downside of that we need to extract field values from object on add and build new object when
 * return data. That's why this storage not good for selection big portion of data compare to original result set.
 * <p>
 * We use {@link MethodHandle#invoke(Object...)} here to field values from object.
 * You could be surprised but it has same performance as normal reflect. Other alternative
 * which you can think will be faster {@link MethodHandle#invokeExact(Object...)} however it's not.
 * <p>
 * More information about that:
 * <a href="https://gist.github.com/raphw/881e1745996f9d314ab0#file-result-field-txt">
 * https://gist.github.com/raphw/881e1745996f9d314ab0#file-result-field-txt</a>
 * <p>
 * <h3>JMX</h3>
 * To see static of {@link FastSelect} you can use {@link com.github.terma.fastselect.jmx.FastSelectMXBeanImpl}
 * <h3>Save / Load from file</h3>
 * FastSelect support save and load from {@link FileChannel}
 * <p>
 * Binary file format:
 * <pre>
 * # header
 * format-version (int)
 * row-count (int)
 * columns-count (int)
 *
 * # columns meta info
 * column-0-data-class-size (int)
 * column-0-data-class (byte[])
 * column-0-name-size (int)
 * column-0-name (byte[])
 * column-0-data-file-position (long)
 * column-0-data-byte-size (int)
 * ...
 * column-M
 *
 * # columns data
 * data 0 (type specific format)
 * ...
 * data n
 * EOF
 * </pre>
 * To find details about specific column (data) format please check implementation of {@link Data}
 *
 * @author Artem Stasiuk
 * @see FastSelectBuilder
 * @see ArrayLayoutCallback
 * @see com.github.terma.fastselect.callbacks.GroupCountCallback
 * @see com.github.terma.fastselect.callbacks.MultiGroupCountCallback
 */
@SuppressWarnings("WeakerAccess")
@ThreadSafe
public final class FastSelect<T> {

    private final int[] blockSizes;
    private final Class<T> dataClass;
    private final MethodHandlerRepository mhRepo;
    private final Block rootBlock;
    private final List<Column> columns;
    private final Map<String, Column> columnsByNames;

    /**
     * @param blockSize - block size
     * @param dataClass - data class
     * @param columns   list of {@link FastSelect.Column} data for them will be extracted from dataClass object
     *                  and used for filtering.
     */
    // todo don't throw exception when inc is small than data to add
    // todo add description for block sizes parameter
    public FastSelect(final int blockSize, final Class<T> dataClass, final List<Column> columns) {
        // todo check that block sizes not zero or negative or empty
        this.blockSizes = new int[]{blockSize};
        this.dataClass = dataClass;
        this.columns = columns;
        this.rootBlock = new SuperBlock(-1, Integer.MAX_VALUE);
        for (int i = 0; i < columns.size(); i++) columns.get(i).index = i;
        this.columnsByNames = initColumnsByName(columns);
        this.mhRepo = new MethodHandlerRepository(dataClass, getColumnsAsMap(columns));

        for (Column column : columns) {
            column.getter = mhRepo.get(column.name);
            column.setter = mhRepo.set(column.name);
        }
    }

    private static Map<String, Column> initColumnsByName(List<Column> columns) {
        Map<String, Column> r = new HashMap<>();
        for (Column column : columns) r.put(column.name, column);
        return r;
    }

    private static Map<String, Class> getColumnsAsMap(List<Column> columns) {
        Map<String, Class> r = new HashMap<>();
        for (Column column : columns) r.put(column.name, column.type);
        return r;
    }

    public void addAll(final List<T> data) {
        rootBlock.add(data, 0, -1);
    }

    /**
     * Beta version
     * <p>
     * Format described in javadoc for class
     *
     * @param fileChannel - fileChannel
     * @throws IOException
     * @see Saver
     */
    public void save(final FileChannel fileChannel) throws IOException {
        try (Saver saver = new Saver(columns, size(), fileChannel)) {
            for (Column column : columns) saver.saveData(column.data);
            saver.save();
        }
    }

    /**
     * Beta version
     * <p>
     * Format described in javadoc for class
     *
     * @param fileChannel  - fc
     * @param threadCounts - for parallel load
     * @throws IOException
     */
    public void load(final FileChannel fileChannel, final int threadCounts) throws IOException {
        if (fileChannel.size() > 0) {
            final int version = IOUtils.readInt(fileChannel);
            if (version == 0) {
                throw new IllegalArgumentException("Corrupted data! Ensure that you create dump properly.");
            } else if (version != Data.STORAGE_FORMAT_VERSION)
                throw new IllegalArgumentException("Unsupported format version: " + version
                        + ", expected: " + Data.STORAGE_FORMAT_VERSION);

            final int size = IOUtils.readInt(fileChannel);
            final int columnCount = IOUtils.readInt(fileChannel);

            final ExecutorService executorService = Executors.newFixedThreadPool(threadCounts);
            final Set<String> loadedColumns = new HashSet<>();
            final List<Future<Object>> futures = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                final String dataClass = IOUtils.readString(fileChannel);
                final String columnName = IOUtils.readString(fileChannel);
                loadedColumns.add(columnName);
                final long position = IOUtils.readLong(fileChannel);
                final int bytesSize = IOUtils.readInt(fileChannel);

                futures.add(executorService.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        final ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, position, bytesSize);
                        Column column = columnsByNames.get(columnName);
                        if (column != null) column.data.load(dataClass, buffer, size);
                        return null;
                    }
                }));
            }
            ThreadUtils.getAll(futures);
            executorService.shutdown();

            for (Column column : columns) {
                if (!loadedColumns.contains(column.name)) column.data.init(size);
            }
        }
        rootBlock.init();
    }

    /**
     * To free preallocated (no real data) space.
     * <p>
     * In general when you add data to FastSelect. Column sizes don't same with columns allocated space.
     * That's because allocation of new space is relative heavy operation. So FastSelect increase column size
     * for new data by steps.
     * <p>
     * For example default size is 16. So when you are going to add 17th item column allocated space will be not
     * enough. As result FastSelect will try to allocated more space for column. Means increase column space on
     * 300000 (default).
     * <p>
     * Result. In FastSelect you have 17 items. However space allocated for 300016 items. Some times you don't
     * want add more and you need to clean up allocated space. That's method for that.
     */
    public void compact() {
        for (final Column column : columns) column.compact();
    }

    public List<T> select(final Request... where) {
        ListCallback<T> result = new ListCallback<>();
        select(where, result);
        return result.getResult();
    }

    /**
     * Main search method. Find data good for your where (filter) condition and call {@link ArrayLayoutCallback#data(int)}
     * for each of item in result.
     *
     * @param where    filter criteria. Could be any combination of fields which you provide as columns during
     *                 {@link FastSelect} creation.
     * @param callback callback. Will be called for each item accepted by where.
     */
    public void select(final Request[] where, final ArrayLayoutCallback callback) {
        prepareRequest(where);
        rootBlock.select(where, callback);
    }

    public void select(final ArrayLayoutLimitCallback callback, final Request... where) {
        prepareRequest(where);
        rootBlock.select(where, callback);
    }

    public void select(final Request[] where, final Callback<T> callback) {
        select(where, new ArrayToObjectCallback<>(dataClass, columns, mhRepo, callback));
    }

    public void select(final LimitCallback<T> callback, final Request... where) {
        select(new ArrayToObjectLimitCallback<>(dataClass, columns, mhRepo, callback), where);
    }

    public List<Integer> selectPositions(final Request[] where) {
        final List<Integer> positions = new ArrayList<>();
        select(where, new ArrayLayoutCallback() {
            @Override
            public void data(int position) {
                positions.add(position);
            }
        });
        return positions;
    }

    public void selectAndSort(final Request[] where, final LimitCallback<T> callback, final String... sortBy) {
        final List<Integer> positions = selectPositions(where);

        final List<Data> sortData = new ArrayList<>();
        for (String s : sortBy) sortData.add(columnsByNames.get(s).data);

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer position1, Integer position2) {
                for (Data data : sortData) {
                    int r = data.compare(position1, position2);
                    if (r != 0) return r;
                }
                return 0;
            }
        });

        createObjects(callback, positions);
    }

    /**
     * Support comparator based sorting. So you can implement any kind of sorting.
     * <p>
     * WARNING! This method uses comparator which provided by client. So any performance issues
     * in comparator code will affect performance for this method.
     *
     * @param where      - where
     * @param callback   - callback
     * @param comparator - comparator
     */
    public void selectAndSort(final Request[] where, final LimitCallback<T> callback,
                              final FastSelectComparator comparator) {
        final List<Integer> positions = selectPositions(where);

        final Data[] sortData = new Data[columns.size()];
        for (int i = 0; i < columns.size(); i++) sortData[i] = columns.get(i).data;

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer position1, Integer position2) {
                return comparator.compare(sortData, position1, position2);
            }
        });

        createObjects(callback, positions);
    }

    public void selectAndSort(final Request[] where, final FastSelectComparator comparator,
                              final ArrayLayoutCallback callback) {
        final List<Integer> positions = selectPositions(where);

        final Data[] sortData = new Data[columns.size()];
        for (int i = 0; i < columns.size(); i++) sortData[i] = columns.get(i).data;

        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer position1, Integer position2) {
                return comparator.compare(sortData, position1, position2);
            }
        });

        for (Integer position : positions) callback.data(position);
    }

    public List<T> selectAndSort(final Request[] where, final FastSelectComparator comparator) {
        ListLimitCallback<T> result = new ListLimitCallback<>(Integer.MAX_VALUE);
        selectAndSort(where, result, comparator);
        return result.getResult();
    }

    public List<T> selectAndSort(final Request[] where, final String... sortBy) {
        ListLimitCallback<T> result = new ListLimitCallback<>(Integer.MAX_VALUE);
        selectAndSort(where, result, sortBy);
        return result.getResult();
    }

    /**
     * Create new instance of {@link FastSelect} with same data class, columns and block size
     * with data which accepted by passed where.
     * <p>
     * Allocated size will be same with original. Use {@link FastSelect#compact()} to trim
     *
     * @param where - conditions to copy only specific data
     * @return - copy
     */
    public FastSelect<T> copy(final Request[] where) {
        final byte[] needToCopy = new byte[size()];
        select(where, new ArrayLayoutCallback() {
            @Override
            public void data(int position) {
                needToCopy[position] = 1;
            }
        });

        final List<Column> columnsCopy = new ArrayList<>();
        for (Column column : columns) columnsCopy.add(column.copy(needToCopy));
        FastSelect<T> copy = new FastSelect<>(blockSizes[0], dataClass, columnsCopy);
        copy.rootBlock.init();
        return copy;
    }

    public int blockTouch(final ColumnRequest[] where) {
        prepareRequest(where);
        return rootBlock.blockTouch(where);
    }

    /**
     * Count of items
     *
     * @return - count
     */
    public int size() {
        return columns.iterator().next().data.size();
    }

    /**
     * Size in heap memory for current instance with data
     *
     * @return - size in bytes
     */
    public long mem() {
        long mem = 0;
        for (final Column column : columns) mem += column.mem();
        return mem;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Map<String, Column> getColumnsByNames() {
        return columnsByNames;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {blockSizes: " + Arrays.toString(blockSizes) + ", data: " + size()
                + ", indexes: " + columns + ", class: " + dataClass + "}";
    }

    public int dataBlockSize() {
        return blockSizes[blockSizes.length - 1];
    }

    private void prepareRequest(final Request[] where) {
        for (final Request condition : where) condition.prepare(columnsByNames);
    }

    private void createObjects(final LimitCallback<T> callback, final List<Integer> positions) {
        try {
            for (Integer p : positions) {
                final T o = dataClass.newInstance();
                for (final Column column : columns) {
                    column.setter.invoke(o, column.data.get(p));
                }
                callback.data(o);
                if (callback.needToStop()) return;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public int allocatedSize() {
        return columns.get(0).allocatedSize();
    }

    public static class Column {

        public final String name;
        public final Data data;
        final Class type;
        final Class annotationType;
        MethodHandle getter;
        MethodHandle setter;
        int index;

        public Column(final String name, final Class type, final Class annotationType, final int inc) {
            this.name = name;
            this.type = type;
            this.annotationType = annotationType;

            if (type == long.class) {
                data = new LongData(inc);
            } else if (type == long[].class) {
                data = new MultiLongData(inc);
            } else if (type == int[].class) {
                data = new MultiIntData(inc);
            } else if (type == short[].class) {
                data = new MultiShortData(inc);
            } else if (type == byte[].class) {
                data = new MultiByteData(inc);
            } else if (type == int.class) {
                data = new IntData(inc);
            } else if (type == short.class) {
                data = new ShortData(inc);
            } else if (type == byte.class) {
                data = new ByteData(inc);
            } else if (type == String.class && annotationType == StringCompressedByte.class) {
                data = new StringCompressedByteData(inc);
            } else if (type == String.class && annotationType == StringCompressedShort.class) {
                data = new StringCompressedShortData(inc);
            } else if (type == String.class && annotationType == StringCompressedInt.class) {
                data = new StringCompressedIntData(inc);
            } else if (type == String.class) {
                data = new StringData(inc);
            } else if (type == double.class) {
                data = new DoubleData(inc);
            } else {
                throw new IllegalArgumentException("Unsupportable column type: " + type
                        + ". Support byte,short,int,long,double,byte[],short[],int[],long[]!");
            }
        }

        public Column(final String name, final Class type, final int inc) {
            this(name, type, null, inc);
        }

        public Column(final Column column, byte[] needToCopy) {
            this.name = column.name;
            this.type = column.type;
            this.annotationType = column.annotationType;
            this.data = column.data.copy(needToCopy);
        }

        public void compact() {
            data.compact();
        }

        @Override
        public String toString() {
            return "Column {name: " + name + ", type: " + type + '}';
        }

        public int valueAsInt(final int position) {
            if (type == byte.class) {
                return ((ByteData) data).data[position];
            } else if (type == short.class) {
                return ((ShortData) data).data[position];
            } else if (type == int.class) {
                return ((IntData) data).data[position];
            } else if (type == long.class) {
                return (int) ((LongData) data).data[position];
            } else {
                throw new IllegalArgumentException("Unknown column type: " + type);
            }
        }

        public int allocatedSize() {
            return data.allocatedSize();
        }

        public long mem() {
            return data.mem();
        }

        public int size() {
            return data.size();
        }

        public Class getType() {
            return type;
        }

        public Column copy(byte[] needToCopy) {
            return new Column(this, needToCopy);
        }

    }

    private final class SuperBlock extends Block {

        final int level;
        final int maxSize;
        final List<Block> blocks = new ArrayList<>();

        SuperBlock(int level, int maxSize) {
            this.level = level;
            this.maxSize = maxSize;

            for (Column ignored : columns) {
                columnBitSets.add(new BitSet());
            }
        }

        @Override
        public String toString() {
            return "SuperBlock {level: " + level + ", maxSize: " + maxSize + ", blocks: " + blocks.size() + "}";
        }

        @Override
        int free() {
            throw new UnsupportedOperationException();
        }

        private boolean inBlock(Request[] requests, Block block) {
            for (Request request : requests) {
                if (!request.checkBlock(block)) return false;
            }
            return true;
        }

        @Override
        void select(Request[] where, ArrayLayoutCallback callback) {
            for (final Block block : blocks) {
                if (!inBlock(where, block)) continue;
                block.select(where, callback);
            }
        }

        @Override
        int blockTouch(Request[] where) {
            int c = 0;
            for (final Block block : blocks) {
                if (!inBlock(where, block)) continue;
                c += block.blockTouch(where);
            }
            return c;
        }

        @Override
        void select(Request[] where, ArrayLayoutLimitCallback callback) {
            for (final Block block : blocks) {
                if (!inBlock(where, block)) continue;
                block.select(where, callback);

                if (callback.needToStop()) return;
            }
        }

        @Override
        public void init() {
            int start = 0;
            int size = columns.iterator().next().size();
            while (start < size) {
                int blockSize = Math.min(size - start, dataBlockSize());
                DataBlock block = new DataBlock(start, blockSize);
                block.init();
                blocks.add(block);
                start += block.getMaxSize();
            }
        }

        @Override
        void add(List dataToAdd, int addFrom, int addTo) {
            int position = 0;
            int updatePosition = Math.max(0, blocks.size() - 1);

            if (blocks.isEmpty()) {
                Block block = new DataBlock(columns.get(0).data.size());
                blocks.add(block);
            }

            while (position < dataToAdd.size()) {
                Block block = blocks.get(blocks.size() - 1);
                final int free = block.free();
                if (free == 0) {
                    block = new DataBlock(columns.get(0).data.size());
                    blocks.add(block);
                }

                int toAdd = Math.min(free, dataToAdd.size() - position);
                block.add(dataToAdd, position, position + toAdd);
                position += toAdd;
            }

            for (int i = updatePosition; i < blocks.size(); i++) {
                Block block = blocks.get(i);
                for (Column column : columns) {
                    BitSet bitSet = columnBitSets.get(column.index);
                    bitSet.or(block.columnBitSets.get(column.index));
                }
            }
        }

    }

    private final class DataBlock extends Block {

        private final int start;
        private int size;

        private DataBlock(final int start) {
            this(start, 0);
        }

        private DataBlock(final int start, final int size) {
            this.start = start;
            this.size = size;
            for (Column ignored : columns) {
                columnBitSets.add(new BitSet());
                ranges.add(new Range());
            }
        }

        @Override
        public String toString() {
            return "DataBlock {maxSize: " + getMaxSize() + ", start: " + start + ", size: " + size + "}";
        }

        /**
         * @param dataToAdd - block of data to add
         * @param addFrom   - addFrom position in dataToAdd (inclusive)
         * @param addTo     - addTo position in dataToAdd (exclusive)
         */
        @Override
        void add(List dataToAdd, int addFrom, int addTo) {
            final int additionalSize = addTo - addFrom;

            try {
                for (final Column column : columns) {
                    final MethodHandle methodHandle = column.getter;
                    final Range range = ranges.get(column.index);

                    if (column.type == long.class) {
                        final LongData data = (LongData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            long v = (long) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                            range.update(v);
                        }

                    } else if (column.type == long[].class) {
                        MultiLongData data = (MultiLongData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            long[] v = (long[]) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                        }

                    } else if (column.type == int[].class) {
                        MultiIntData data = (MultiIntData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            int[] v = (int[]) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                        }

                    } else if (column.type == short[].class) {
                        final MultiShortData data = (MultiShortData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            short[] v = (short[]) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                            // set all bits
                            for (short v1 : v) setColumnBitSet(column, v1);
                        }

                    } else if (column.type == byte[].class) {
                        MultiByteData data = (MultiByteData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            byte[] v = (byte[]) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                            // set all bits
                            for (byte v1 : v) setColumnBitSet(column, v1);
                        }

                    } else if (column.type == int.class) {
                        final IntData data = (IntData) column.data;
                        data.allocate(additionalSize);

                        for (int i = addFrom, position = this.start + size; i < addTo; i++, position++) {
                            int v = (int) methodHandle.invoke(dataToAdd.get(i));
                            data.set(position, v);
                            range.update(v);
                        }

                    } else if (column.type == short.class) {
                        final ShortData data = (ShortData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            short v = (short) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                            setColumnBitSet(column, v);
                            range.update(v);
                        }

                    } else if (column.type == byte.class) {
                        final ByteData data = (ByteData) column.data;
                        data.allocate(additionalSize);

                        for (int i = addFrom, position = this.start + size; i < addTo; i++, position++) {
                            byte v = (byte) methodHandle.invoke(dataToAdd.get(i));
                            data.set(position, v);
                            setColumnBitSet(column, v);
                            range.update(v);
                        }

                    } else if (column.type == String.class && column.annotationType == StringCompressedByte.class) {
                        final StringCompressedByteData data = (StringCompressedByteData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            String v = (String) methodHandle.invoke(dataToAdd.get(i));
                            byte position = data.add(v);
                            setColumnBitSet(column, position);
                        }

                    } else if (column.type == String.class && column.annotationType == StringCompressedShort.class) {
                        final StringCompressedShortData data = (StringCompressedShortData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            String v = (String) methodHandle.invoke(dataToAdd.get(i));
                            short position = data.add(v);
                            setColumnBitSet(column, position);
                        }

                    } else if (column.type == String.class && column.annotationType == StringCompressedInt.class) {
                        final StringCompressedIntData data = (StringCompressedIntData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            String v = (String) methodHandle.invoke(dataToAdd.get(i));
                            int position = data.add(v);
                            range.update(position);
                        }

                    } else if (column.type == String.class) {
                        final StringData data = (StringData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            String v = (String) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                        }

                    } else if (column.type == double.class) {
                        final DoubleData data = (DoubleData) column.data;
                        for (int i = addFrom; i < addTo; i++) {
                            double v = (double) methodHandle.invoke(dataToAdd.get(i));
                            data.add(v);
                        }

                    } else {
                        throw new IllegalArgumentException("Unsupported column type: " + column.type + "!");
                    }
                }
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            size += additionalSize;
        }

        @Override
        void select(Request[] where, ArrayLayoutCallback callback) {
            final int end = start + size;
            opa:
            for (int i = start; i < end; i++) {
                for (final Request request : where) {
                    if (!request.checkValue(i)) continue opa;
                }

                callback.data(i);
            }
        }

        @Override
        int blockTouch(Request[] where) {
            return 1;
        }

        @Override
        void select(Request[] where, ArrayLayoutLimitCallback callback) {
            final int end = start + size;
            opa:
            for (int i = start; i < end; i++) {
                for (final Request request : where) {
                    if (!request.checkValue(i)) continue opa;
                }

                callback.data(i);

                if (callback.needToStop()) return;
            }
        }

        @Override
        public void init() {
            final int end = start + size;

            for (final Column column : columns) {

                if (column.type == long.class) {
                    final LongData data = (LongData) column.data;
                    final Range range = ranges.get(column.index);
                    for (int i = start; i < end; i++) range.update(data.data[i]);

                } else if (column.type == short[].class) {
                    final MultiShortData data = (MultiShortData) column.data;
                    for (int i = start; i < end; i++) {
                        short[] v = (short[]) data.get(i);
                        for (short v1 : v) setColumnBitSet(column, v1);
                    }

                } else if (column.type == byte[].class) {
                    MultiByteData data = (MultiByteData) column.data;
                    for (int i = start; i < end; i++) {
                        byte[] v = (byte[]) data.get(i);
                        for (byte v1 : v) setColumnBitSet(column, v1);
                    }

                } else if (column.type == int.class) {
                    final IntData data = (IntData) column.data;
                    final Range range = ranges.get(column.index);
                    for (int i = start; i < end; i++) range.update(data.data[i]);

                } else if (column.type == short.class) {
                    final ShortData data = (ShortData) column.data;
                    for (int i = start; i < end; i++) setColumnBitSet(column, data.data[i]);

                } else if (column.type == byte.class) {
                    final ByteData data = (ByteData) column.data;
                    for (int i = start; i < end; i++) setColumnBitSet(column, data.data[i]);

                } else if (column.type == String.class && column.annotationType == StringCompressedByte.class) {
                    final byte[] data = ((StringCompressedByteData) column.data).data.data;
                    for (int i = start; i < end; i++) setColumnBitSet(column, data[i]);

                } else if (column.type == String.class && column.annotationType == StringCompressedShort.class) {
                    final short[] data = ((StringCompressedShortData) column.data).data.data;
                    for (int i = start; i < end; i++) setColumnBitSet(column, data[i]);

                } else if (column.type == String.class && column.annotationType == StringCompressedInt.class) {
                    final int[] data = ((StringCompressedIntData) column.data).data.data;
                    final Range range = ranges.get(column.index);
                    for (int i = start; i < end; i++) range.update(data[i]);

                }
            }
        }

        int free() {
            return getMaxSize() - size;
        }

        public int getMaxSize() {
            return blockSizes[blockSizes.length - 1];
        }

    }

}
