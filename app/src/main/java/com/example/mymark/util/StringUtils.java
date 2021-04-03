package com.example.mymark.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static final Integer parseInteger(String str, Integer defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static List<String> split(String text, String regex) {
        List<String> list = new ArrayList<>();
        String [] tmp = text.split(regex);
        for (int i = 0; i < tmp.length; i++) {
            list.add(tmp[i]);
        }
        return list;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
