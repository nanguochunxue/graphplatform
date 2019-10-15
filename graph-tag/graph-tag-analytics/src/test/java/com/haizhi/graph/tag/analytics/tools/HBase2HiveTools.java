package com.haizhi.graph.tag.analytics.tools;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.bean.SchemaField;
import com.haizhi.graph.dc.core.service.DcMetadataService;
import com.haizhi.graph.engine.flow.tools.hive.HiveHelper;
import com.haizhi.graph.tag.analytics.util.FieldTypeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class HBase2HiveTools {

    static final String GRAPH = "crm_dev2";

    @Autowired
    DcMetadataService dcMetadataService;

    @Test
    public void hBase2Hive(){
        Domain domain = dcMetadataService.getDomain(GRAPH);
        HiveHelper hiveHelper = new HiveHelper(Resource.getActiveProfile());

        List<String> sqlList = new ArrayList<>();
        sqlList.add("CREATE DATABASE IF NOT EXISTS " + GRAPH);
        sqlList.add("use " + GRAPH);
        for (Schema sch : domain.getSchemaMap().values()) {
            StringBuilder sb = new StringBuilder();
            String tableName = sch.getSchemaName();
            String hbaseTableName = GRAPH + ":" + tableName;
            sb.append("CREATE EXTERNAL TABLE IF NOT EXISTS ").append(tableName).append("(key string,");

            StringBuilder sbh = new StringBuilder();
            sbh.append(":key,");
            for (SchemaField sf : sch.getFieldMap().values()) {
                String field = sf.getField();
                String dataType = FieldTypeUtils.toHiveDataType(sf.getType());
                sb.append(field).append(" ").append(dataType).append(",");
                sbh.append("objects:").append(field).append(",");
            }
            sb.append("day string");
            sbh.append("objects:utime");
            //sb = sb.delete(sb.length() - 1, sb.length());
            //sbh = sbh.delete(sbh.length() - 1, sbh.length());
            sb.append(") \n");
            sb.append("STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' \n");
            sb.append("WITH SERDEPROPERTIES ('hbase.columns.mapping'=");
            sb.append("'").append(sbh.toString()).append("') \n");
            sb.append("TBLPROPERTIES('hbase.table.name'=");
            sb.append("'").append(hbaseTableName).append("',");
            sb.append("'hbase.mapred.output.outputtable'=");
            sb.append("'").append(hbaseTableName).append("')");
            sqlList.add(sb.toString());
        }
        System.out.println(sqlList.size());

        boolean success = hiveHelper.execute(sqlList);
        System.out.println(success);
    }

    @Test
    public void dropHiveDatabase(){
        HiveHelper hiveHelper = new HiveHelper(Resource.getActiveProfile());
        String sql = "DROP DATABASE IF EXISTS " + GRAPH + " CASCADE";
        boolean success = hiveHelper.execute(sql);
        System.out.println(success);
    }

    @Test
    public void executeQuery(){
        HiveHelper hiveHelper = new HiveHelper(Resource.getActiveProfile());
        //String sql = "select * from crm_dev.company t limit 1";
        String sql = "select * from crm_dev.tag_value t order by t.updatetime desc limit 5";
        List<Map<String, Object>> resultList = hiveHelper.executeQuery(sql);
        System.out.println(JSON.toJSONString(resultList, true));
    }
}
