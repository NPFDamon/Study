package com.damon.algorithm.leecode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day {

    public int numJewelsInStones(String jewels, String stones) {
        Set<Character> set = new HashSet<>();
        List<Character> characters = Arrays.asList();
        for(Character c : jewels.toCharArray()){
            set.add(c);
        }
        int i = 0;
        for(Character c: stones.toCharArray()){
            if(set.contains(c)){
                i++;
            }
        }
        return i;
    }



}
