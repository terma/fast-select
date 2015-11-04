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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class MethodHandlerRepository {

    private final Map<String, MethodHandle> data;

    public MethodHandlerRepository(Class dataClass, String... fields) {
        final Map<String, MethodHandle> tempData = new HashMap<>();
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (final String indexColumn : fields) {
            try {
                final MethodHandle methodHandle = lookup.findGetter(dataClass, indexColumn, int.class);
                tempData.put(indexColumn, methodHandle);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
        this.data = Collections.unmodifiableMap(tempData);
    }

    public MethodHandle get(String indexColumn) {
        final MethodHandle methodHandle = data.get(indexColumn);
        if (methodHandle == null) throw new IllegalArgumentException("Can't find method handler for " + indexColumn +
                ". You need to add it to index!");
        return methodHandle;
    }

}
