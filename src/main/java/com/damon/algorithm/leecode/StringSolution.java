package com.damon.algorithm.leecode;

import java.util.*;

public class StringSolution {

    /**
     * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。答案可以按 任意顺序 返回。
     * <p>
     * https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number/
     * </p>
     *
     * @param digits
     * @return
     */
    public static List<String> letterCombinations(String digits) {
        List<String> combinations = new ArrayList<String>();
        if (digits.length() == 0) {
            return combinations;
        }
        Map<Character, String> phoneMap = new HashMap<Character, String>() {{
            put('0', "");
            put('1', "");
            put('2', "abc");
            put('3', "def");
            put('4', "ghi");
            put('5', "jkl");
            put('6', "mno");
            put('7', "pqrs");
            put('8', "tuv");
            put('9', "wxyz");
        }};
        backtrack(combinations, digits, phoneMap, new StringBuilder(), 0);
        return combinations;
    }

    public static void backtrack(List<String> combinations, String digits, Map<Character, String> map, StringBuilder stringBuilder, int index) {
        if (index == digits.length()) {
            combinations.add(stringBuilder.toString());
        } else {
            char dig = digits.charAt(index);
            String s = map.get(dig);
            if (s != null) {
                for (int i = 0; i < s.length(); i++) {
                    stringBuilder.append(s.charAt(i));
                    backtrack(combinations, digits, map, stringBuilder, index + 1);
                    stringBuilder.deleteCharAt(index);
                }
            }

        }
    }

    /**
     * 有效的括号
     * @param s
     * @return
     */

    public static boolean isValid(String s) {
        if (s.isEmpty()) {
            return false;
        }
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(')');
            } else if (c == '[') {
                stack.push(']');
            } else if (c == '{') {
                stack.push('}');
            } else if (stack.isEmpty() || c != stack.pop()) {
                return false;
            }
        }
        return stack.isEmpty();
    }


    public static void main(String[] args) {

//        List<String> res = letterCombinations("12");
        boolean a = isValid("()[]{}");
        System.out.println("=println===>");

    }
}
