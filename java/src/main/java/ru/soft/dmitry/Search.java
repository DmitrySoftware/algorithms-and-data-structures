package ru.soft.dmitry;

/**
 * Created by main on 28.06.17.
 */
public class Search {

    /**
     * Search by division by two.
     * @param key search key
     * @param arr array to search in
     * @return array's index which value equals key
     */
    public static int binary(final int key, final int[] arr) {
        final int notFound = -1;
        if (arr.length == 0) return notFound;
        if (arr.length == 1) return (arr[0] == key) ? 0 : notFound;
        int l = 0;
        int r = arr.length;
        int m = notFound;
        while (l < r) {
            m = (l + r) >>> 1;
            if (arr[m] > key) {
                r = m;
            } else {
                l = m + 1;
            }
        }
        if (arr[m] == key) return m;
        if (arr[m-1] == key) return m-1;
        return notFound;
    }

    public static int binary2(final int key, final int[] arr) {
        int l = 0;
        int r = arr.length - 1;
        while (l <= r) {
            final int m = (l + r) >>> 1;
            final int value = arr[m];
            if (value > key) {
                r = m - 1;
            } else if (value < key ) {
                l = m + 1;
            } else {
                return m;
            }
        }
        return -1;
    }

    public static int anyBetween(final int l, final int r) {
        return (int)(((long)l + (long)r) / 2);
    }

    public static int anyBetween2(final int l, final int r) {
        return ((r - l) / 2) + l;
    }

    public static int anyBetween3(final int l, final int r) {
        return (l + r) >>> 1;
    }


}
