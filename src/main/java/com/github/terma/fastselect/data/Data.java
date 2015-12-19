package com.github.terma.fastselect.data;

public interface Data {

    boolean check(int position, int[] values);

    Object get(int position);

    int size();

}
