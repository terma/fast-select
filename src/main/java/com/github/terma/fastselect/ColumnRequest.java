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
 * Abstract base class for any requests which works with only one column.
 */
public abstract class ColumnRequest extends Request {

    public final String name;

    /**
     * Field for internal usage by framework you don't need to fill it.
     */
    protected FastSelect.Column column;

    public ColumnRequest(String name) {
        this.name = name;
    }

    /**
     * Prepare request to scan through data set
     *
     * @param columnByNames - map passed by engine
     */
    @Override
    public void prepare(Map<String, FastSelect.Column> columnByNames) {
        column = columnByNames.get(name);
        if (column == null) throw new IllegalArgumentException(
                "Can't find requested column: " + name + " in " + columnByNames);
    }

}
