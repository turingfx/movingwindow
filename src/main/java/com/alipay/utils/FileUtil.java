package com.alipay.utils;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * 文件读取和写入工具类
 */
public class FileUtil {

    /**
     * 使用BufferedReader逐行读取文件
     *
     * @param filePath     文件路径
     * @param lineConsumer 对每一行数据的处理函数
     * @throws IOException 读取文件时发生错误
     */
    public static void readFileWithBufferedReader(String filePath, java.util.function.Consumer<String> lineConsumer, boolean skipTitle)
            throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                if (skipTitle && lineNum++ == 1) {
                    continue;
                }

                if (StringUtils.isBlank(line)) {
                    continue;
                }
                try {
                    lineConsumer.accept(line);
                } catch (Exception e) {
                    // skip it
                    System.out.println("Skip line " + line);
                }
            }
        } catch (IOException e) {
            throw new IOException("读取文件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 将字符串列表写入文件，每行一个字符串
     *
     * @param filePath 文件路径
     * @param lines    要写入的字符串列表
     * @throws IOException 写入文件时发生错误
     */
    public static void writeFileFromList(String filePath, List<String> lines) throws IOException {
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            for (String line : lines) {
                writer.write(line);
                writer.write(System.lineSeparator()); // 添加换行符
            }
        } catch (IOException e) {
            throw new IOException("写入文件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 将字符串列表追加到文件末尾，每行一个字符串
     *
     * @param filePath 文件路径
     * @param lines    要追加的字符串列表
     * @throws IOException 追加文件时发生错误
     */
    public static void appendFileFromList(String filePath, List<String> lines) throws IOException {
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath, true)) {
            for (String line : lines) {
                writer.write(line);
                writer.write(System.lineSeparator()); // 添加换行符
            }
        } catch (IOException e) {
            throw new IOException("追加文件时发生错误: " + e.getMessage());
        }
    }

    /**
     * 将字符串内容写入文件
     *
     * @param filePath 文件路径
     * @param content  要写入的字符串内容
     * @throws IOException 写入文件时发生错误
     */
    public static void writeStringToFile(String filePath, String content) throws IOException {
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            throw new IOException("写入文件时发生错误: " + e.getMessage());
        }
    }
}