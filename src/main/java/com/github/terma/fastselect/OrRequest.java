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

package com.github.terma.fastselect;

import java.util.Arrays;
import java.util.Map;

/**
 * SQL analog is <code>where CONDITION X or CONDITION Y [or CONDITION Z ...]</code>
 */
@SuppressWarnings("WeakerAccess")
public class OrRequest extends Request {

    private final Request[] requests;

    public OrRequest(final Request... requests) {
        this.requests = requests;
    }

    @Override
    public boolean checkBlock(Block block) {
        for (final Request request : requests)
            if (request.checkBlock(block)) return true;
        return false;
    }

    @Override
    public boolean checkValue(final int position) {
        for (final Request request : requests)
            if (request.checkValue(position)) return true;
        return false;
    }

    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        for (final Request request : requests) request.prepare(columnByNames);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + Arrays.toString(requests);
    }

}
