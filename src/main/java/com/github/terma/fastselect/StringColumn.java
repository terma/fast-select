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

import com.github.terma.fastselect.data.MultiByteData;

public class StringColumn extends XColumn {

    private final MultiByteData data = new MultiByteData();
    private final String name;

    public StringColumn(String name) {
        this.name = name;
    }

    public void add(String v) {
        final byte[] bytes = v == null ? new byte[0] : v.getBytes();
        data.add(bytes);
    }

    @Override
    public Object get(int position) {
        final byte[] bytes = getRaw(position);
        return new String(bytes);
    }

    public byte[] getRaw(int position) {
        return (byte[]) data.get(position);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Class type() {
        return String.class;
    }

    @Override
    public String name() {
        return name;
    }

}
