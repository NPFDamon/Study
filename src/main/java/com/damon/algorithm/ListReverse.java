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
    public static ListNode reverseForeach(ListNode head) {
        // 前一节点
        ListNode prev = null;
        // 当前节点
        ListNode curr = head;

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
            ListNode next = curr.next;
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
    public static ListNode reverseRecursion(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode newHead = reverseRecursion(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    /**
     * 合并两个有序链表
     */
    public static ListNode merge(ListNode n1, ListNode n2) {
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
    public static ListNode merge_Iteration(ListNode n1, ListNode n2) {
        ListNode dummyHead = new ListNode(0);

        ListNode tail = dummyHead;
        while (n1 != null && n2 != null) {
            if (n1.value < n2.value) {
                tail.next = n1;
                n1 = n1.next;
            } else {
                tail.next = n2;
                n2 = n2.next;
            }
            tail = tail.next;
        }

        tail.next = n1 == null ? n2 : n1;
        return dummyHead.next;
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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode head = null, tail = null;
        int val = 0;
        while (l1 != null || l2 != null) {
            int v1 = l1 != null ? l1.value : 0;
            int v2 = l2 != null ? l2.value : 0;
            int sum = v1 + v2 + val;
            if (head == null) {
                head = tail = new ListNode(sum % 10);
            } else {
                tail.next = new ListNode(sum % 10);
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
            tail.next = new ListNode(val);
        }
        return head;

    }

    /**
     * 合并n个有序链表
     * 给你一个链表数组，每个链表都已经按升序排列。
     * <p>
     * 请你将所有链表合并到一个升序链表中，返回合并后的链表。
     * https://leetcode-cn.com/problems/merge-k-sorted-lists/
     *
     * @param lists
     * @return
     */
//    public Node mergeKLists(Node[] lists) {
//
//
//    }

    /**
     * 删除倒数第n个节点
     *
     * @param head
     * @param n
     * @return
     */
    public static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode listNode = new ListNode(0);
        listNode.next = head;
        ListNode first = head;
        ListNode second = listNode;
        for (int i = 0; i < n; i++) {
            first = first.next;
        }

        while (first != null) {
            first = first.next;
            second = second.next;
        }

        second.next = second.next.next;
        return listNode.next;

    }

    /**
     * 合并n个升序链表
     *
     * @param lists
     * @return
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 0) {
            return null;
        }
        ListNode node = null;

        for (ListNode list : lists) {
            node = merge_Iteration(node, list);
        }
        return node;
    }


    public ListNode mergeKLists_two(ListNode[] lists) {
        if (lists.length == 0) {
            return null;
        }
        int k = lists.length;
        while (k > 1) {
            int idx = 0;
            for (int i = 0; i < k; i += 2) {
                if (i == k - 1) {
                    lists[idx++] = lists[i];
                } else {
                    lists[idx++] = merge_Iteration(lists[i], lists[i + 1]);
                }
            }
            k = idx;
        }
        return lists[0];
    }


    private static class ListNode {
        private Integer value;

        private ListNode next;

        public ListNode(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public ListNode setValue(Integer value) {
            this.value = value;
            return this;
        }

        public ListNode getNext() {
            return next;
        }

        public ListNode setNext(ListNode next) {
            this.next = next;
            return this;
        }
    }

    /**
     * temp -> 1 -> 2 -> 3
     *
     * @param head
     * @return
     */

    public ListNode swapPairs(ListNode head) {
        ListNode dyn = new ListNode(0);
        dyn.next = head;
        ListNode temp = dyn;
        while (temp.next != null && temp.next.next != null){
            ListNode n1 = temp.next;
            ListNode n2 = temp.next.next;
            temp.next = n2;
            n1.next = n2.next;
            temp.next.next = n1;
            temp = n1;
        }
        return dyn.next;
    }

    /**
     * k个一组反转链表
     *
     * @param head
     * @param k
     * @return
     */
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode hair = new ListNode(0);
        hair.next = head;
        ListNode pre = hair;
        while (head != null){
            ListNode tail = pre;
            //找到尾结点
            for(int i = 0; i < k ; i++){
                tail = tail.next;
                while (tail == null){
                    return hair.next;
                }
            }
            ListNode nex = tail.next;
            ListNode[] res = myReverse(head, tail);
            head = res[0];
            tail = res[1];
            pre.next = head;
            tail.next = nex;

            pre = tail;
            head = tail.next;

        }
        return hair.next;
    }

    /**
     * 1 -> 2 -> 3
     * @param head
     * @param tail
     * @return
     */
    public ListNode[] myReverse(ListNode head, ListNode tail) {
        ListNode prev = tail.next;
        ListNode p = head;
        while (prev != tail) {
            ListNode nex = p.next;
            p.next = prev;
            prev = p;
            p = nex;
        }
        return new ListNode[]{tail, head};
    }



    public static void main(String[] args) {
        ListNode head = new ListNode(0);
        ListNode one = new ListNode(1);
        ListNode two = new ListNode(2);
        ListNode three = new ListNode(3);
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
        ListNode w = reverseRecursion(head);

        while (w != null) {
            System.out.println(w.getValue());
            w = w.next;
        }
    }
}
