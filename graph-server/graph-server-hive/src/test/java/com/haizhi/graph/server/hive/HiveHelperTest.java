package com.haizhi.graph.server.hive;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.List;

/**
 * Created by chengmo on 2018/4/16.
 */
public class HiveHelperTest {

    @Test
    public void exampleCDH(){
        doExample("application-haizhi.properties");
    }

    @Test
    public void exampleFI(){
        doExample("application-haizhi-fic80.properties");
    }

    private void doExample(String activeProfile){
        HiveHelper hiveHelper = new HiveHelper(activeProfile);
        hiveHelper.execute("CREATE DATABASE IF NOT EXISTS test");
        hiveHelper.execute("USE test");
        hiveHelper.execute("CREATE TABLE test_table(id INT, name STRING)");
        List<HiveTable> tables = hiveHelper.describeTables("test");
        System.out.println(JSON.toJSONString(tables, true));
        hiveHelper.execute("DROP TABLE test_table");
    }
}
