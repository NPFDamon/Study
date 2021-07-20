package com.damon.algorithm;


import javax.swing.tree.TreeNode;
import java.util.*;

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

    /**
     * 前序遍历迭代法
     *
     * @param head 头结点
     * @return 1 2 4 5 3 6 7
     */
    public static List<TreeNode> before_Iteration(TreeNode head) {
        if (head == null) {
            return null;
        }
        Stack<TreeNode> stack = new Stack<TreeNode>();
        stack.push(head);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            System.out.println(node.value);
            //压栈操作  先压right
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        return stack;
    }


    /**
     * 中序遍历
     *
     * @param head
     * @return 4 2 5 1 6 3 7
     */
    public static void middle(TreeNode head) {
        if (head == null) {
            return;
        }
        middle(head.left);
        System.out.println(head.value);
        middle(head.right);

    }


    /**
     * 中序遍历迭代法
     *
     * @param head 头结点
     * @return 4 2 5 1 6 3 7
     */
    public static List<TreeNode> middle_Iteration(TreeNode head) {
        if (head == null) {
            return null;
        }
        Stack<TreeNode> stack = new Stack<TreeNode>();
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode curr = head;
        while (!stack.isEmpty() || curr != null) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            TreeNode node = stack.pop();
            System.out.println(node.value);
            treeNodes.add(node);
            if (node.right != null) {
                curr = node.right;
            }
        }
        return treeNodes;
    }

    /**
     * 后续遍历
     *
     * @param head
     * @return 4 5 2 6 7 3 1
     */
    public static void after(TreeNode head) {
        if (head == null) {
            return;
        }
        after(head.left);
        after(head.right);
        System.out.println(head.value);

    }


    /**
     * 后续遍历迭代法
     *
     * @param head 头结点
     * @return 4 5 2 6 7 3 1
     */
    public static List<TreeNode> after_Iteration(TreeNode head) {
        if (head == null) {
            return null;
        }
        Stack<TreeNode> stack1 = new Stack<TreeNode>();
        Stack<TreeNode> stack2 = new Stack<TreeNode>();
        stack1.push(head);
        while (!stack1.isEmpty()) {
            TreeNode node = stack1.pop();
            stack2.push(node);
            if (node.left != null) {
                stack1.push(node.left);
            }
            if (node.right != null) {
                stack1.push(node.right);
            }
        }
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        while (!stack2.isEmpty()) {
//            treeNodes.add(stack2.pop());
            System.out.println(stack2.pop().value);
        }
        return treeNodes;
    }

    /**
     * 二叉树层级遍历
     *
     * @param head head
     * @return 1 2 3 4 5 6 7
     */
    public static List<TreeNode> layerTree(TreeNode head) {
        if (head == null) {
            return null;
        }
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(head);
        List<TreeNode> lay = new ArrayList<>();
        while (!queue.isEmpty()) {
            int n = queue.size();
            List<TreeNode> lay1 = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                TreeNode node = queue.poll();
                lay1.add(node);
                if (node.left != null) {
                    queue.add(node.left);
                }
                if (node.right != null) {
                    queue.add(node.right);
                }
            }
            lay.addAll(lay1);
        }
        return lay;
    }

    /**
     * 二叉树翻转
     *
     * @param head tree node
     * @return 翻转后的二叉树
     */
    public static TreeNode invertTree(TreeNode head) {
        if (head == null) {
            return head;
        }

        TreeNode tempNode = head.right;
        head.right = head.left;
        head.left = tempNode;

        invertTree(head.left);
        invertTree(head.right);

        return head;
    }


    /**
     * 翻转二叉树（https://github.com/NPFDamon/Study/blob/main/src/main/java/com/damon/algorithm/pic/invertTree.gif）
     *
     * @param head tree node
     * @return 翻转后的二叉树
     */
    public static TreeNode invertTree_Iteration(TreeNode head) {
        if (head == null) {
            return head;
        }

        //节点队列
        Queue<TreeNode> queue = new ArrayDeque<>();
        //将二叉树的节点放入队列
        queue.add(head);
        while (!queue.isEmpty()) {
            //二叉树左右互换
            TreeNode node = queue.poll();
            TreeNode tempNode = node.right;
            node.right = node.left;
            node.left = tempNode;
            //互换后的节点放入队列
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        }

        return head;
    }

    public static boolean isBalance(TreeNode node) {
        return check(node, node);
    }

    public static boolean check(TreeNode p, TreeNode q) {
        //堆栈保存节点
        Stack<TreeNode> queue = new Stack<>();
        //节点左右孩子
        queue.add(p);
        queue.add(q);
        while (!queue.isEmpty()) {
            //节点弹出，判断
            TreeNode node1 = queue.pop();
            TreeNode node2 = queue.pop();
            if (node1 == null && node2 == null) {
                continue;
            }
            if (node1 == null || node2 == null) {
                return false;
            }
            if (!node1.value.equals(node2.value)) {
                return false;
            }

            //栈是先进后出，所以压栈操作为倒序压栈
            //先压栈右节点的左孩子
            queue.add(node2.left);
            //压栈左节点的右孩子
            queue.add(node1.right);

            //压栈右节点的右孩子
            queue.add(node2.right);
            //压栈左节点的左孩子
            queue.add(node1.left);
            /**
             * 如果使用数组
             * //用队列保存节点
             * 		LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
             * 		//将根节点的左右孩子放到队列中
             * 		queue.add(root.left);
             * 		queue.add(root.right);
             * 		while(queue.size()>0) {
             * 			//从队列中取出两个节点，再比较这两个节点
             * 			TreeNode left = queue.removeFirst();
             * 			TreeNode right = queue.removeFirst();
             * 			//如果两个节点都为空就继续循环，两者有一个为空就返回false
             * 			if(left==null && right==null) {
             * 				continue;
             *                        }
             * 			if(left==null || right==null) {
             * 				return false;
             *            }
             * 			if(left.val!=right.val) {
             * 				return false;
             *            }
             * 			//将左节点的左孩子， 右节点的右孩子放入队列
             * 			queue.add(left.left);
             * 			queue.add(right.right);
             * 			//将左节点的右孩子，右节点的左孩子放入队列
             * 			queue.add(left.right);
             * 			queue.add(right.left);
             */

        }
        return true;
    }

    /**
     * 二叉树节点
     * 1
     * /  \
     * 2     3
     * /  \   / \
     * 4    5 6   7
     */
    public static class TreeNode {
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

    public static void main(String[] args) {
        TreeNode head = new TreeNode(1);
        TreeNode left = new TreeNode(2);
        TreeNode right = new TreeNode(2);
        TreeNode leftLeft = new TreeNode(3);
        TreeNode leftRight = new TreeNode(4);
        TreeNode rightLeft = new TreeNode(4);
        TreeNode rightRight = new TreeNode(3);
        head.left = left;
        head.right = right;
        left.left = leftLeft;
        left.right = leftRight;
        right.left = rightLeft;
        right.right = rightRight;

//        before(head);
//        middle(head);
//        after(head);
//        List<TreeNode> stack = before_Iteration(head);
//        List<TreeNode> stack = middle_Iteration(head);
//        List<TreeNode> stack = after_Iteration(head);
//        List<TreeNode> stack = layerTree(head);
//        TreeNode node = invertTree(head);
//        TreeNode node_1 = invertTree_Iteration(head);
        boolean balance = isBalance(head);
        System.out.println("End :" + balance);
    }
}
