package com.github.terma.fastselect;

class Range {

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

    @Override
    public String toString() {
        return "Range [" + min + ", " + max + ']';
    }
}
