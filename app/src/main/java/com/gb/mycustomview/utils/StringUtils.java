package com.gb.mycustomview.utils;

/**
 * @author Gerkey
 * Created on 2021/9/23
 */
public class StringUtils {

    public static int get(String s) {
        return Integer.parseInt(s.substring(0, s.length() - 1));
    }
}

