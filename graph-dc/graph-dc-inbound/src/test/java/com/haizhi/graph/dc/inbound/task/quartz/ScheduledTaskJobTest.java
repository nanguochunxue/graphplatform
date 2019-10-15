package com.haizhi.graph.dc.inbound.task.quartz;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/03/04
 */
public class ScheduledTaskJobTest {

    public static final String SOURCE_1 = "hdfs://hadoop01.sz.haizhi.com:8020/user/graph/task_473011d3-122d-42db-8104-a9ca0a2d8c1d.json";

    @Test
    public void test(){
        HDFSHelper helper = new HDFSHelper();
        String line;
        while (
                (line = helper.readLine("/user/graph/task_473011d3-122d-42db-8104-a9ca0a2d8c1d.json")) != null
        ){
            System.out.println(line);
        }
    }

    @Test
    public void test2(){
        String spark = "spark://hadoop01.sz.haizhi.com:8032";

        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster(spark).setAppName("test");
        SparkContext sparkContext = new SparkContext(sparkConf);
        SparkSession sparkSession = new SparkSession(sparkContext);

        Dataset<Row> rows = sparkSession.read().json("hdfs://hadoop01.sz.haizhi.com:8020/user/graph/task_d59d76d9-55b5-4c47-9894-64741604acef.json");
        rows.foreach(new ForeachFunction<Row>() {
            @Override
            public void call(Row row) throws Exception {
                System.out.println(row.mkString());
            }
        });
    }

    // {"object_key":"2","string_field":"test2","long_field":100001,"double_file": 1001.999,"date_fiele":"2019-02-10 14:46:56"}
    @Test
    public void generatoTestData() throws IOException {
        File file = new File("/Users/haizhi/Documents/data.json");

        if (file.exists()){
            throw new RuntimeException("file exists !");
        }
        file.createNewFile();

        try(FileOutputStream stream = new FileOutputStream(file)){
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < 15500; i ++ ){
                map.put("object_key", String.valueOf(RandomStringUtils.randomAlphabetic(8)));
                map.put("string_field", RandomStringUtils.randomAlphabetic(15));
                map.put("long_field", RandomUtils.nextLong(1000L, 10000000L));
                map.put("double_field", RandomUtils.nextDouble(10d, 2000d));
                map.put("date_field", "2019-01-10");
                map.put("ctime", "2019-01-12");
                stream.write(JSON.toJSONString(map).getBytes());
                stream.write('\n');
            }
        }
    }

    @Test
    public void generateCSVData() throws IOException {
        File file = new File("/Users/haizhi/Documents/data.csv");

        if (file.exists()){
            throw new RuntimeException("file exists !");
        }
        file.createNewFile();

        try(FileOutputStream stream = new FileOutputStream(file)){
            stream.write("object_key,string_field,long_field,double_field,date_field,ctime".getBytes());
            for (int i = 0; i < 15500; i ++ ){
                stream.write((String.valueOf(RandomStringUtils.randomAlphabetic(8)) + ","+RandomStringUtils.randomAlphabetic(15)
                + ","+RandomUtils.nextLong(1000L, 10000000L) + ","+RandomUtils.nextDouble(10d, 2000d) + ",2019-01-10" + ",2019-01-12").getBytes());
                stream.write('\n');
            }
        }
    }
}