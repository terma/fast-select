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

package com.github.terma.fastselect.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MethodHandlerRepository {

    private final Map<String, MethodHandle> getters;
    private final Map<String, MethodHandle> setters;

    public MethodHandlerRepository(final Class dataClass, final Map<String, Class> fields) {
        final Map<String, MethodHandle> tempGetters = new HashMap<>();
        final Map<String, MethodHandle> tempSetters = new HashMap<>();
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (final Map.Entry<String, Class> field : fields.entrySet()) {
            try {
                final Field fieldReference = dataClass.getDeclaredField(field.getKey());
                if (fieldReference.getType() != field.getValue()) {
                    throw new IllegalArgumentException("Unexpected field type: " + field.getValue() + " for "
                            + dataClass + "." + field.getKey() + "!");
                }

                // force access if field is private
                fieldReference.setAccessible(true);

                final MethodHandle getterHandler = lookup.unreflectGetter(fieldReference);
                final MethodHandle setterHandler = lookup.unreflectSetter(fieldReference);

                tempGetters.put(field.getKey(), getterHandler);
                tempSetters.put(field.getKey(), setterHandler);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalArgumentException("Can't find field: " + field.getKey()
                        + " with type: " + field.getValue() + " in " + dataClass, e);
            }
        }
        this.getters = Collections.unmodifiableMap(tempGetters);
        this.setters = Collections.unmodifiableMap(tempSetters);
    }

    public MethodHandle get(String fieldName) {
        final MethodHandle methodHandle = getters.get(fieldName);
        if (methodHandle == null)
            throw new IllegalArgumentException("Can't find method handler for " + fieldName + "!");
        return methodHandle;
    }


    public MethodHandle set(String fieldName) {
        final MethodHandle methodHandle = setters.get(fieldName);
        if (methodHandle == null)
            throw new IllegalArgumentException("Can't find method handler for " + fieldName + "!");
        return methodHandle;
    }

}
