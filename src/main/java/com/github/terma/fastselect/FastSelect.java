/*
Copyright 2015 Artem Stasiuk

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

import com.github.terma.fastselect.callbacks.ArrayLayoutCallback;
import com.github.terma.fastselect.callbacks.ArrayToObjectCallback;
import com.github.terma.fastselect.callbacks.Callback;
import com.github.terma.fastselect.callbacks.ListCallback;
import com.github.terma.fastselect.data.*;
import com.github.terma.fastselect.utils.MethodHandlerRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.Field;
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
 *
 * @author Artem Stasiuk
 * @see ArrayLayoutCallback
 * @see com.github.terma.fastselect.callbacks.GroupCountCallback
 * @see com.github.terma.fastselect.callbacks.MultiGroupCountCallback
 */
@ThreadSafe
public final class FastSelect<T> {

    private final static int[] DEFAULT_BLOCK_SIZES = new int[]{1000};

    private final static long MAX_COLUMN_BIT = 10000;
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

    public FastSelect(final Class<T> dataClass, final List<Column> columns) {
        this(DEFAULT_BLOCK_SIZES, dataClass, columns);
    }

    private static List<Column> getColumnsFromDataClass(Class dataClass) {
        final List<Column> columns = new ArrayList<>();
        for (Field field : dataClass.getDeclaredFields()) {
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
        for (final T row : data) {
            rootBlock.add(row);
        }
    }

    public List<T> select(final Request[] where) {
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
        for (final Request condition : where) {
            condition.column = columnsByNames.get(condition.name);

            if (condition.column == null) throw new IllegalArgumentException(
                    "Can't find requested column: " + condition.name + " in " + columns);

            int max = condition.values[0];
            for (int i = 1; i < condition.values.length; i++)
                if (condition.values[i] > max) max = condition.values[i];

            byte[] plainValues = new byte[max + 1];
            for (int value : condition.values) plainValues[value] = 1;
            condition.plainValues = plainValues;

            // todo implement search by array if direct index can't be used Arrays.sort(condition.values);
        }

        rootBlock.select(where, callback);
    }

    public void select(final Request[] where, final Callback<T> callback) {
        select(where, new ArrayToObjectCallback<>(dataClass, columns, mhRepo, callback));
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

    public static class Column {

        public final String name;
        final Class type;
        public final Data data;

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
            } else {
                throw new IllegalArgumentException("Unsupportable column type: " + type
                        + ". Support byte,short,int,long!");
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

        abstract boolean isFull();

        void setColumnBitSet(Column column, int bit) {
            if (bit >= MIN_COLUMN_BIT && bit <= MAX_COLUMN_BIT) {
                columnBitSets.get(column.index).set(bit);
            } else {
                // todo implement indexing for negative values, currently just use direct scan
            }
        }

        abstract void add(T row);

        abstract void select(Request[] where, ArrayLayoutCallback callback);

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
        boolean isFull() {
            return !blocks.isEmpty() && blocks.get(blocks.size() - 1).isFull();
        }

        private boolean inBlock(Request[] requests, Block block) {
            for (Request request : requests) {
                final BitSet columnBitSet = block.columnBitSets.get(request.column.index);

                boolean p = false;
                for (final int value : request.values) {
                    p = p | columnBitSet.get(value);
                }
                if (!p) return false;
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

        private Block createBlock() {
            Block block;

            if (level == blockSizes.length - 1) block = new DataBlock();
            else block = new SuperBlock(level + 1, blockSizes[level + 1]);

            return block;
        }

        @Override
        void add(T row) {
            if (blocks.isEmpty() || blocks.get(blocks.size() - 1).isFull()) blocks.add(createBlock());
            final Block block = blocks.get(blocks.size() - 1);
            block.add(row);

            for (Column column : columns) {
                BitSet bitSet = columnBitSets.get(column.index);
                bitSet.or(block.columnBitSets.get(column.index));
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
            }
        }

        @Override
        public String toString() {
            return "DataBlock {maxSize: " + getMaxSize() + ", start: " + start + ", size: " + size + "}";
        }

        @Override
        void add(T row) {
            size++;

            for (Column column : columns) {
                try {
                    if (column.type == long.class) {
                        long v = (long) mhRepo.get(column.name).invoke(row);
                        ((LongData) column.data).add(v);
                        setColumnBitSet(column, (int) v);

                    } else if (column.type == long[].class) {
                        long[] v = (long[]) mhRepo.get(column.name).invoke(row);
                        ((MultiLongData) column.data).add(v);
                        // set all bits
                        for (long v1 : v) setColumnBitSet(column, (int) v1);

                    } else if (column.type == short[].class) {
                        short[] v = (short[]) mhRepo.get(column.name).invoke(row);
                        ((MultiShortData) column.data).add(v);
                        // set all bits
                        for (short v1 : v) setColumnBitSet(column, v1);

                    } else if (column.type == byte[].class) {
                        byte[] v = (byte[]) mhRepo.get(column.name).invoke(row);
                        ((MultiByteData) column.data).add(v);
                        // set all bits
                        for (byte v1 : v) setColumnBitSet(column, v1);

                    } else if (column.type == int.class) {
                        int v = (int) mhRepo.get(column.name).invoke(row);
                        ((IntData) column.data).add(v);
                        setColumnBitSet(column, v);

                    } else if (column.type == short.class) {
                        short v = (short) mhRepo.get(column.name).invoke(row);
                        ((ShortData) column.data).add(v);
                        setColumnBitSet(column, v);

                    } else if (column.type == byte.class) {
                        byte v = (byte) mhRepo.get(column.name).invoke(row);
                        ((ByteData) column.data).add(v);
                        setColumnBitSet(column, v);

                    } else {
                        throw new IllegalArgumentException("!");
                    }
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
        }

        @Override
        void select(Request[] where, ArrayLayoutCallback callback) {
            final int end = start + size;
            opa:
            for (int i = start; i < end; i++) {
                for (final Request request : where) {
                    if (request.plainValues != null) {
                        if (!request.column.data.plainCheck(i, request.plainValues)) continue opa;
                    } else {
                        if (!request.column.data.check(i, request.values)) continue opa;
                    }
                }

                callback.data(i);
            }
        }

        @Override
        boolean isFull() {
            return size == getMaxSize();
        }

        private int getMaxSize() {
            return blockSizes[blockSizes.length - 1];
        }

    }

}
