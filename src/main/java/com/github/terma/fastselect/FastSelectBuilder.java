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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class FastSelectBuilder<T> {

    public static final int DEFAULT_BLOCK_SIZE = 1000;
    public static final int DEFAULT_INC = 300000;

    private final Class<T> dataClass;
    private int blockSize = DEFAULT_BLOCK_SIZE;
    private int inc = DEFAULT_INC;
    private List<FastSelect.Column> columns;

    public FastSelectBuilder(Class<T> dataClass) {
        this.dataClass = dataClass;
    }

    private static List<FastSelect.Column> getColumnsFromDataClass(Class dataClass, int inc) {
        final List<FastSelect.Column> columns = new ArrayList<>();
        for (Field field : dataClass.getDeclaredFields()) {
            if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers()))
                columns.add(new FastSelect.Column(field.getName(), field.getType(), inc));
        }
        return columns;
    }

    public FastSelectBuilder<T> blockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * @param inc - when you add more data to fastSelect we need to increase size of columns to be able
     *            add all data. Inc is value which show how many items will be added during one extension.
     *            WARNING!!! If that value will be less than you try to add exception will be raised as
     *            no enough space will be allocated.
     * @return - same builder
     */
    public FastSelectBuilder<T> inc(final int inc) {
        this.inc = inc;
        return this;
    }

    public FastSelectBuilder<T> columns(List<FastSelect.Column> columns) {
        this.columns = columns;
        return this;
    }

    public FastSelect<T> create() {
        if (columns == null) columns = getColumnsFromDataClass(dataClass, inc);
        return new FastSelect<>(blockSize, dataClass, columns);
    }

}
