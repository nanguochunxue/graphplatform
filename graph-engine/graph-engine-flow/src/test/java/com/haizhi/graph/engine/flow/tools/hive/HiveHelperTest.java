package com.haizhi.graph.engine.flow.tools.hive;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.List;

/**
 * Created by chengmo on 2018/4/16.
 */
public class HiveHelperTest {

    @Test
    public void describeTables(){
        String activeProfile = "application-tag-hdp.properties";
        HiveHelper hiveHelper = new HiveHelper(activeProfile);
        List<HiveTable> tables = hiveHelper.describeTables("");
        System.out.println(JSON.toJSONString(tables, true));

        hiveHelper.execute("CREATE DATABASE IF NOT EXISTS test");
        hiveHelper.execute("USE test");
        hiveHelper.execute("CREATE TABLE test_table(id INT, name STRING)");
        hiveHelper.execute("DROP TABLE test_table");
    }
}
