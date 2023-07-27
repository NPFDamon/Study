package com.damon.algorithm.leecode;

import java.util.ArrayList;
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

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        if (obstacleGrid == null || obstacleGrid.length == 0) {
            return 0;
        }
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;
        int[][] dp = new int[m][n];
        for(int i = 0; i< m ; i++){
            if(obstacleGrid[i][0] == 0){
                dp[i][0] = 1;
            }
        }

        for(int i = 0; i< n ; i++){
            if(obstacleGrid[0][i] == 0){
                dp[0][i] = 1;
            }
        }

        for(int i = 1; i < m; i++){
            for(int j = 1; j< n; j++){
                if(obstacleGrid[i][j] == 0){
                    dp[i][j] = dp[i][j -1] + dp[i - 1][j];
                }
            }
        }
        return dp[m -1][n -1];
    }


    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for(int i=0; i< m; i++){
            dp[i][0] = 1;
        }

        for(int i = 0; i< n ; i++){
            dp[0][i] = 1;
        }

        for(int i = 1; i < m ; i++){
            for(int j = 1; j< n; j++){
                dp[i][j] = dp[i - 1][j] + dp[i][j -1];
            }
        }
        return dp[m-1][n-1];
    }

    public static int minPathSum(int[][] grid) {
        int m =grid.length;
        int n = grid[0].length;
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for(int i=1; i< m; i++){
            dp[i][0] = grid[i][0] + dp[i - 1][0];
        }

        for(int i = 1; i< n ; i++){
            dp[0][i] = grid[0][i] + dp[0][i-1];
        }

        for(int i = 1; i < m ; i++){
            for(int j = 1; j< n; j++){
                dp[i][j] = Math.min(dp[i - 1][j],dp[i][j -1]) + grid[i][j];
            }
        }
        return dp[m -1][n-1];
    }

    public static int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[][] dp = new int[n][n];
        dp[0][0] = triangle.get(0).get(0);
        for(int i = 1; i< n; i++){
            dp[i][0] = dp[i-1][0] + triangle.get(i).get(0);
            for(int j = 1; j < i; j++){
                dp[i][j] = Math.min(dp[i-1][j-1], dp[i-1][j]) + triangle.get(i).get(j);
            }
            dp[i][i] = dp[i-1][i-1] + triangle.get(i).get(i);
        }
        int ans = dp[n-1][0];
        for(int i = 1; i < n; i++){
            ans = Math.min(ans, dp[n-1][i]);
        }
        return ans;
    }

    public static int minimumTotal1(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[] dp = new int[n];
        dp[0] = triangle.get(0).get(0);
        for(int i = 1; i< n; i ++){
            dp[i] = dp[i-1] + triangle.get(i).get(i);
            for(int j = i-1; j > 0; j--){
                dp[j] = Math.min(dp[j-1], dp[j]) + triangle.get(i).get(j);
            }
            dp[0] = dp[0] + triangle.get(i).get(0);
        }
        int ans = dp[0];
        for(int i = 1; i< n; i++){
            ans = Math.min(ans, dp[i]);
        }
        return ans;
    }

    public int minFallingPathSum(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;

        int[][] dp = new int[m][n];

        for (int i = 0; i < n; i++){
            dp[0][i] = matrix[0][i];
        }

        for(int i = 1; i< m; i++){
            for(int j = 0; j< n; j++){
                if(j == 0){
                    dp[i][j] = Math.min(dp[i-1][j], dp[i -1][j +1]) + matrix[i][j];
                }else if(j == n -1){
                    dp[i][j] = Math.min(dp[i-1][j-1], dp[i-1][j]) + matrix[i][j];
                }else {
                    dp[i][j] = Math.min(Math.min(dp[i-1][j-1], dp[i-1][j]),dp[i-1][j+1]) + matrix[i][j];
                }

            }
        }
        int ans = dp[m-1][0];
        for(int i =1; i<n;i++){
            ans = Math.min(ans, dp[m-1][i]);
        }
        return ans;
    }

    /**
     * [-73,61,43,-48,-36]
     * [3,30,27,57,10]
     * [96,-76,84,59,-15]
     * [5,-49,76,31,-7]
     * [97,91,61,-46,67]
     *
     * @param grid
     * @return
     */
    public int minFallingPathSumII(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        int[][] dp = new int[m][n];

        for (int i = 0; i < n; i++){
            dp[0][i] = grid[0][i];
        }

        for(int i = 1; i< m; i++){
            for(int j = 0; j< n; j++){
                dp[i][j] =  Integer.MAX_VALUE;
                for(int p = 0; p < n; p++) {
                    if(p != j){
                        dp[i][j] = Math.min(dp[i][j], dp[i-1][p] + grid[i][j]);
                    }
                }
            }
        }
        int ans = dp[m-1][0];
        for(int i =1; i<n;i++){
            ans = Math.min(ans, dp[m-1][i]);
        }
        return ans;
    }
    public int minFallingPathSumII1(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int cfm = 0, csm = 0, lfm = 0, lsm = 0;
        int lfi = 0, cfi = 0, var = 0;
        for(int i = 0; i< m; i++){
            cfm = Integer.MAX_VALUE;
            csm = Integer.MAX_VALUE;
            for(int j =0; j < n; j++){
                var = grid[i][j];
                if(i != 0){
                    var  = var + (lfi != j ? lfm : lsm) ;
                }
                if(var < cfm){
                    csm = cfm;
                    cfm = var;
                    cfi = j;
                }else if(var < csm){
                    csm = var;
                }
            }
            lfm = cfm;
            lsm = cfm;
            lfi = cfi;
        }
        return cfm;
    }
    public int minFallingPathSum2(int[][] matrix) {
        int m = matrix.length,n = matrix[0].length;
        int max = Integer.MAX_VALUE;
        int lfm  = 0,lsm = 0, cfm = 0, csm = 0;
        int lfi = 0, cfi = 0, f = 0;
        for(int i = 0; i < m;i++){
            cfm = max; csm = max;
            for(int j = 0; j < n; j++){
                f = matrix[i][j];
                if(i != 0) {
                    f = f + (lfi != j ? lfm : lsm);
                }
                if(f < cfm){ csm = cfm; cfm = f; cfi = j;}
                else if(f < csm) csm = f;
            }
            lfi = cfi; lfm = cfm; lsm = csm;
        }
        return  cfm;
    }
    public static void main(String[] args) {
       List<List<Integer>> lists = new ArrayList<>();
       List<Integer> l1 = new ArrayList<>();
       l1.add(2);
        List<Integer> l2 = new ArrayList<>();
        l2.add(3);
        l2.add(4);

        List<Integer> l3 = new ArrayList<>();
        l3.add(6);
        l3.add(5);
        l3.add(7);

        List<Integer> l4 = new ArrayList<>();
        l4.add(4);
        l4.add(1);
        l4.add(8);
        l4.add(3);

        lists.add(l1);
        lists.add(l2);
        lists.add(l3);
        lists.add(l4 );
        System.out.println(minimumTotal1(lists));
    }
}
