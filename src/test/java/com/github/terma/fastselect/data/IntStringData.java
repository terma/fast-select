package com.github.terma.fastselect.data;

public class IntStringData {

    public int value1;
    public String value2;

    // empty constructor for database to be able restore object
    public IntStringData() {
        this(0, "");
    }

    public IntStringData(int value1, String value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "IntStringData {value1: " + value1 + ", value2: '" + value2 + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntStringData that = (IntStringData) o;
        return value1 == that.value1 && (value2 != null ? value2.equals(that.value2) : that.value2 == null);
    }

    @Override
    public int hashCode() {
        int result = value1;
        result = 31 * result + (value2 != null ? value2.hashCode() : 0);
        return result;
    }

}
