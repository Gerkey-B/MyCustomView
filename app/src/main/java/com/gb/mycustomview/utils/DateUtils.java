package com.gb.mycustomview.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Gerkey
 * Created on 2021/9/22
 */
public class DateUtils {

    /**
     * 获取当前的年月日,时分秒
     *
     * @return
     */
    public static String getNowDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date();
        String timeStr = simpleDateFormat.format(date);
        return timeStr;
    }

    /**
     * 获取当前的时间,只包括年月日
     *
     * @return
     */
    public static String getNowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String time = simpleDateFormat.format(date);
        return time;
    }


    public static Integer getCurYear() {
        Calendar calendar = Calendar.getInstance();
        return  calendar.get(Calendar.YEAR);
    }


    public static Integer getCurMonth() {
        Calendar calendar = Calendar.getInstance();
        return  calendar.get(Calendar.MONTH);
    }


    public static Integer getCurDay() {
        Calendar calendar = Calendar.getInstance();
        return  calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Integer getCurHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static Integer getCurMinute() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

}
