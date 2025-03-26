package com.devops.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author yuxc
 * @date 2025/2/21
 */
public class TimeUtils {

    /**
     * Date 转 String
     *
     * @param date   Date
     * @param format String
     * @return String
     */
    public static String getTimeStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前时间 格式：yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public static String getNowTimeStr() {
        return getTimeStr(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式化时间字符串
     *
     * @param dateStr String 时间字符串
     * @return Date 时间对象
     */
    public static Date formatDateStr(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(dateStr);
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 获取当前时间 舍弃毫秒
     *
     * @return Date 时间对象
     */
    public static Date getNowDate() {
        return formatDateStr(getNowTimeStr());
    }

    /**
     * 获取当前时间 格式：yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getNowTimeLong() {
        return getTimeStr(new Date(), "yyyyMMddHHmmss");
    }

    /**
     * 格式化时间 格式：yyyyMMddHHmmss
     *
     * @param date Date
     * @return String
     */
    public static String formatTimeLong(Date date) {
        return getTimeStr(date, "yyyyMMddHHmmss");
    }

    /**
     * 格式化时间 格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date Date
     * @return String
     */
    public static String formatTimeStr(Date date) {
        return getTimeStr(date, "yyyy-MM-dd HH:mm:ss");
    }

}
