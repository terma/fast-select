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
 * SQL analog is <code>where not CONDITION X</code>
 * <p>
 * For example <code>where AGE not 15</code>
 * <p>
 * You can use it with any type of request
 * <p>
 * WARNING! <code>NotRequest</code> could be slow. It doesn't support column statistic.
 * Because of that filtering will always use full scan by all cache elements regardless of underling request.
 * <p>
 * Simple explanation for that is <code>NotRequest</code> doesn't have information how to make opposite
 * result from underling request. By definition {@link Request#checkBlock(Block)} returns <code>true</code>
 * only when data present somewhere at the block. So it could be case when two records in block and only
 * one of them good for filter but <code>checkBlock</code> should return <code>true</code>
 */
@SuppressWarnings("WeakerAccess")
public class NotRequest extends Request {

    private final Request request;

    public NotRequest(final Request request) {
        this.request = request;
    }

    /**
     * @param block {@link Request#checkBlock(Block)}
     * @return always true Why? Check class javadoc
     */
    @Override
    public boolean checkBlock(Block block) {
        return true;
    }

    @Override
    public boolean checkValue(final int position) {
        return !request.checkValue(position);
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        request.prepare(columnByNames);
    }

    @Override
    public String toString() {
        return "not " + request;
    }

}
