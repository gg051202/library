package gg.base.library.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by guilin on 2019-07-30 11:24.
 * email 973635949@qq.com
 */
public class LocalLogUtil {

    public static String getLogPath(Context context) {
        return context.getCacheDir() + File.separator + "http_log.txt";
    }

    public static void writeLogtoFile(Context context, String tag, String text) {
        String needWriteMessage = tag + "    " + text + "huanhang";

        // 取得日志存放目录
        String path = getLogPath(context);
        try {
            // 创建目录
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            // 打开文件
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.flush();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearLogFile(Context context) {
        File file = new File(getLogPath(context));
        if (file.exists()) {
            file.delete();
        }
    }

    public static String getLogString(Context context) {
        return readFileByLines(getLogPath(context));
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static String readFileByLines(String fileName) {
        StringBuilder result = new StringBuilder();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                result.append(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return result.toString().replaceAll("huanhang", "\n");
    }
}
