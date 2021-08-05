package com.damon.algorithm.leecode;

import java.util.List;

/**
 * 动态规划
 */

public class DP {

    /**
     * 爬楼梯问题
     * 假设你正在爬楼梯。需要 n 阶你才能到达楼顶。
     * <p>
     * 每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
     * <p>
     * 注意：给定 n 是一个正整数。
     * https://leetcode-cn.com/problems/climbing-stairs/
     *
     * @param n
     * @return
     */
    public static int climbStairs_0(int n) {
        int[] dp = new int[n];
        dp[0] = 1;
        dp[1] = 2;
        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n -1];
    }
    public static int climbStairs_1(int n) {
        int p = 1,q =2,temp =0;
        for(int i = 3;i<=n;i++){
            temp = p + q;
            p = q;
            q = temp;
        }
        return q;
    }

    public static void main(String[] args) {
        int n = climbStairs_1(4);
        System.out.println("=println===>");

    }
}
