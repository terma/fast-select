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

package com.github.terma.fastselect.utils;

public class BlockRoundValue {

    private final int blockCardinality;
    private final int blockDuration;
    private final int max;

    private int blockLive;
    private int valueInBlock;
    private int shift;

    public BlockRoundValue(int blockCardinality, int blockDuration, int cardinality) {
        if (cardinality < 1) throw new IllegalArgumentException("Cardinality should be more 0!");

        if (blockCardinality > cardinality) throw new IllegalArgumentException(
                "Block cardinality = " + blockCardinality + " should be less or eq to cardinality = " + cardinality + "!");

        this.blockCardinality = blockCardinality;
        this.blockDuration = blockDuration;
        this.max = cardinality;
    }

    public int next() {
        int value = shift + valueInBlock;
        valueInBlock++;
        if (valueInBlock >= blockCardinality) valueInBlock = 0;

        blockLive++;
        if (blockLive == blockDuration) {
            blockLive = 0;
            valueInBlock = 0;
            shift += blockCardinality;
        }
        if (shift + blockCardinality > max) shift = 0;

        return value;
    }

}
