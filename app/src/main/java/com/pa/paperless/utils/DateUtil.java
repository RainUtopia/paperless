package com.pa.paperless.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/12/26.
 */

public class DateUtil {


    public static String getTim(long time) {
        Date date = new Date(time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(date);
    }
    /**
     * 转换成时间格式   8:30
     *
     * @param time
     * @return
     */
    public static String getTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GTM"));
        return timeFormat.format(date);
    }

    /**
     * 转成时分秒 00::00:00
     */
    //time 单位 毫秒
    public static String convertTime(long ms) {
        String ret = "";
        Date date = new Date(ms);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GTM"));
        ret = timeFormat.format(date);
        return ret;
    }

    //time 单位 秒
    public static String convertTime(int sec) {
        return convertTime((long) sec * 1000);
    }

    /**
     * 月、星期、时间
     * day :01月01日  week:  星期四   time:  08:00
     *
     * @param time 时间 单位是毫秒
     * @return
     */
    public static String[] getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String day = dateFormat.format(date);
        String week = weekFormat.format(date);
        String tim = timeFormat.format(date);
        String[] dateStr = new String[]{day, week, tim};
        return dateStr;
    }

    /**
     * @param time 单位 毫秒
     *             时区设置：SimpleDateFormat对象.setTimeZone(TimeZone.getTimeZone("GTM"));
     * @return String id = TimeZone.getDefault().getID();
     */
    public static String[] getGTMDate(long time) {

        Date tTime = new Date(time);

        SimpleDateFormat day = new SimpleDateFormat("MM月dd日");
        day.setTimeZone(TimeZone.getTimeZone("GTM"));
        String dayt = day.format(tTime);

        SimpleDateFormat week = new SimpleDateFormat("E");
        week.setTimeZone(TimeZone.getTimeZone("GTM"));
        String weekt = week.format(tTime);

        SimpleDateFormat tim = new SimpleDateFormat("HH:mm:ss");
        tim.setTimeZone(TimeZone.getTimeZone("GTM"));
        String timt = tim.format(tTime);

        String[] date = {dayt, weekt, timt};
        return date;
    }
}
