package com.damon.algorithm;


/**
 * @Author: Damon
 * @Date: 2021/6/30-09:12
 * @Description: 链表反转
 * @Slogan: To be or not to be
 **/
public class ListReverse {

    /**
     * 遍历方式
     *
     * @param head
     * @return
     */
    public static Node reverseForeach(Node head) {
        // 前一节点
        Node prev = null;
        // 当前节点
        Node curr = head;

        /**
         * 1 -> 2 -> 3 -> null
         * 第一次遍历
         * cuur = 1
         * next = 2
         * 1 -> null
         * cuur.next = pre //头结点prev = null
         * prev = cuur
         * cuur = 2
         * 第二次遍历
         * next = 3
         * 2.next -> pre //pre = 1
         * 2 -> 1 -> null
         * pre = 2
         * cuur = 3
         * 第三次遍历
         * next = null
         * 3 -> 2 -> 1 -> null
         * pre = 3
         * cuur = null
         * 结束
         *
         */
        while (curr != null) {
            //暂存当前节点的next节点
            Node next = curr.next;
            //当前节点的下一节设置为前一节点
            curr.next = prev;
            //前一节点设置为当前节点
            prev = curr;
            //当前节点设置为下一节点
            curr = next;
        }
        return prev;
    }


    /**
     * 递归方式
     * detail 参考:https://www.huaweicloud.com/articles/8357697.html
     *
     *
     * @param head
     * @return
     */
    public static Node reverseRecursion(Node head) {
        if (head == null || head.next == null) {
            return head;
        }

        Node newHead = reverseRecursion(head.next);
        head.next.next = head;
        head.next = null;
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
        Node w = reverseRecursion(head);

        while (w != null) {
            System.out.println(w.getValue());
            w = w.next;
        }
    }
}
