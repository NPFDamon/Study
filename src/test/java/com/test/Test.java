package com.test;

import com.damon.algorithm.ListReverse;
import org.w3c.dom.Node;

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

    public static Node res1(Node headNode){
        if(headNode == null || headNode.next == null){
            return headNode;
        }
        Node newHead  = res1(headNode.next);
        headNode.next.next = headNode;
        headNode.next = null;
        return newHead;
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
        Node head = new Node(0);
        Node one = new Node(1);
        Node two = new Node(2);
        Node three = new Node(3);
        head.next = one;
        one.next = two;
        two.next = three;
        three.next = null;
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
        Node w = res1(head);

        while (w != null) {
            System.out.println(w.getValue());
            w = w.next;
        }
    }
}
