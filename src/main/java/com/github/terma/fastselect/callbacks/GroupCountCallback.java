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

package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.ArrayLayoutFastSelect;

import java.util.HashMap;
import java.util.Map;

public class GroupCountCallback implements ArrayLayoutCallback {

    private final Map<Integer, Integer> counter = new HashMap<>();
    private final ArrayLayoutFastSelect.Column groupBy;

    public GroupCountCallback(final ArrayLayoutFastSelect.Column groupBy) {
        this.groupBy = groupBy;
    }

    @Override
    public void data(final int position) {
        final int value = groupBy.valueAsInt(position);
        final Integer c = counter.get(value);
        if (c == null) counter.put(value, 1);
        else counter.put(value, c + 1);
    }

    public Map<Integer, Integer> getCounter() {
        return counter;
    }

}
