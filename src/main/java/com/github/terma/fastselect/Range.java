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

public final class Range {

    public long min;
    public long max;

    /**
     * Create zero negative size range!
     */
    public Range() {
        this(Long.MAX_VALUE, Long.MIN_VALUE);
    }

    public Range(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public void update(long value) {
        max = Math.max(max, value);
        min = Math.min(min, value);
    }

    public void update(int value) {
        max = Math.max(max, value);
        min = Math.min(min, value);
    }

    @Override
    public String toString() {
        return "Range [" + min + ", " + max + ']';
    }
}
