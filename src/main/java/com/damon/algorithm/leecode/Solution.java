package com.damon.algorithm.leecode;

import com.damon.algorithm.ListReverse;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: cashmama
 * @Date: 2021/7/23-16:50
 * @Description:
 * @Slogan: To be or not to be
 **/
public class Solution {


    /**
     * 二分查找
     *
     * @param nums   数组
     * @param target 目标值
     * @return 目标值下标
     */
    public static int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }
        return -1;
    }

    /**
     * 搜索插入位置
     * <p>
     * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
     *
     * @param nums
     * @param target
     * @return
     */
    public static int searchTarget(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }
        return right + 1;
    }

    /**
     * 有序数组的平方
     *
     * @param nums
     * @return
     */
    public static int[] sortedSquares(int[] nums) {
        int n = nums.length;

        int[] ans = new int[n];

        for (int i = 0, j = n - 1, pos = n - 1; i <= j; pos--) {
            if (nums[i] * nums[i] < nums[j] * nums[j]) {
                ans[pos] = nums[j] * nums[j];
                j--;
            } else {
                ans[pos] = nums[i] * nums[i];
                i++;
            }

        }
        return ans;
    }


    /**
     * 选择数组
     *
     * @param nums
     * @param k
     * @return
     */
    public static int[] rotate(int[] nums, int k) {
        int n = nums.length;
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            ans[(i + k) % n] = nums[i];
        }
//        System.arraycopy(ans, 0, nums, 0, n);
        return ans;
    }


    public static void rotateSelf(int[] nums, int k) {
        int n = nums.length;
        for (int i = 1; i <= k; i++) {
            int tmp = nums[1];
            for (int j = 0; j < n; j++) {
                nums[j + 1] = nums[j];
            }
        }
    }


    public static void moveZeroes(int[] nums) {
        int n = nums.length;
        //两个指针i和j
        int j = 0;
        for (int i = 0; i < n; i++) {
            //当前元素!=0，就把其交换到左边，等于0的交换到右边
            if (nums[i] != 0) {
                int tmp = nums[i];
                nums[i] = nums[j];
                nums[j++] = tmp;
            }
        }

    }


    public static List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        generateAll(new char[2 * n],0,res);
        return res;
    }

    public static void generateAll(char[] current, int pos, List<String> res) {
        if (pos == current.length) {
            //进行筛选
            if(valid(current)){
                res.add(new String(current));
            }
        } else {
            //穷举出所有可能
            current[pos] = '(';
            generateAll(current, pos + 1, res);
            current[pos] = ')';
            generateAll(current, pos + 1, res);
        }
    }

    public static boolean valid(char[] current) {
        int balance = 0;
        for (char c : current) {
            if (c == '(') {
                ++balance;
            } else {
                --balance;
            }
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }


    public static void main(String[] args) {
//        int[] nums = {-1, 0, 3, 5, 9, 12};
//        int[] nums = {5};
//        int[] nums = {-4, -1, 2, 3, 10};
//        int[] as = rotate(nums, 3);
        List<String> res = generateParenthesis(4);
        System.out.println("=println===>");

    }
}
