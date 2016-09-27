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

import java.util.Map;

/**
 * Abstract class. Parent for all kind of filtering predicates.
 */
public abstract class Request {

    /**
     * Called before filtering in each of block in {@link FastSelect}
     * <p>
     * Some of column supports statistics which could be used to speed up filtering by
     * skipping entire block and avoid scan of that block
     *
     * @param block to check
     * @return true if need to perform scan of records in a block and false to skip block
     */
    public boolean checkBlock(Block block) {
        return true;
    }

    /**
     * Called for each record within block for which {@link Request#checkBlock(Block)} was true
     *
     * @param position absolute position in data set
     * @return true if value in requested position good for requested
     */
    public abstract boolean checkValue(int position);

    /**
     * Called before filtering started. Prepare request to scan through data set.
     *
     * @param columnByNames map passed by engine
     */
    public abstract void prepare(Map<String, FastSelect.Column> columnByNames);

}
