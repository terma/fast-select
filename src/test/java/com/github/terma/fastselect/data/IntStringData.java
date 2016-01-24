package com.github.terma.fastselect.data;

public class IntStringData {

    public int intValue;
    public String stringValue;

    // empty constructor for database to be able restore object
    public IntStringData() {
        this(0, "");
    }

    public IntStringData(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return "IntStringData {intValue: " + intValue + ", stringValue: '" + stringValue + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntStringData that = (IntStringData) o;
        return intValue == that.intValue && (stringValue != null ? stringValue.equals(that.stringValue) : that.stringValue == null);
    }

    @Override
    public int hashCode() {
        int result = intValue;
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        return result;
    }

}
