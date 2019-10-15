package com.haizhi.graph.plugins.flume.source.file.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.file.BufferedRandomAccessFile;
import com.haizhi.graph.plugins.flume.source.file.model.FileType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HaiyangWork on 2018/12/19.
 */
public class FileParser {
    private static String[] fields = null;

    private static final Logger logger = LoggerFactory.getLogger(FileParser.class);

    public static Map<String, Object> readLines(File file, String encoding, long pos, int num) {
        Map<String, Object> result = Maps.newHashMap();
        List<String> lines = Lists.newArrayList();
        FileType fileType = FileType.JSON;
        try (BufferedRandomAccessFile reader = new BufferedRandomAccessFile(file, "r")) {
            reader.seek(pos);
            if(pos == 0 ) {
                String firstLine = reader.readLine();
                try{
                    JSONObject.parseObject(firstLine);
                    fileType = FileType.JSON;
                    lines.add(firstLine);
                } catch (Exception e){
                    fields = firstLine.split(",");
                    fileType = FileType.CSV;
                }
            }

            for (int i = 0; i < num; i++) {
                String line = reader.readLine();
                if (StringUtils.isBlank(line)) { break; }
                lines.add(new String(line.getBytes("8859_1"), encoding));
            }

            if(fileType.equals(FileType.CSV)){
                lines = transfer(lines);
            }

            result.put("lines", lines);
            result.put("pos", reader.getFilePointer());
            reader.close();
        } catch (Exception e) {
            logger.error("", e);
        }

        return result;
    }

    // used when csv
    private static List<String> transfer(List<String> lines){
        List<String> ret = new ArrayList<>();
        for(String line:lines){
            String[] valuesArr = line.split(",");
            JSONObject lineJson = new JSONObject(true);
            for(int j=0;j<valuesArr.length;j++) {
                lineJson.put(fields[j], valuesArr[j]);
            }
            ret.add(lineJson.toJSONString());
        }
        lines.clear();

        return ret;
    }

}
