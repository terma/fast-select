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

import com.github.terma.fastselect.callbacks.ArrayLayoutCallback;
import com.github.terma.fastselect.callbacks.ArrayLayoutLimitCallback;

import java.util.List;

public class BlockMock extends Block {

    public BlockMock() {
    }

    public BlockMock(Range range) {
        ranges.add(range);
    }

    @Override
    int free() {
        return 0;
    }

    @Override
    void add(List dataToAdd, int addFrom, int addTo) {

    }

    @Override
    void select(Request[] where, ArrayLayoutCallback callback) {

    }

    @Override
    int blockTouch(Request[] where) {
        return 0;
    }

    @Override
    void select(Request[] where, ArrayLayoutLimitCallback callback) {

    }

    @Override
    public void init() {

    }
}
