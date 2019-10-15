package com.haizhi.graph.server.tiger.util;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.SchemaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/6.
 */
public class SchemaTypeTest {
    public static void main(String[] args) {
        SchemaType type = SchemaType.VERTEX;
        String database = "work_graph";
        String table = "company";
        String graphSql = String.format("USE GRAPH %s\nDROP %s %s",database, type, table);
        System.out.println(graphSql);

        Map<String,FieldType> fields = new HashMap<>();
        fields.put("age", FieldType.LONG);
        fields.put("name", FieldType.STRING);
        fields.put("sex", FieldType.STRING);
        fields.put("date", FieldType.DATETIME);

        System.out.println(fields.toString());
    }
}
