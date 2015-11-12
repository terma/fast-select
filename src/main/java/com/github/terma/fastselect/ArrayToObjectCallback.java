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

package com.github.terma.fastselect;

import org.apache.commons.collections.primitives.ArrayLongList;
import org.apache.commons.collections.primitives.ArrayShortList;

import java.lang.invoke.MethodHandle;
import java.util.List;

public class ArrayToObjectCallback<T> implements ArrayLayoutCallback {

    private final Class<T> dataClass;
    private final List<ArrayLayoutFastSelect.Column> columns;
    private final MethodHandlerRepository mhRepo;
    private final Callback<T> callback;

    public ArrayToObjectCallback(final Class<T> dataClass, final List<ArrayLayoutFastSelect.Column> columns,
                                 final MethodHandlerRepository mhRepo, final Callback<T> callback) {
        this.dataClass = dataClass;
        this.columns = columns;
        this.mhRepo = mhRepo;
        this.callback = callback;
    }

    @Override
    public void data(final int position) {
        try {
            final T o = dataClass.newInstance();

            for (final ArrayLayoutFastSelect.Column column : columns) {
                MethodHandle methodHandle = mhRepo.set(column.name);

                if (column.type == long.class) {
                    methodHandle.invoke(o, ((ArrayLongList) column.data).get(position));
                } else if (column.type == int.class) {
                    methodHandle.invoke(o, ((ArrayLayoutFastSelect.FastIntList) column.data).data[position]);
                } else if (column.type == short.class) {
                    methodHandle.invoke(o, ((ArrayShortList) column.data).get(position));
                } else if (column.type == byte.class) {
                    methodHandle.invoke(o, ((ArrayLayoutFastSelect.FastByteList) column.data).data[position]);
                }
            }

            callback.data(o);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
