package code.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentTimeFiller {
    public static String fillFilename(String str) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd_HH_mm_ss");

        // 将当前时间格式化为字符串
        String currentTime = now.format(formatter);

        // 使用字符串填充

        // 打印填充后的字符串
        return String.format(str, currentTime);
    }

    public static void main(String[] args) {
        String type = "IMDB";
        System.out.println(type.toLowerCase());
    }
}
