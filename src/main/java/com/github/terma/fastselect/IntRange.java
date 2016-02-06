package com.github.terma.fastselect;

class IntRange {
    public int min = Integer.MAX_VALUE;
    public int max = Integer.MIN_VALUE;

    @Override
    public String toString() {
        return "IntRange [" + min + ", " + max + ']';
    }
}
