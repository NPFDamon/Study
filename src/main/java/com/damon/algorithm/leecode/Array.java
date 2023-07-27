package com.damon.algorithm.leecode;

import java.util.*;

public class Array {


//    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
//        int h = Math.max(nums1.length, nums2.length);
//
//        int[] ans = new int[nums1.length + nums2.length];
//        int j = 0;
//        for (int i = 0; i < h; i++) {
//            if(nums1[i] > nums2[i]){
//                ans[i] =
//            }
//        }
//
//    }


    /**
     * 股票的最大收益
     * 给定一个数组 prices ，其中 prices[i] 是一支给定股票第 i 天的价格。
     * <p>
     * 设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）
     * <p>
     * 作者：力扣 (LeetCode)
     * 链接：https://leetcode-cn.com/leetbook/read/top-interview-questions-easy/x2zsx1/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     *
     * @param prices
     * @return
     */
    public static int maxProfit(int[] prices) {
        int n = prices.length;
        if (n <= 1) {
            return 0;
        }
        int max = 0;
        int d = 0;
        for (int i = 1; i < n; i++) {
            d = prices[i] - prices[i - 1];
            if (d > 0) {
                max += d;
            }
        }
        return max;
    }


    /**
     * 盛水最多的容器
     * 给你 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点 (i, ai) 。在坐标内画 n 条垂直线，垂直线 i 的两个端点分别为 (i, ai) 和 (i, 0) 。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。
     * <p>
     * 说明：你不能倾斜容器。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/container-with-most-water
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     *
     * @param nums
     * @return
     */
    public static int maxArea(int[] nums) {
        int area = 0;
        int i = 0, j = nums.length - 1;
        while (i < j) {
            area = Math.max(area, Math.min(nums[i], nums[j]) * (j - i));
            if (nums[i] < nums[j]) {
                ++i;
            } else {
                --j;
            }
        }
        return area;
    }

    /**
     * 两数之和
     * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
     * <p>
     * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
     * <p>
     * 你可以按任意顺序返回答案。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/two-sum
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     *
     * @param nums
     * @param target
     * @return
     */
    public static int[] towSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        int h = nums.length;
        for (int i = 0; i < h; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{i, map.get(target - nums[i])};
            }
            map.put(nums[i], i);
        }
        return new int[0];
    }

    /**
     * 三数之和
     * 给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有和为 0 且不重复的三元组。
     * <p>
     * 注意：答案中不可以包含重复的三元组。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/3sum
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     *
     * @param nums
     * @return
     */
    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        int h = nums.length;
        Arrays.sort(nums);
        for (int first = 0; first < h; first++) {
            //需要和上次枚举的数不同
            if (first > 0 && nums[first] == nums[first - 1]) {
                continue;
            }
            // 指针指向数组的最右边
            int third = h - 1;

            int target = -nums[first];

            //枚举b
            for (int second = first + 1; second < h; second++) {
                //需要和上次枚举的数不同
                if (second > first + 1 && nums[second] == nums[second - 1]) {
                    continue;
                }

                //需要保证b的指针在c的指针的左侧
                while (second < third && nums[second] + nums[third] > target) {
                    third--;
                }

                //如指针重合，随着b的后续增加，就不会有满足a+b+c=0，并且b<c的c了，可以跳出循环
                if (second == third) {
                    break;
                }
                if (nums[second] + nums[third] == target) {
                    List<Integer> r = new ArrayList<>();
                    r.add(nums[first]);
                    r.add(nums[second]);
                    r.add(nums[third]);
                    res.add(r);
                }
            }
        }
        return res;
    }

    public static void nextPermutation(int[] nums) {
        if(nums.length <= 1){
            return;
        }
        int i = 1;
        for (int j = i+1; j < nums.length; j++) {
            if (nums[j] > nums[i]) {
                int temp = nums[i];
                nums[i] = nums[j];
                nums[j] = temp;
            }
            j++;
        }
        if (nums[0] > nums[1]) {
            Collections.reverse(Arrays.asList(nums));
        }
    }


    /**
     * 合并两个有序数组
     * @param nums1
     * @param nums2
     * @param m
     * @param n
     */
    public static void merge_two_array(int[] nums1,int[] nums2,int m,int n){
        int p1 = 0,p2 = 0;
        int res[] = new int[m+n];
        int cur;
        while (p1 < m || p2 < n){
            if(p1 == m){
                cur = nums2[p2++];
            }else if(p2 == n){
                cur = nums1[p1++];
            }else if(nums1[p1] < nums2[p2]){
                cur = nums1[p1++];
            }else {
                cur = nums2[p2 ++];
            }
            res[p1 + p2 -1] = cur;
        }
        for (int i = 0; i != m + n; ++i) {
            nums1[i] = res[i];
        }

    }

    /**
     *
     * 最长回文字符串
     *
     * @param s
     * @return
     */
    public String longestPalindrome(String s) {
        if(s.length() == 0){
            return s;
        }
        int h = s.length();
        int start = 0, end = 0;
        for(int i = 0; i < h; i++){
            int h1 = getString(i, i, s);
            int h2 = getString(i, i +1, s);
            int len = Math.max(h1, h2);
            if(len > end - start){
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }

        return  s.substring(start, end + 1);
    }

    private int getString(int left, int right, String s){
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)){
            left --;
            right++;
        }
        return right - left -1;
    }


    public String convert(String s, int numRows) {
        if(numRows < 2){
            return s;
        }
        List<StringBuilder> stringBuilders = new ArrayList<>();
        for(int i = 0; i < numRows; i++){
            stringBuilders.add(new StringBuilder());
        }

        int flag = -1 , i = 0;
        for(int index = 0; index < s.length(); index ++){
            StringBuilder sb = stringBuilders.get(i);
            sb.append(s.charAt(index));
            if (i == 0 || i == numRows - 1){
                flag = -flag;
            }
            i +=flag;
        }
        StringBuilder res = new StringBuilder();
        stringBuilders.forEach(res::append);
        return res.toString();
    }

    public int reverse(int x) {
        int res = 0;
        while (x != 0){
            if(res < Integer.MIN_VALUE / 10 || res > Integer.MAX_VALUE / 10){
                return 0;
            }
            int dig = x % 10;
            res = res * 10 + dig;
            x = x / 10;
        }
        return res;
    }

    public int maxSubArray(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];
        for(int i =1; i < n; i++){
            if(nums[i] <= 0){
                dp[i] = nums[i];
            }else {
                dp[i] = dp[i-1] + nums[i];
            }
        }
        int res = dp[0];
        for(int i = 0; i < n; i++){
            res = Math.max(res, dp[i]);
        }
        return  res;
    }

    public String longestCommonPrefix(String[] strs) {
        if(strs == null || strs.length == 0){
            return "";
        }
        String pix = strs[0];
        for(int i = 1; i < strs.length; i++){
            pix = getPix(pix, strs[i]);
            if(pix.length() == 0){
                break;
            }
        }
        return pix;
    }

    private String getPix(String s, String pix){
        int min = Math.min(s.length(), pix.length());
        int i = 0;
        while (i < min && s.charAt(i) == pix.charAt(i)){
            i ++;
        }
        return s.substring(0, i);
    }


    public static void main(String[] args) {
//        int[] nums = {-1, 0, 3, 5, 9, 12};
//        int[] nums = {5};
        int[] nums = {1,2};
//        int[] as = rotate(nums, 3);
//        List<String> res = generateParenthesis(4);
        nextPermutation(nums);
        System.out.println("=println===>");

    }
}
