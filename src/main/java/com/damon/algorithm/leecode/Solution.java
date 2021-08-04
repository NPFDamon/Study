package com.damon.algorithm.leecode;

import com.damon.algorithm.ListReverse;

import java.util.*;

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
     * 旋转数组
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


    /**
     * 括号生成
     *
     * @param n
     * @return
     */
    public static List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        generateAll(new char[2 * n], 0, res);
        return res;
    }

    public static void generateAll(char[] current, int pos, List<String> res) {
        if (pos == current.length) {
            //进行筛选
            if (valid(current)) {
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


    /**
     * 括号生成 回溯发
     *
     * @param n
     * @return
     */
    public static List<String> generateParenthesis_1(int n) {
        List<String> res = new ArrayList<>();
        backtrack(res, new StringBuilder(), 0, n, 0);
        return res;
    }

    public static void backtrack(List<String> ans, StringBuilder sb, int open, int max, int close) {
        if (sb.length() == 2 * max) {
            ans.add(sb.toString());
            return;
        }
        if (open < max) {
            sb.append('(');
            backtrack(ans, sb, open + 1, max, close);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (close < open) {
            sb.append(')');
            backtrack(ans, sb, open, max, close + 1);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    List<String> res = new ArrayList<>();

    public List<String> generateParenthesis_2(int n) {
        if (n <= 0) {
            return res;
        }
        getParenthesis("", n, n);
        return res;
    }

    private void getParenthesis(String str, int left, int right) {
        if (left == 0 && right == 0) {
            res.add(str);
            return;
        }
        if (left == right) {
            //剩余左右括号数相等，下一个只能用左括号
            getParenthesis(str + "(", left - 1, right);
        } else if (left < right) {
            //剩余左括号小于右括号，下一个可以用左括号也可以用右括号
            if (left > 0) {
                getParenthesis(str + "(", left - 1, right);
            }
            getParenthesis(str + ")", left, right - 1);
        }
    }


    /**
     * 两数之和
     *
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> res = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (res.containsKey(target - nums[i])) {
                return new int[]{i, res.get(target - nums[i])};
            }
            res.put(nums[i], i);
        }
        return new int[0];
    }

    /**
     * 最长字符串
     *
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring(String s) {
        if (s.length() == 0) return 0;
        HashMap<Character, Integer> map = new HashMap<>();
        int start = 0, max = 0;
        for (int end = 0; end < s.length(); end++) {
            char c = s.charAt(end);
            if (map.containsKey(c)) {
                start = Math.max(map.get(c) + 1, start);
            }
            max = Math.max(max, end - start + 1);
            map.put(c, end);
        }
        return max;
    }

    /**
     * 最长回文字符串
     *
     * @param s
     * @return
     */
    public static String longestPalindrome(String s) {
        if (s == null || s.length() < 1) {
            return "";
        }

        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            int len1 = expandAroundCenter(s, i, i);
            int len2 = expandAroundCenter(s, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > end - start) {
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }
        return s.substring(start, end + 1);
    }


    public static int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            --left;
            ++right;
        }
        return right - left - 1;
    }


    Random random = new Random();

    /**
     * 数组中第k大元素
     *
     * @param nums
     * @param k
     * @return
     */
    public int findKthLargest(int[] nums, int k) {
        return quickSelect(nums, 0, nums.length - 1, nums.length - k);
    }

    public int quickSelect(int[] a, int l, int r, int index) {
        int q = randomPartition(a, l, r);
        if (q == index) {
            return a[q];
        } else {
            return q < index ? quickSelect(a, q + 1, r, index) : quickSelect(a, l, q - 1, index);
        }
    }

    public int randomPartition(int[] a, int l, int r) {
        int i = random.nextInt(r - l + 1) + l;
        swap(a, i, r);
        return partition(a, l, r);
    }

    public int partition(int[] a, int l, int r) {
        int x = a[r], i = l - 1;
        for (int j = l; j < r; ++j) {
            if (a[j] <= x) {
                swap(a, ++i, j);
            }
        }
        swap(a, i + 1, r);
        return i + 1;
    }

    public void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }





    public static void main(String[] args) {
//        int[] nums = {-1, 0, 3, 5, 9, 12};
//        int[] nums = {5};
//        int[] nums = {-4, -1, 2, 3, 10};
//        int[] as = rotate(nums, 3);
//        List<String> res = generateParenthesis(4);
        String res = longestPalindrome("babad");
        System.out.println("=println===>" + res);

    }
}
