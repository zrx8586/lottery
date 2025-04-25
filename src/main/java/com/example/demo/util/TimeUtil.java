package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 */
public class TimeUtil {

    // yyyy-MM-dd HH:mm:ss
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // yyyy-MM-dd
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";


    public static String formatDate(LocalDateTime localDateTime, String dateFormat) {
        // 定义格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 转换为字符串
        String formattedDateTime = localDateTime.format(formatter);
        System.out.println("格式化后的时间：" + formattedDateTime);
        return formattedDateTime;
    }
}
