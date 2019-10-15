package com.haizhi.graph.api.temp;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.util.CodecUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by tanghaiyang on 2019/5/29.
 */
public class BuldDataTest {

    private static ArrayList<String> vertex_object_key = new ArrayList<>();
    private static ArrayList<String> edge_object_key = new ArrayList<>();
    private static ArrayList<String> arr_string_field = new ArrayList<>();
    private static ArrayList<Long> arr_long_field = new ArrayList<>();
    private static ArrayList<Double> arr_double_field = new ArrayList<>();
    private static ArrayList<String> arr_date_field = new ArrayList<>();

    private static int data_size = 50;

    @Test
    public void writeFile() throws Exception{
        String demo_vertex = "D:/CodeGraph/graph/graph-api/src/test/resources/data/demo_vertex.json";
        String demo_edge = "D:/CodeGraph/graph/graph-api/src/test/resources/data/demo_edge.json";

        init();

        BufferedWriter bwVertex = new BufferedWriter(new FileWriter(new File(demo_vertex), true));
        for(int i = 0; i<100; i++){
            bwVertex.write(buildVertex() + "\n");
        }
        bwVertex.flush();
        bwVertex.close();

        BufferedWriter bwEdge = new BufferedWriter(new FileWriter(new File(demo_edge), true));
        for(int i = 0; i<100; i++){
            bwEdge.write(buildEdge() + "\n");
        }
        bwEdge.flush();
        bwEdge.close();
    }

    public void init(){
        for(int i=0;i<data_size;i++){
            vertex_object_key.add(RandomStringUtils.randomAlphanumeric(32).toUpperCase());
            edge_object_key.add(RandomStringUtils.randomAlphanumeric(32).toUpperCase());
            arr_string_field.add(RandomStringUtils.randomAlphanumeric(10));
            arr_long_field.add(RandomUtils.nextLong());
            arr_double_field.add(RandomUtils.nextDouble());
            Random random = new Random();
            int randomTime = random.nextInt();
            Date date = new Date(randomTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            arr_date_field.add(sdf.format(date));
        }
    }

    /*
    * {"long_field":4915038,"object_key":"bvroTEQU","ctime":"2019-01-12","double_field":1971.7923571105118,"string_field":"TLNWNAXbgGuSpfk","date_field":"2019-01-10"}
    *
    STRING("字符串"),
    LONG("整数"),
    DOUBLE("浮点数"),
    DATETIME("日期");
    * */
    private String buildRecord(int i){
        JSONObject record = new JSONObject();
        record.put("long_field", DataUtil.getRandomInt(4));
        record.put("object_key", DataUtil.getRandomString(8));
        record.put("double_field", DataUtil.getRandomDouble(3));

        Random random = new Random();
        int randomTime = random.nextInt();
        Date date = new Date(randomTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        record.put("date_field", DataUtil.getRandomDate());
        return record.toJSONString();
    }

    private String buildRecord2(int i){
        JSONObject record = new JSONObject();
        record.put("object_key", CodecUtils.md5(UUID.randomUUID().toString()));
        record.put("demo_long_field", DataUtil.getRandomInt(4));
        record.put("demo_double_field", DataUtil.getRandomDouble(3));
        record.put("demo_date_field", DataUtil.getRandomDate());
        record.put("demo_string_field", DataUtil.getRandomString(5));
        return record.toJSONString();
    }

    private String buildVertex(){
        JSONObject record = new JSONObject();
        record.put("object_key", vertex_object_key.get(RandomUtils.nextInt(data_size)));
        record.put("demo_long_field", arr_long_field.get(RandomUtils.nextInt(data_size)));
        record.put("demo_double_field", arr_double_field.get(RandomUtils.nextInt(data_size)));
        record.put("demo_date_field", arr_date_field.get(RandomUtils.nextInt(data_size)));
        record.put("demo_string_field", arr_string_field.get(RandomUtils.nextInt(data_size)));
        return record.toJSONString();
    }

    private String buildEdge(){
        JSONObject record = new JSONObject();
        record.put("object_key", edge_object_key.get(RandomUtils.nextInt(data_size)));
        record.put("from_key", "demo_vertex/"+vertex_object_key.get(RandomUtils.nextInt(data_size)));
        record.put("to_key", "demo_vertex/"+vertex_object_key.get(RandomUtils.nextInt(data_size)));
        record.put("demo_long_field", arr_long_field.get(RandomUtils.nextInt(data_size)));
        record.put("demo_double_field", arr_double_field.get(RandomUtils.nextInt(data_size)));
        record.put("demo_date_field", arr_date_field.get(RandomUtils.nextInt(data_size)));
        record.put("demo_string_field", arr_string_field.get(RandomUtils.nextInt(data_size)));
        return record.toJSONString();
    }

    @Test
    public void test(){
        System.out.println(RandomUtils.nextInt(10));
        System.out.println(RandomStringUtils.randomAlphanumeric(3).toUpperCase());
    }
}
