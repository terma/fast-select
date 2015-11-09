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

import java.util.*;

/**
 * Based two step search algorithm (bloom filter + direct scan within block)
 */
public final class ObjectFastSelect<T> implements FastSelect<T> {

    private final int blockSize;
    private final Class dataClass;
    private final String[] indexColumns;
    private final MethodHandlerRepository mhRepo;
    private final List<Block<T>> blocks;

    public ObjectFastSelect(final int blockSize, final Class dataClass, final String... indexColumns) {
        this.blockSize = blockSize;
        this.dataClass = dataClass;
        this.indexColumns = indexColumns;
        this.mhRepo = new MethodHandlerRepository(dataClass, indexColumns);
        this.blocks = new ArrayList<>();
    }

    public void addAll(final List<T> data) {
        for (final T row : data) {
            if (blocks.isEmpty() || blocks.get(blocks.size() - 1).data.size() == blockSize)
                blocks.add(createBlock());

            Block<T> block = blocks.get(blocks.size() - 1);
            for (String indexColumn : indexColumns) {
                try {
                    int indexValue = (Integer) mhRepo.get(indexColumn).invoke(row);
                    block.columnBitSets.get(indexColumn).set(indexValue);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }

            block.data.add(row);
        }
    }

    private Block<T> createBlock() {
        final Block<T> block = new Block<>();
        for (String indexColumn : indexColumns) {
            block.columnBitSets.put(indexColumn, new BitSet());
        }
        return block;
    }

    @Override
    public List<T> select(final MultiRequest[] where) {
        ListCallback<T> result = new ListCallback<>();
        select(where, result);
        return result.getResult();
    }

    @Override
    public void select(final MultiRequest[] where, final Callback<T> callback) {
        for (final MultiRequest condition : where) {
            condition.mh = mhRepo.get(condition.name);
            Arrays.sort(condition.values);
        }

        for (final Block<T> block : blocks) {
            if (!inBlock(where, block)) break;

            // block good for direct search
            try {
                opa:
                for (final T obj : block.data) {
                    for (final MultiRequest request : where) {
                        final int v = (int) request.mh.invoke(obj);

                        if (Arrays.binarySearch(request.values, v) < 0) {
                            continue opa;
                        }
                    }

                    callback.data(obj);
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean inBlock(MultiRequest[] requests, Block<T> block) {
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
        int r = 0;
        for (Block<T> block : blocks) r += block.data.size();
        return r;
    }

    public List<T> getItems() {
        final List<T> result = new ArrayList<>();
        for (final Block<T> block : blocks) {
            result.addAll(block.data);
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " {blockSize: " + blockSize + ", data: " + size()
                + ", indexes: " + indexColumns.length + ", class: " + dataClass + "}";
    }

    private static class Block<T> {

        public final Map<String, BitSet> columnBitSets = new HashMap<>();
        public final List<T> data = new ArrayList<>();

    }

}
