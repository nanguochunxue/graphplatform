package com.haizhi.graph.plugins.flume.source.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Created by HaiyangWork on 2018/12/18.
 */
public class BuildDataTest {
    private static final GLog LOG = LogFactory.getLogger(BuildDataTest.class);

    private enum FileType{
        JSON,
        String
    }

    public static void main(String[] args) {
        buildData();
    }

    @SuppressWarnings("all")
    public static void buildData() {
        String userdir = System.getProperty("user.dir");
        String rootDir = userdir + "/graph-plugins/graph-dc-inbound-flume/src/test/resources/data1/";
        JSONArray tables = new JSONArray();

        JSONObject table1 = new JSONObject();
        table1.put("name", "tv_invest");
        table1.put("field", "id,sub_conam,object_key,currency,funded_ratio,update_date");
        tables.add(table1);

        JSONObject table2 = new JSONObject();
        table2.put("name", "tv_user");
        table2.put("field", "id,cmb_flg,clt_cop_nam,clt_nbr,sen_cod");
        tables.add(table2);

        JSONObject table3 = new JSONObject();
        table3.put("name", "tv_address");
        table3.put("field", "id,street,type,address,update_date");
        tables.add(table3);

        LOG.info(JSONArray.toJSONString(tables,true));

        try {
            for (int h = 0; h < tables.size(); h++) {
                String currentDir = rootDir + tables.getJSONObject(h).getString("name");

                for(int i=0;i<3;i++){
                    String filename = "part-" + i + ".csv";
                    String path = currentDir + "/" + filename;
                    LOG.info("writing to file: {0}", path);
                    File file = new File(path);
                    file.delete();
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                    String field = tables.getJSONObject(i).getString("field");
                    bw.write(field + "\n");
                    for (int j=0; j<100;j++) {
                        bw.write(generateRecord(field, FileType.String) );
                        bw.write("\n");
                    }
                    bw.flush();
                    bw.close();
                }

                for(int i=0;i<3;i++){
                    String filename = "part-" + i;
                    String path = currentDir + "/" + filename;
                    LOG.info("writing to file: {0}", path);
                    File file = new File(path);
                    file.delete();
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                    String field = tables.getJSONObject(i).getString("field");
                    for (int j=0; j<100;j++) {
                        bw.write(generateRecord(field, FileType.JSON) );
                        bw.write("\n");
                    }
                    bw.flush();
                    bw.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String getRandomString(int length){
        String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        Random random=new Random();
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<length; ++i){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private static int getRandomInt(int size){
        return (int)(Math.random() * size);
    }


    private static String generateRecord(String field, FileType fileType){
        String ret = null;
        if(fileType == FileType.String) {
            StringBuilder sb = new StringBuilder();
            int fieldLength = field.split(",").length;
            for (int i = 0; i < fieldLength; i++) {
                if (i == 0) sb.append(getRandomInt(1000));
                else sb.append(getRandomString(5));
                if (i != fieldLength - 1) {
                    sb.append(",");
                }
            }
            ret = sb.toString();
        }else if(fileType == FileType.JSON){
            JSONObject record = new JSONObject(true);
            String[] fields = field.split(",");
            for(int i=0;i<fields.length;i++){
                if(i==0) record.put(fields[i], getRandomInt(1000));
                else record.put(fields[i], getRandomString(7));
            }

            ret = record.toJSONString();
        }

        return ret;
    }
}
