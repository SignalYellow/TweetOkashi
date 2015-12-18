package com.signalyellow.tweetokashi.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    static final String YEAR = "年";
    static final String DAY = "日";
    static final String HOUR = "時間";
    static final String MINUTE = "分";
    static final String SECOND = "秒";

    public static String getAbsoluteTime(Date date){
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
    }

    public static String getRelativeTime(Date date){

        long time = date.getTime();
        long now = System.currentTimeMillis();

        long second = (now - time)/1000;
        if(second < 0) second = 0;
        long minute = second/60;
        long hour = minute/60;
        long day = hour/24;
        long year = day/365;

        return year > 0     ? year + YEAR :
               day > 0      ? day + DAY :
               hour > 0     ? hour + HOUR :
               minute >0    ? minute + MINUTE :
                              second + SECOND;
    }
}
