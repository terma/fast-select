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

package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.utils.MethodHandlerRepository;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandle;
import java.util.List;

@NotThreadSafe
public class ArrayToObjectCallback<T> implements ArrayLayoutCallback {

    private final Class<T> dataClass;
    private final List<FastSelect.Column> columns;
    private final MethodHandlerRepository mhRepo;
    private final Callback<T> callback;

    public ArrayToObjectCallback(final Class<T> dataClass, final List<FastSelect.Column> columns,
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

            for (final FastSelect.Column column : columns) {
                MethodHandle methodHandle = mhRepo.set(column.name);
                methodHandle.invoke(o, column.data.get(position));
            }

            callback.data(o);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
