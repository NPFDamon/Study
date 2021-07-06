package com.test;

import com.damon.algorithm.BinaryTree;
import com.damon.algorithm.ListReverse;
import org.w3c.dom.Node;

import javax.swing.tree.TreeNode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @Author: cashmama
 * @Date: 2021/6/30-13:51
 * @Description:
 * @Slogan: To be or not to be
 **/
public class Test {

    public static Node res(Node headNode) {
        Node prev = null;
        Node cur = headNode;
        while (cur != null) {
            Node next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    public static Node res1(Node headNode) {
        if (headNode == null || headNode.next == null) {
            return headNode;
        }
        System.out.println("e:" + headNode.value);
        Node newHead = res1(headNode.next);
        System.out.println(":" + headNode.value);
        System.out.println("n:" + headNode.next.value);

        /**
         * 2.next = 3
         * 2.next.next = 3.next
         */
        headNode.next.next = headNode;
        headNode.next = null;
        return newHead;
    }


    public static List<BinaryTree.TreeNode> lay(BinaryTree.TreeNode headNode) {
        if (headNode == null) {
            return null;
        }

        List<BinaryTree.TreeNode> res = new ArrayList<>();
        Queue<BinaryTree.TreeNode> queue = new ArrayDeque<>();
        queue.add(headNode);
        while (!queue.isEmpty()) {
            List<BinaryTree.TreeNode> list = new ArrayList<>();
            for (int i = 0; i < queue.size(); i++) {
                BinaryTree.TreeNode node = queue.poll();
                list.add(node);

                if (node.getLeft() != null) {
                    queue.add(node.getLeft());
                }

                if (node.getRight() != null) {
                    queue.add(node.getRight());
                }
            }
            res.addAll(list);
        }
        return res;
    }


    private static class Node {
        private Integer value;

        private Node next;

        public Node(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public Node setValue(Integer value) {
            this.value = value;
            return this;
        }

        public Node getNext() {
            return next;
        }

        public Node setNext(Node next) {
            this.next = next;
            return this;
        }
    }

    public static void main(String[] args) {
//        Node head = new Node(0);
//        Node one = new Node(1);
//        Node two = new Node(2);
//        Node three = new Node(3);
//        head.next = one;
//        one.next = two;
//        two.next = three;
//        three.next = null;
//        while (head != null) {
//            System.out.println(head.getValue());
//            head = head.next;
//        }

//        Node s = reverseForeach(head);
//
//        while (s != null) {
//            System.out.println(s.getValue());
//            s = s.next;
//        }
//
//        Node w = res1(head);
//
//        while (w != null) {
//            System.out.println(w.getValue());
//            w = w.next;
//        }

        BinaryTree.TreeNode head = new BinaryTree.TreeNode(1);
        BinaryTree.TreeNode left = new BinaryTree.TreeNode(2);
        BinaryTree.TreeNode right = new BinaryTree.TreeNode(3);
        BinaryTree.TreeNode leftLeft = new BinaryTree.TreeNode(4);
        BinaryTree.TreeNode leftRight = new BinaryTree.TreeNode(5);
        BinaryTree.TreeNode rightLeft = new BinaryTree.TreeNode(6);
        BinaryTree.TreeNode rightRight = new BinaryTree.TreeNode(7);
        head.setLeft(left);
        head.setRight(right);
        head.getLeft().setLeft(leftLeft);
        head.getLeft().setRight(leftRight);
        head.getRight().setLeft(rightLeft);
        head.getRight().setRight(rightRight);

        List<BinaryTree.TreeNode> res = lay(head);
        System.out.println(res.toString());
    }
}
