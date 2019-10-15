package com.haizhi.graph.server.es.index.mapping;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by chengmo on 2017/12/28.
 */
public class TypeMappingTest {

    @Test
    public void builder() throws IOException {
        TypeMapping mapping = new TypeMapping("test_es", "test_es_person");
        mapping.addField("name", DataType.STRING);
        mapping.addField("age", DataType.LONG);
        mapping.addField("address", DataType.STRING);

        TypeMapping.Field objectField = new TypeMapping.Field("attrs", DataType.OBJECT);
        objectField.addProperty("attrName", DataType.STRING);
        objectField.addProperty("attrValue", DataType.STRING);
        mapping.addField(objectField);

        String mappingSource = mapping.builder().string();
        System.out.println(JSON.toJSONString(JSON.parse(mappingSource), true));

    }
}
