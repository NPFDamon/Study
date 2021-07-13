package com.damon.algorithm;

import java.util.Random;

/**
 * @Author: cashmama
 * @Date: 2021/7/13-09:40
 * @Description: 排序
 * @Slogan: To be or not to be
 **/
public class Sort {

    public static int[] quickSort(int[] array) {
        randomizedQuicksort(array, 0, array.length - 1);
        return array;
    }

    /**
     * 1,3,5,4,6,8,0,9,2,7
     *
     * @param num   数组 1,3,5,4,6,8,0,9,2,7
     * @param left  left位置 0
     * @param right right位置 9
     */
    public static void randomizedQuicksort(int[] num, int left, int right) {
        if (left < right) {
            int pos = randomizedPartition(num, left, right);
            randomizedPartition(num, left, pos - 1);
            randomizedPartition(num, pos + 1, right);
        }
    }

    /**
     * 1,3,5,4,6,8,0,9,2,7
     *
     * @param num   数组
     * @param left  left位置
     * @param right right位置
     * @return position
     */
    public static int randomizedPartition(int[] num, int left, int right) {
        int i = new Random().nextInt(right - left + 1) + 1;
        swap(num, right, i);
        return partition(num, left, right);
    }

    /**
     * 1,3,5,4,7,8,0,9,2,6
     *
     * @param num   数组
     * @param left  left位置
     * @param right right位置
     * @return position
     */
    public static int partition(int[] num, int left, int right) {
        int pivotIndex = num[right];
        int i = left - 1;
        for (int j = left; j < right - 1; j++) {
            if (num[j] <= pivotIndex) {
                i = i + 1;
                swap(num, i, j);
            }
        }
        swap(num, i + 1, right);
        return i + 1;
    }


    /**
     * 数组第i个位置和第j个位置交换
     *
     * @param num 数组
     * @param i   位置i
     * @param j   位置j
     */
    private static void swap(int[] num, int i, int j) {
        int temp = num[i];
        num[i] = num[j];
        num[j] = temp;
    }

    public static void main(String[] args) {
        int[] tre = {1, 3, 5, 4, 6, 8, 0, 9, 2, 7};
        quickSort(tre);
    }
}
