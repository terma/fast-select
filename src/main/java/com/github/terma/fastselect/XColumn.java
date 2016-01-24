package com.github.terma.fastselect;

abstract public class XColumn {

    public abstract int size();

    public abstract Class type();

    public abstract String name();

    public abstract Object get(int position);

}
