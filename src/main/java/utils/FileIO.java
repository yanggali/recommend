package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yangjiali on 2017/9/5 0005.
 * Version 1.0
 */
public class FileIO {
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        List<String> filelist = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 0;  //记录行数
            while ((tempString = reader.readLine()) != null) {
                filelist.add(tempString);
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
        return filelist;
    }
}
