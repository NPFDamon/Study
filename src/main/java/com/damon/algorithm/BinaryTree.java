package com.damon.algorithm;


/**
 * @Author: cashmama
 * @Date: 2021/7/5-17:32
 * @Description:
 * @Slogan: To be or not to be
 **/
public class BinaryTree {


    /**
     * 前序遍历
     *
     * @param head
     * @return 1 2 4 5 3 6 7
     */
    public static void before(TreeNode head) {
        if (head == null) {
            return;
        }
        System.out.println(head.value);

        before(head.left);
        before(head.right);
    }

    public static void before_Iteration(TreeNode head) {
        if (head == null) {
            return;
        }
        System.out.println(head.value);

        before(head.left);
        before(head.right);
    }


    /**
     * 中序遍历
     * @param head
     * @return 4 2 5 1 6 3 7
     */
    public static void middle(TreeNode head){
        if(head ==null){
            return;
        }
        middle(head.left);
        System.out.println(head.value);
        middle(head.right);

    }

    /**
     * 后续遍历
     * @param head
     * @return  4 2 5 6 3 7 1
     */
    public static void after(TreeNode head){
        if(head ==null){
            return;
        }
        middle(head.left);
        middle(head.right);
        System.out.println(head.value);

    }

    /**
     * 二叉树节点
     *          1
     *        /  \
     *      2     3
     *    /  \   / \
     *   4    5 6   7
     *
     */
    private static class TreeNode {
        private TreeNode left;
        private TreeNode right;
        private Integer value;

        public TreeNode(Integer value) {
            this.value = value;
        }

        public TreeNode getLeft() {
            return left;
        }

        public TreeNode setLeft(TreeNode left) {
            this.left = left;
            return this;
        }

        public TreeNode getRight() {
            return right;
        }

        public TreeNode setRight(TreeNode right) {
            this.right = right;
            return this;
        }

        public Integer getValue() {
            return value;
        }

        public TreeNode setValue(Integer value) {
            this.value = value;
            return this;
        }
    }

    public static void main(String[]  args){
        TreeNode head = new TreeNode(1);
        TreeNode left = new TreeNode(2);
        TreeNode right = new TreeNode(3);
        TreeNode leftLeft = new TreeNode(4);
        TreeNode leftRight = new TreeNode(5);
        TreeNode rightLeft = new TreeNode(6);
        TreeNode rightRight = new TreeNode(7);
        head.left = left;
        head.right = right;
        left.left = leftLeft;
        left.right = leftRight;
        right.left = rightLeft;
        right.right = rightRight;

//        before(head);
//        middle(head);
        after(head);
    }
}
