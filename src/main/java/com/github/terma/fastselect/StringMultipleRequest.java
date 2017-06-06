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

import com.github.terma.fastselect.data.StringData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SQL analog <code>where STRING_FIELD in ('', ''...)</code>
 *
 * @see StringNoCaseLikeRequest
 * @see StringRequest
 */
@SuppressWarnings("WeakerAccess")
public class StringMultipleRequest extends ColumnRequest {

    private final Set<String> values;

    public StringMultipleRequest(String name, String... values) {
        this(name, Arrays.asList(values));
    }

    public StringMultipleRequest(String name, List<String> values) {
        super(name);
        this.values = new HashSet<>(values);
    }

    /**
     * @param name   - column name
     * @param values - set of values will be used as is without copy so be careful
     */
    public StringMultipleRequest(String name, Set<String> values) {
        super(name);
        this.values = values;
    }

    @Override
    public boolean checkValue(int position) {
        StringData data = (StringData) column.data;
        String value = (String) data.get(position);
        return values.contains(value);
    }

    @Override
    public String toString() {
        return "StringMultipleRequest {name: '" + name + "', in: " + values + "}";
    }

}
