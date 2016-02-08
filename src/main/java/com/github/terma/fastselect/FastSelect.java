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
import com.github.terma.fastselect.utils.MethodHandlerRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

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
 * <p>
 * Search:
 * Fast Search based two step search algorithm (<a href="https://en.wikipedia.org/wiki/Bloom_filter">Bloom Filter</a>
 * + direct scan within block)
 * <p>
 * <h3>Implementation Points</h3>
 * <p>
 * <h4>Marshalling and unmarshalling.</h4>
 * <p>
 * Because storage uses non object layout. That's provide huge improvement for memory and performance.
 * Downside of that we need to extract field values from object on add and build new object when
 * return data. That's why this storage not good for selection big portion of data compare to original result set.
 * <p>
 * We use {@link MethodHandle#invoke(Object...)} here to field values from object.
 * You could be surprised but it has same performance as normal reflect. Other alternative
 * which you think is faster {@link MethodHandle#invokeExact(Object...)} however it's not.
 * <p>
 * More information about that:
 * <a href="https://gist.github.com/raphw/881e1745996f9d314ab0#file-result-field-txt">
 * https://gist.github.com/raphw/881e1745996f9d314ab0#file-result-field-txt</a>
 *
 * @author Artem Stasiuk
 * @see ArrayLayoutCallback
 * @see com.github.terma.fastselect.callbacks.GroupCountCallback
 * @see com.github.terma.fastselect.callbacks.MultiGroupCountCallback
 */
@ThreadSafe
public final class FastSelect<T> {

    private final static int[] DEFAULT_BLOCK_SIZES = new int[]{1000};

    private final static long MIN_COLUMN_BIT = 0;

    private final int[] blockSizes;
    private final Class<T> dataClass;
    private final MethodHandlerRepository mhRepo;
    private final Block rootBlock;
    private final List<Column> columns;
    private final Map<String, Column> columnsByNames;

    /**
     * @param blockSizes - block sizes
     * @param dataClass  - data class
     * @param columns    list of {@link FastSelect.Column} data for them will be extracted from dataClass object
     *                   and used for filtering.
     */
    // todo add description for block sizes parameter
    public FastSelect(final int[] blockSizes, final Class<T> dataClass, final List<Column> columns) {
        // todo check that block sizes not zero or negative or empty
        this.blockSizes = Arrays.copyOf(blockSizes, blockSizes.length);
        this.dataClass = dataClass;
        this.columns = columns;
        this.rootBlock = new SuperBlock(-1, Integer.MAX_VALUE);
        for (int i = 0; i < columns.size(); i++) columns.get(i).index = i;
        this.columnsByNames = initColumnsByName(columns);
        this.mhRepo = new MethodHandlerRepository(dataClass, getColumnsAsMap(columns));

        for (Column column : columns) {
            column.getter = mhRepo.get(column.name);
        }
    }

    public FastSelect(final int blockSize, final Class<T> dataClass, final Column... columns) {
        this(blockSize, dataClass, Arrays.asList(columns));
    }

    public FastSelect(final int blockSize, final Class<T> dataClass, final List<Column> columns) {
        this(new int[]{blockSize}, dataClass, columns);
    }

    public FastSelect(final int[] blockSizes, final Class<T> dataClass) {
        this(blockSizes, dataClass, getColumnsFromDataClass(dataClass));
    }

    /**
     * Derive list of columns from class fields (exclude any inherited)
     *
     * @param blockSize - block size
     * @param dataClass - data class
     */
    public FastSelect(final int blockSize, final Class<T> dataClass) {
        this(blockSize, dataClass, getColumnsFromDataClass(dataClass));
    }

    public FastSelect(final Class<T> dataClass) {
        this(DEFAULT_BLOCK_SIZES, dataClass, getColumnsFromDataClass(dataClass));
    }

    public FastSelect(final Class<T> dataClass, final List<Column> columns) {
        this(DEFAULT_BLOCK_SIZES, dataClass, columns);
    }

    private static List<Column> getColumnsFromDataClass(Class dataClass) {
        final List<Column> columns = new ArrayList<>();
        for (Field field : dataClass.getDeclaredFields()) {
            if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers()))
                columns.add(new Column(field.getName(), field.getType()));
        }
        return columns;
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

    public List<T> select(final AbstractRequest[] where) {
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
    public void select(final AbstractRequest[] where, final ArrayLayoutCallback callback) {
        prepareRequest(where);
        rootBlock.select(where, callback);
    }

    public int blockTouch(final AbstractRequest[] where) {
        prepareRequest(where);
        return rootBlock.blockTouch(where);
    }

    public void select(final AbstractRequest[] where, final ArrayLayoutLimitCallback callback) {
        prepareRequest(where);
        rootBlock.select(where, callback);
    }

    private void prepareRequest(final AbstractRequest[] where) {
        for (final AbstractRequest condition : where) {
            condition.column = columnsByNames.get(condition.name);

            if (condition.column == null) throw new IllegalArgumentException(
                    "Can't find requested column: " + condition.name + " in " + columns);

            condition.prepare();
        }
    }

    public void selectAndSort(final AbstractRequest[] where, final LimitCallback<T> callback, final String... sortBy) {
        final List<Integer> positions = new ArrayList<>();
        ArrayLayoutCallback myCallback = new ArrayLayoutCallback() {
            @Override
            public void data(int position) {
                positions.add(position);
            }
        };
        select(where, myCallback);

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

        for (Integer p : positions) {
            try {
                final T o = dataClass.newInstance();
                for (final FastSelect.Column column : columns) {
                    MethodHandle methodHandle = mhRepo.set(column.name);
                    methodHandle.invoke(o, column.data.get(p));
                }
                callback.data(o);
                if (callback.needToStop()) return;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<T> selectAndSort(final AbstractRequest[] where, final String... sortBy) {
        ListLimitCallback<T> result = new ListLimitCallback<>(Integer.MAX_VALUE);
        selectAndSort(where, result, sortBy);
        return result.getResult();
    }

    public void select(final AbstractRequest[] where, final Callback<T> callback) {
        select(where, new ArrayToObjectCallback<>(dataClass, columns, mhRepo, callback));
    }

    public void select(final AbstractRequest[] where, final LimitCallback<T> callback) {
        select(where, new ArrayToObjectLimitCallback<>(dataClass, columns, mhRepo, callback));
    }

    public int size() {
        return columns.iterator().next().data.size();
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

    public static class Column {

        public final String name;
        public final Data data;
        final Class type;
        MethodHandle getter;
        int index;

        public Column(final String name, final Class type) {
            this.name = name;
            this.type = type;

            if (type == long.class) {
                data = new LongData();
            } else if (type == long[].class) {
                data = new MultiLongData();
            } else if (type == short[].class) {
                data = new MultiShortData();
            } else if (type == byte[].class) {
                data = new MultiByteData();
            } else if (type == int.class) {
                data = new IntData();
            } else if (type == short.class) {
                data = new ShortData();
            } else if (type == byte.class) {
                data = new ByteData();
            } else if (type == String.class) {
                data = new StringData();
            } else {
                throw new IllegalArgumentException("Unsupportable column type: " + type
                        + ". Support byte,short,int,long,byte[],short[],int[],long[]!");
            }
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

    }

    private abstract class Block {

        final List<BitSet> columnBitSets = new ArrayList<>();
        final List<IntRange> intRanges = new ArrayList<>();

        abstract int free();

        void setColumnBitSet(Column column, int bit) {
            if (bit >= MIN_COLUMN_BIT) {
                columnBitSets.get(column.index).set(bit);
            } else {
//                 todo implement indexing for negative values, currently just use direct scan
            }
        }

        abstract void add(List<T> row, int start, int end);

        abstract void select(AbstractRequest[] where, ArrayLayoutCallback callback);

        abstract int blockTouch(AbstractRequest[] where);

        abstract void select(AbstractRequest[] where, ArrayLayoutLimitCallback callback);

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

        private boolean inBlock(AbstractRequest[] requests, Block block) {
            for (AbstractRequest request : requests) {
                if (request.column.type == byte.class || request.column.type == short.class) {
                    final BitSet columnBitSet = block.columnBitSets.get(request.column.index);
                    if (!request.inBlock(columnBitSet)) return false;
                } else if (block.getClass() == DataBlock.class && request.column.type == int.class) {
                    if (!request.inBlock(block.intRanges.get(request.column.index))) return false;
                }
            }
            return true;
        }

        @Override
        void select(AbstractRequest[] where, ArrayLayoutCallback callback) {
            for (final Block block : blocks) {
                if (!inBlock(where, block)) continue;
                block.select(where, callback);
            }
        }

        @Override
        int blockTouch(AbstractRequest[] where) {
            int c = 0;
            for (final Block block : blocks) {
                if (!inBlock(where, block)) continue;
                c += block.blockTouch(where);
            }
            return c;
        }

        @Override
        void select(AbstractRequest[] where, ArrayLayoutLimitCallback callback) {
            for (final Block block : blocks) {
                if (!inBlock(where, block)) continue;
                block.select(where, callback);

                if (callback.needToStop()) return;
            }
        }

        private Block createBlock() {
            return new DataBlock();
        }

        @Override
        void add(List<T> data, int start, int end) {
            int position = 0;
            int updatePosition = Math.max(0, blocks.size() - 1);

            if (blocks.isEmpty()) {
                Block block = createBlock();
                blocks.add(block);
            }

            while (position < data.size()) {
                Block block = blocks.get(blocks.size() - 1);
                final int free = block.free();
                if (free == 0) {
                    block = createBlock();
                    blocks.add(block);
                }

                int toAdd = Math.min(free, data.size() - position);
                block.add(data, position, position + toAdd);
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

        final int start;
        private int size;

        private DataBlock() {
            this.start = columns.get(0).data.size();

            for (Column ignored : columns) {
                columnBitSets.add(new BitSet());
                intRanges.add(new IntRange());
            }
        }

        @Override
        public String toString() {
            return "DataBlock {maxSize: " + getMaxSize() + ", start: " + start + ", size: " + size + "}";
        }

        /**
         * @param rows  - block of data to add
         * @param start - start position in rows (inclusive)
         * @param end   - end position in rows (exclusive)
         */
        @Override
        void add(List<T> rows, int start, int end) {
            final int additionalSize = end - start;
            size += additionalSize;

            try {
                for (final Column column : columns) {
                    final MethodHandle methodHandle = column.getter;

                    if (column.type == long.class) {
                        final LongData data = (LongData) column.data;
                        for (int i = start; i < end; i++) {
                            data.add((long) methodHandle.invoke(rows.get(i)));
                        }

                    } else if (column.type == long[].class) {
                        MultiLongData data = (MultiLongData) column.data;
                        for (int i = start; i < end; i++) {
                            long[] v = (long[]) methodHandle.invoke(rows.get(i));
                            data.add(v);
                        }

                    } else if (column.type == short[].class) {
                        final MultiShortData data = (MultiShortData) column.data;
                        for (int i = start; i < end; i++) {
                            short[] v = (short[]) methodHandle.invoke(rows.get(i));
                            data.add(v);
                            // set all bits
                            for (short v1 : v) setColumnBitSet(column, v1);
                        }

                    } else if (column.type == byte[].class) {
                        MultiByteData data = (MultiByteData) column.data;
                        for (int i = start; i < end; i++) {
                            byte[] v = (byte[]) methodHandle.invoke(rows.get(i));
                            data.add(v);
                            // set all bits
                            for (byte v1 : v) setColumnBitSet(column, v1);
                        }

                    } else if (column.type == int.class) {
                        final IntData data = (IntData) column.data;
                        final IntRange intRange = intRanges.get(column.index);

                        data.allocate(additionalSize);

                        for (int i = start; i < end; i++) {
                            int v = (int) methodHandle.invoke(rows.get(i));
                            data.set(i, v);

                            intRange.max = Math.max(intRange.max, v);
                            intRange.min = Math.min(intRange.min, v);
                        }

                    } else if (column.type == short.class) {
                        final ShortData data = (ShortData) column.data;
                        for (int i = start; i < end; i++) {
                            short v = (short) methodHandle.invoke(rows.get(i));
                            data.add(v);
                            setColumnBitSet(column, v);
                        }

                    } else if (column.type == byte.class) {
                        final ByteData data = (ByteData) column.data;
                        data.allocate(additionalSize);

                        for (int i = start; i < end; i++) {
                            byte v = (byte) methodHandle.invoke(rows.get(i));
                            data.set(i, v);
                            setColumnBitSet(column, v);
                        }

                    } else if (column.type == String.class) {
                        final StringData data = (StringData) column.data;
                        for (int i = start; i < end; i++) {
                            String v = (String) methodHandle.invoke(rows.get(i));
                            data.add(v);
                        }

                    } else {
                        throw new IllegalArgumentException("!");
                    }
                }
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        @Override
        void select(AbstractRequest[] where, ArrayLayoutCallback callback) {
//            System.out.println("B");

            final int end = start + size;
            opa:
            for (int i = start; i < end; i++) {
                for (final AbstractRequest request : where) {
                    if (!request.checkValue(i)) continue opa;
                }

                callback.data(i);
            }
        }

        @Override
        int blockTouch(AbstractRequest[] where) {
            return 1;
        }

        @Override
        void select(AbstractRequest[] where, ArrayLayoutLimitCallback callback) {
            final int end = start + size;
            opa:
            for (int i = start; i < end; i++) {
                for (final AbstractRequest request : where) {
                    if (!request.checkValue(i)) continue opa;
                }

                callback.data(i);

                if (callback.needToStop()) return;
            }
        }

        int free() {
            return getMaxSize() - size;
        }

        private int getMaxSize() {
            return blockSizes[blockSizes.length - 1];
        }

    }

}
