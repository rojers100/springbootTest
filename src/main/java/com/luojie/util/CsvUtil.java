package com.luojie.util;

import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    /**
     * 根据指定的类读取csv文件
     *
     * @param filepath 文件路径
     * @param clazz 读取类
     * @param startLine 开始行
     * @return
     * @param <T>
     */
    public static <T> List<T> CSVToBean(String filepath, Class<T> clazz, int startLine) throws IOException {
        BufferedReader reader = createBufferedReader(filepath);
        try {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader).withType(clazz) // 映射的目标类型
                    .withIgnoreLeadingWhiteSpace(true) //忽略 CSV 文件中每行开头的空白字符。
                    .withSkipLines(startLine)  // 从第几行开始读取数据
                    .withIgnoreEmptyLine(true) // 忽略空行
                    .build();
            List<T> resultObj = csvToBean.parse();
            return resultObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            reader.close();
        }

    }

    private static BufferedReader createBufferedReader(String filepath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filepath);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    private static BufferedWriter createBufferedWriter(String filepath) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(filepath);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        return new BufferedWriter(outputStreamWriter);
    }

    /**
     * 写为csv文件
     *
     * @param filepath
     * @param content
     */
    public static void writeToFile(String filepath, Object content) throws IOException {
        BufferedWriter bufferedWriter = createBufferedWriter(filepath);
        try {
            if (content instanceof List) {
                writeList(bufferedWriter, (List<Object>) content);
            } else {
                writeSingle(bufferedWriter, content);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    private static void writeList(BufferedWriter bufferedWriter, List<Object> content) throws IOException {
        CSVWriter csvWriter = new CSVWriter(bufferedWriter);

        try {
            /**
             * 方法一：简单输出为一行 或者输出为多行
             */
            String[] strings = new String[content.size()];
            for (int i = 0; i < content.size(); i++) {
                strings[i] = content.get(i).toString();
                // 输出到多行
    //            csvWriter.writeNext(new String[]{strings[i]});
            }
            // 只会输出到一行
//        csvWriter.writeAll(Collections.singleton(strings));

            /**
             * 方法二： 输出格式按csv的格式
             */
            for (Object e : content) {
                Method[] methods = e.getClass().getMethods();
                List<String> list = new ArrayList<>();

                // 遍历所有方法
                for (Method method : methods) {
                    // 如果方法是 getter 方法（以 "get" 或 "is" 开头，并且没有参数）
                    if (isGetter(method)) {
                        try {
                            // 调用 getter 方法获取属性值
                            String value = String.valueOf(method.invoke(e));
                            // 加入list
                            list.add(value);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                writeForList(csvWriter, list);
                list.clear();
            }
        } finally {
            csvWriter.flush();
            csvWriter.close();
        }
    }

    private static void writeForList(CSVWriter csvWriter, List<String> list) {
        String[] array = list.toArray(new String[0]);
        csvWriter.writeNext(array);
    }

    // 判断方法是否为 getter 方法
    private static boolean isGetter(Method method) {
        String name = method.getName();
        return (name.startsWith("get") || name.startsWith("is"))
                && !name.startsWith("getClass")
                && method.getParameterCount() == 0
                && !void.class.equals(method.getReturnType());
    }

    private static void writeSingle(BufferedWriter bufferedWriter, Object content) throws Exception {
        ICSVWriter writer = new CSVWriterBuilder(bufferedWriter)
                .withSeparator(',')
                .withQuoteChar('"')
                .withEscapeChar('\\')
                .withLineEnd("\n")
                .build();
        StatefulBeanToCsv<Object> beanToCsv = new StatefulBeanToCsvBuilder<Object>(writer)
                .build();

        // 将对象列表写入 CSV 文件
        beanToCsv.write(content);
    }
}
