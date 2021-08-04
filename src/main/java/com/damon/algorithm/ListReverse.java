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

    /**
     * 合并两个有序链表
     */
    public static Node merge(Node n1, Node n2) {
        if (n1 == null) {
            return n2;
        } else if (n2 == null) {
            return n1;
        } else if (n1.value < n2.value) {
            n1.next = merge(n1.next, n1);
            return n1;
        } else {
            n2.next = merge(n1, n2.next);
            return n2;
        }
    }


    /**
     * 合并两个有序链表
     */
    public static Node merge_Iteration(Node n1, Node n2) {
        Node pre = new Node(1);

        Node prev = pre;
        while (n1 != null && n2 != null) {
            if (n1.value < n2.value) {
                prev.next = n1;
                n1 = n1.next;
            } else {
                prev.next = n2;
                n2 = n2.next;
            }
            prev = prev.next;
        }

        prev.next = n1 == null ? n2 : n1;
        return prev;
    }


    /**
     * 两数相加
     * 给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。
     * <p>
     * 请你将两个数相加，并以相同形式返回一个表示和的链表。
     * <p>
     * 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/add-two-numbers
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     *
     * @param l1
     * @param l2
     * @return
     */
    public Node addTwoNumbers(Node l1, Node l2) {
        Node head = null, tail = null;
        int val = 0;
        while (l1 != null || l2 != null) {
            int v1 = l1 != null ? l1.value : 0;
            int v2 = l2 != null ? l2.value : 0;
            int sum = v1 + v2 + val;
            if (head == null) {
                head = tail = new Node(sum % 10);
            } else {
                tail.next = new Node(sum % 10);
                tail = tail.next;
            }
            //进位数
            val = sum / 10;
            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        }
        if (val > 0) {
            tail.next = new Node(val);
        }
        return head;

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
