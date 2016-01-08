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

package com.github.terma.fastselect.data;

public class StringData implements Data {

    private final MultiByteData data = new MultiByteData();

    public void add(String v) {
        final byte[] bytes = v == null ? new byte[0] : v.getBytes();
        data.add(bytes);
    }

    @Override
    public boolean check(int position, int[] values) {
        throw new UnsupportedOperationException("String field doesn't support filter!");
    }

    @Override
    public boolean plainCheck(int position, byte[] values) {
        throw new UnsupportedOperationException("String field doesn't support filter!");
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

}
