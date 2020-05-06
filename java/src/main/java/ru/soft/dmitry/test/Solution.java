package ru.soft.dmitry.test;

import java.util.Scanner;

public class Solution {

    static int jumpingOnClouds(final int[] c) {
        int result = 0;
        int i = 0;
        // we are looking to jump for two clouds forward
        // so if we are in two or less clouds close to the end - it's over
        // because the last cloud is guaranteed to be safe
        // but corrections must be made to accumulate jumps correctly
        // in case we are standing on a cloud preceding to the last cloud
        final int end = c.length - 2;
        while (i < end) {
            // look to jump over
            if (c[i+2] == 0) i++;
            // move forward anyway
            i++;
            // count jump
            result++;
        }
        // make corrections in case of we were standing at preceding to the last cloud
        return result + (end + 1 - i);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] c = new int[n];
        for(int c_i = 0; c_i < n; c_i++){
            c[c_i] = in.nextInt();
        }
        int result = jumpingOnClouds(c);
        System.out.println(result);
        in.close();
    }

    public static void test(final int[] c) {
        int result = jumpingOnClouds(c);
        System.out.println(result);
    }
}
