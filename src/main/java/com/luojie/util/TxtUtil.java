package com.luojie.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 简单的txt文件工具 类
 *
 */
@Slf4j
public class TxtUtil {

    private static final String LOG_DIRECTORY = "D:\\tmp\\log"; // 指定日志文件夹
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void writeLog(String message) {
        // 获取当前日期
        String currentDate = dateFormat.format(new Date());
        // 构建文件路径
        String filePath = LOG_DIRECTORY + "/" + currentDate + ".txt";

        // 创建目录如果不存在
        try {
            Files.createDirectories(Paths.get(LOG_DIRECTORY));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // 追加写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(message);
            writer.newLine(); // 换行
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
