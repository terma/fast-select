package com.github.terma.fastselect;

public class CountArrayLayoutCallback implements ArrayLayoutCallback {

    private int count;

    @Override
    public void data(int position) {
        count++;
    }

    public int getCount() {
        return count;
    }
}
