package com.lennonwoo.rubber.utils;

public class Utils {

    public static String durationToString(int duration) {
        int second = duration % 60;
        int minute = duration / 60;
        StringBuilder builder = new StringBuilder();
        builder.append(minute);
        builder.append(":");
        if (second / 10 == 0) {
            builder.append("0");
        }
        builder.append(second);
        return builder.toString();
    }

}
