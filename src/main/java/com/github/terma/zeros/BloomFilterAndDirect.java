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

package com.github.terma.zeros;

import java.util.*;

public final class BloomFilterAndDirect<T> {

    private final String[] indexColumns;
    private final MethodHandlerRepository mhRepo;

    private final List<Block<T>> blocks = new ArrayList<>();

    public BloomFilterAndDirect(Class dataClass, String... indexColumns) {
        this.indexColumns = indexColumns;
        this.mhRepo = new MethodHandlerRepository(dataClass, indexColumns);
    }

    private void addBlock() {
        Block<T> block = new Block<>();
        for (String indexColumn : indexColumns) {
            block.columnBitSets.put(indexColumn, new BitSet());
        }

        blocks.add(block);
    }

    public void add(final T data) {
        if (blocks.isEmpty()) addBlock();
        if (blocks.get(blocks.size() - 1).data.size() == 1000) addBlock();

        Block<T> block = blocks.get(blocks.size() - 1);
        for (String indexColumn : indexColumns) {
            try {
                int indexValue = (Integer) mhRepo.get(indexColumn).invoke(data);
                block.columnBitSets.get(indexColumn).set(indexValue);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }

        block.data.add(data);
    }

    public List<T> select(final MultiRequest[] requests) {
        for (final MultiRequest request : requests) {
            request.mh = mhRepo.get(request.name);
            Arrays.sort(request.values);
        }

        final List<T> result = new ArrayList<>();

        for (final Block<T> block : blocks) {
            if (!inBlock(requests, block)) break;

            // block good for direct search
            try {
                opa:
                for (final T obj : block.data) {
                    for (final MultiRequest request : requests) {
                        final Integer v = (Integer) request.mh.invoke(obj);

                        if (Arrays.binarySearch(request.values, v) < 0) {
                            continue opa;
                        }
                    }

                    result.add(obj);
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }


        return result;
    }

    private boolean inBlock(MultiRequest[] requests, Block<T> block) {
        for (MultiRequest request : requests) {
            final BitSet columnBitSet = block.columnBitSets.get(request.name);

            boolean p = false;
            for (final int value : request.values) {
                p = p || columnBitSet.get(value);
            }
            if (!p) return false;
        }
        return true;
    }

    private static class Block<T> {

        private final Map<String, BitSet> columnBitSets = new HashMap<>();
        private final List<T> data = new ArrayList<>();

    }

}
