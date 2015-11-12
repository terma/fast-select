package com.github.terma.fastselect;

public class CountCallback<T> implements Callback<T> {

    private int count;

    @Override
    public void data(T data) {
        count++;
    }

    public int getCount() {
        return count;
    }

}
