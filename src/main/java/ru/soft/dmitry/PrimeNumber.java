package ru.soft.dmitry;

public class PrimeNumber {

    private static final int SIZE = 32;

    public static boolean isPrime(final long number) {
        if (number <= 0) throw new IllegalArgumentException(String.format("Must be positive number: [%d]!", number));
        if (number == 1) return false;
        final int sqrt = (int) Math.sqrt(number);
        final int remainder = sqrt % SIZE;
        final int addSegment = (sqrt<=SIZE || remainder!=0) ? 1 : 0;
        final int[] masks = new int[sqrt / SIZE + addSegment];
        masks[0] = 0b11111111_11111111_11111111_11111100;
        for (int i = 1; i < masks.length; i++) {
            masks[i] = 0b11111111_11111111_11111111_11111111;
        }
        long n = 1;
        long i;
        while ( (i = ++n << 1) <= sqrt) {
            final int mask = 1 << (n % SIZE);
            final int page = (int) (n / SIZE);
            if (mask == (masks[page] & mask)) {
                for (long j = i; j <= sqrt; j += n) {
                    final int seg = (int) (j / SIZE);
                    final int flag = 1 << (j % SIZE);
                    masks[seg] = masks[seg] & ~flag;
                }
            }
        }
        final int lastSeg = masks.length - 1;
        for (int seg = 0; seg < lastSeg; seg++) {
            if (hasDivisor(number, masks, seg, SIZE, SIZE)) return false;
        }
        return !hasDivisor(number, masks, lastSeg, remainder+1, SIZE);
    }

    private static boolean hasDivisor(final long input, final int[] mask, final int seg, final int offsetLimit, final int size) {
        for (int offset = 0; offset < offsetLimit; offset++) {
            final int flag = 1 << offset;
            if (flag == (mask[seg] & flag)) {
                final int div = (seg * size) + offset;
                if (input % div == 0) return true;
            }
        }
        return false;
    }

}
