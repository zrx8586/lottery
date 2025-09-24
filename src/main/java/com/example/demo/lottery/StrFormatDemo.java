package com.example.demo.lottery;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrFormatDemo {

    public static void main(String[] args) {
        String pattern = "很抱歉，因故订单未能预订成功，原因是{{BookFailReason}}。请在{{OrderTimePlus30Min}}前填写退款账户信息。";

        // 假设这些是你想要插入的值
        String bookFailReason = "库存不足";
        String orderTimePlus30Min = "2023-10-15 14:30";

        // 调用 formatWithPlaceholders 方法
        String formattedString = formatWithPlaceholders(pattern, bookFailReason, orderTimePlus30Min);

        System.out.println(formattedString);
    }

    public static String formatWithPlaceholders(String pattern, Object... arguments) {
        // Convert custom placeholders {{...}} to MessageFormat placeholders {...}
        Pattern regex = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = regex.matcher(pattern);
        StringBuffer convertedPattern = new StringBuffer();
        int index = 0;

        while (matcher.find()) {
            matcher.appendReplacement(convertedPattern, "{" + index++ + "}");
        }
        matcher.appendTail(convertedPattern);

        // Replace single quotes for MessageFormat
        String finalPattern = convertedPattern.toString().replace("'", "''");

        // Ensure arguments are not null
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] == null) {
                arguments[i] = "";
            } else {
                arguments[i] = arguments[i].toString();
            }
        }

        // Format the string using MessageFormat
        return MessageFormat.format(finalPattern, arguments);
    }
}
