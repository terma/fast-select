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
import org.apache.commons.collections.primitives.ArrayLongList;
import org.apache.commons.collections.primitives.ArrayShortList;

import java.util.*;

/**
 * Fast Search based two step search algorithm (bloom filter + direct scan within block)
 */
public final class ArrayLayoutFastSelect<T> {

    private final int blockSize;
    private final Class<T> dataClass;
    private final MethodHandlerRepository mhRepo;
    private final List<Block> blocks;
    private final List<Column> columns;
    private final Map<String, Column> columnsByNames;

    public ArrayLayoutFastSelect(final int blockSize, final Class<T> dataClass, final List<Column> columns) {
        this.blockSize = blockSize;
        this.dataClass = dataClass;
        this.columns = columns;
        this.columnsByNames = initColumnsByName(columns);
        this.mhRepo = new MethodHandlerRepository(dataClass, getColumnsAsMap(columns));
        this.blocks = new ArrayList<>();
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
            if (blocks.isEmpty() || blocks.get(blocks.size() - 1).size >= blockSize)
                blocks.add(createBlock(blocks));

            Block block = blocks.get(blocks.size() - 1);
            block.size++;
            for (Column column : columns) {
                try {
                    int indexValue;

                    if (column.type == long.class) {
                        long v = (long) mhRepo.get(column.name).invoke(row);
                        ((ArrayLongList) column.data).add(v);
                        indexValue = (int) v;
                    } else if (column.type == int.class) {
                        int v = (int) mhRepo.get(column.name).invoke(row);
                        ((FastIntList) column.data).add(v);
                        indexValue = v;
                    } else if (column.type == short.class) {
                        short v = (short) mhRepo.get(column.name).invoke(row);
                        ((ArrayShortList) column.data).add(v);
                        indexValue = v;
                    } else if (column.type == byte.class) {
                        byte v = (byte) mhRepo.get(column.name).invoke(row);
                        ((FastByteList) column.data).add(v);
                        indexValue = v;
                    } else {
                        throw new IllegalArgumentException("!");
                    }

                    block.columnBitSets.get(column.name).set(indexValue);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
        }
    }

    private Block createBlock(List<Block> blocks) {
        final Block block = new Block();
        block.start = blocks.isEmpty() ? 0 : blocks.get(blocks.size() - 1).start + blocks.get(blocks.size() - 1).size;
        block.size = 0;
        for (Column column : columns) {
            block.columnBitSets.put(column.name, new BitSet());
        }
        return block;
    }

    public List<T> select(final MultiRequest[] where) {
        ListCallback<T> result = new ListCallback<>();
        select(where, result);
        return result.getResult();
    }

    public void select(final MultiRequest[] where, final ArrayLayoutCallback callback) {
        for (final MultiRequest condition : where) {
            condition.column = columnsByNames.get(condition.name);
            Arrays.sort(condition.values);
        }
        try {
            for (final Block block : blocks) {
                if (!inBlock(where, block)) continue;

                // block good for direct search
                final int end = block.start + block.size;
                opa:
                for (int i = block.start; i < end; i++) {
                    for (final MultiRequest request : where) {
                        final int value = request.column.valueAsInt(i);

                        if (Arrays.binarySearch(request.values, value) < 0) {
                            continue opa;
                        }
                    }

                    callback.data(i);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void select(final MultiRequest[] where, final Callback<T> callback) {
        select(where, new ArrayToObjectCallback<>(dataClass, columns, mhRepo, callback));
    }

    private boolean inBlock(MultiRequest[] requests, Block block) {
        for (MultiRequest request : requests) {
            final BitSet columnBitSet = block.columnBitSets.get(request.name);

            boolean p = false;
            for (final int value : request.values) {
                p = p | columnBitSet.get(value);
            }
            if (!p) return false;
        }
        return true;
    }

    public int size() {
        for (Column column : columns) {
            if (column.type == long.class) {
                return ((ArrayLongList) column.data).size();
            } else if (column.type == int.class) {
                return ((FastIntList) column.data).size;
            } else if (column.type == short.class) {
                return ((ArrayShortList) column.data).size();
            } else if (column.type == byte.class) {
                return ((FastByteList) column.data).size;
            }
        }
        return 0;
    }

    public Map<String, Column> getColumnsByNames() {
        return columnsByNames;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {blockSize: " + blockSize + ", data: " + size()
                + ", indexes: " + columns.size() + ", class: " + dataClass + "}";
    }

    public static class Column {

        public final String name;
        public final Class type;
        public final Object data;

        public Column(final String name, final Class type) {
            this.name = name;
            this.type = type;

            if (type == long.class) {
                data = new ArrayLongList();
            } else if (type == int.class) {
                data = new FastIntList();
            } else if (type == short.class) {
                data = new ArrayShortList();
            } else if (type == byte.class) {
                data = new FastByteList();
            } else {
                throw new IllegalArgumentException("Unsupportable column type: " + type);
            }
        }

        public int valueAsInt(final int position) {
            if (type == byte.class) {
                return ((FastByteList) data).data[position];
            } else if (type == short.class) {
                return ((ArrayShortList) data).get(position);
            } else if (type == long.class) {
                return (int) ((ArrayLongList) data).get(position);
            } else if (type == int.class) {
                return ((FastIntList) data).data[position];
            } else {
                throw new IllegalArgumentException("Unknown column type: " + type);
            }
        }

    }

    private static class Block {
        public final Map<String, BitSet> columnBitSets = new HashMap<>();
        public int start;
        public int size;
    }

    public static class FastIntList {

        public int[] data = new int[16];
        public int size = 0;

        public void add(int v) {
            if (size == data.length) {
                data = Arrays.copyOf(data, size + 1000);
            }
            data[size] = v;
            size++;
        }

    }

    public static class FastByteList {

        public byte[] data = new byte[16];
        public int size = 0;

        public void add(byte v) {
            if (size == data.length) {
                data = Arrays.copyOf(data, size + 1000);
            }
            data[size] = v;
            size++;
        }

    }

}
