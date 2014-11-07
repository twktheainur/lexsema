package org.getalp.optimization.org.getalp.util;

public class PermutationItem implements Comparable<PermutationItem> {
    public int origIndex;
    public double value;

    PermutationItem(int origIndex, double value) {
        this.origIndex = origIndex;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermutationItem)) return false;

        PermutationItem that = (PermutationItem) o;

        if (origIndex != that.origIndex) return false;
        if (Double.compare(that.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = origIndex;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public int compareTo(PermutationItem o) {
        int cmp = Double.valueOf(value).compareTo(Double.valueOf(o.value));
        return -cmp;
    }
}