package com.haizhi.graph.common.file;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/8/28.
 */
public class Files {

    public static List<File> getFiles(String filePath) {
        return FileUtils.getFiles(filePath);
    }

    public static double getDirSize(String filePath) {
        return FileUtils.getDirSize(new File(filePath));
    }

    public static Map<String, Object> readLines(File file, String encoding, long pos, int num) {
        Map<String, Object> result = Maps.newHashMap();
        List<String> lines = Lists.newArrayList();
        result.put("lines", lines);
        try (BufferedRandomAccessFile reader = new BufferedRandomAccessFile(file, "r")) {
            reader.seek(pos);
            for (int i = 0; i < num; i++) {
                String line = reader.readLine();
                if (StringUtils.isBlank(line)) {
                    break;
                }
                lines.add(new String(line.getBytes("8859_1"), encoding));
            }
            result.put("pos", reader.getFilePointer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
