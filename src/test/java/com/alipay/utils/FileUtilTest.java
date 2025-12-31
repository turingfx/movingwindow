package com.alipay.utils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 文件工具类测试
 */
public class FileUtilTest {
    
    private static final String TEST_FILE = "test_output.txt";
    
    @Test
    public void testWriteFileFromList() throws IOException {
        List<String> lines = Arrays.asList("第一行", "第二行", "第三行");
        
        // 测试写入文件
        FileUtil.writeFileFromList(TEST_FILE, lines);
        
        // 验证文件内容
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                assertEquals(lines.get(lineCount), line);
                lineCount++;
            }
        }
        
        assertEquals("文件行数应该与输入列表大小相同", lines.size(), lineCount);
    }
    
    @Test
    public void testAppendFileFromList() throws IOException {
        List<String> lines1 = Arrays.asList("第一行", "第二行");
        List<String> lines2 = Arrays.asList("追加行1", "追加行2");
        
        // 先写入初始数据
        FileUtil.writeFileFromList(TEST_FILE, lines1);
        
        // 追加数据
        FileUtil.appendFileFromList(TEST_FILE, lines2);
        
        // 验证文件内容
        List<String> expectedLines = Arrays.asList("第一行", "第二行", "追加行1", "追加行2");
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                assertEquals(expectedLines.get(lineCount), line);
                lineCount++;
            }
        }
        
        assertEquals("文件行数应该与预期列表大小相同", expectedLines.size(), lineCount);
    }
    
    @Test
    public void testWriteStringToFile() throws IOException {
        String content = "这是一个测试字符串";
        
        // 测试写入字符串到文件
        FileUtil.writeStringToFile(TEST_FILE, content);
        
        // 验证文件内容
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (fileContent.length() > 0) {
                    fileContent.append("\n");
                }
                fileContent.append(line);
            }
        }
        
        assertEquals("文件内容应该与写入的字符串相同", content, fileContent.toString());
    }
    
    @Test
    public void testReadFileWithBufferedReader() throws IOException {
        List<String> lines = Arrays.asList("第一行", "第二行", "第三行");
        FileUtil.writeFileFromList(TEST_FILE, lines);
        
        // 测试读取文件
        int[] lineCount = {0}; // 使用数组以在lambda中修改值
        FileUtil.readFileWithBufferedReader(TEST_FILE, line -> {
            assertEquals(lines.get(lineCount[0]), line);
            lineCount[0]++;
        },true);
        
        assertEquals("读取的行数应该与文件行数相同", lines.size(), lineCount[0]);
    }
}