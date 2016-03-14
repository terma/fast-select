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

package com.github.terma.fastselect.jmx;

import java.beans.ConstructorProperties;

public class ColumnBean {

    private final String name;
    private final String type;
    private final int size;
    private final int allocatedSize;
    private final long mem;

    @ConstructorProperties({"name", "type", "size", "allocatedSize", "mem"})
    public ColumnBean(String name, String type, int size, int allocatedSize, long mem) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.allocatedSize = allocatedSize;
        this.mem = mem;
    }

    public String getName() {
        return name;
    }

    public long getMemInBytes() {
        return mem;
    }

    public long getMemInMb() {
        return mem / 1024 / 1024;
    }

    public long getMemInGb() {
        return mem / 1024 / 1024 / 1024;
    }

    public String getType() {
        return type;
    }

    public int getAllocatedSize() {
        return allocatedSize;
    }

    public int getSize() {
        return size;
    }

}
