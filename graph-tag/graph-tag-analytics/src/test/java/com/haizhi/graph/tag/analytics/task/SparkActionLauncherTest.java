package com.haizhi.graph.tag.analytics.task;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.engine.flow.action.spark.SparkActionLauncher;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.engine.conf.*;
import com.haizhi.graph.tag.analytics.engine.driver.LogicSparkOnHBaseDriver;
import com.haizhi.graph.tag.analytics.util.Constants;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.bean.TagSchemaInfo;
import com.haizhi.graph.tag.core.domain.DataType;
import com.haizhi.graph.tag.core.domain.TagSchemaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/21.
 */
public class SparkActionLauncherTest {

    public static void main(String[] args) {
        // conf
        Map<String, String> conf = new HashMap<>();
        String resourcePath = Resource.getPath();
        String jarPath = Resource.getJarPath();
        conf.put(FKeys.HADOOP_CONF_DIR, resourcePath);
        conf.put(FKeys.APP_RESOURCE, jarPath + Constants.JAR_NAME);
        conf.put(FKeys.MAIN_CLASS, LogicSparkOnHBaseDriver.class.getName());
        conf.put(FKeys.KAFKA_TOPIC, "tag-analytics-test-bd");
        conf.put(FKeys.KAFKA_LOCATION_CONFIG, "application-test.properties");
        System.out.println(JSON.toJSONString(conf, true));

        // TagDomain
        String graphName = "enterprise";
        TagDomain tagDomain = new TagDomain(graphName);
        TagInfo tagInfo = new TagInfo();
        tagInfo.setTagId(3);
        tagInfo.setDataType(DataType.NUMBER);
        tagInfo.setRuleScript("{\n" +
                "  \"source\": \"if (Company.capital < 10000000) {return 1} else {return 2}\",\n" +
                "  \"context\": {\n" +
                "    \"metadata\": {\n" +
                "      \"Company\": {\n" +
                "        \"name\": \"Company\",\n" +
                "        \"type\": \"ENTITY\",\n" +
                "        \"fields\": [\"object_key\",\"capital\"]\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");

        tagDomain.getTagInfoMap().put(tagInfo.getTagId(), tagInfo);

        TagSchemaInfo tsi = new TagSchemaInfo();
        tsi.setTagId(3);
        tsi.setGraph(graphName);
        tsi.setSchema("Company");
        tsi.setFields("object_key,capital");
        tsi.setType(TagSchemaType.VERTEX);
        tagDomain.getTagSchemaInfoList().add(tsi);
        System.out.println(JSON.toJSONString(tagDomain, true));

        HDFSHelper helper = new HDFSHelper();
        helper.upsertLine(Constants.PATH_TAG_DOMAIN, JSON.toJSONString(tagDomain));
        helper.close();

        // FlowTask
        FlowTask task = new FlowTask("tag-analytics-bd");
        task.setDebugEnabled(false);
        task.setAction(ActionType.SPARK);
        task.setGraph(graphName);

        /* stage1 */
        FlowTask.Stage stage1 = new FlowTask.Stage();
        stage1.setId("stage1:FirstStat");
        task.addStage(stage1);

        // input
        FlowTaskInput input = new FlowTaskInput();
        FlowTaskSchema schema = new FlowTaskSchema("Company");
        schema.setReduceKey("objects:object_key");
        schema.addField("objects:object_key", FieldType.STRING.name());
        schema.addField("properties:capital", FieldType.DOUBLE.name());
        input.addSchema(schema);
        stage1.setInput(input);

        FlowTaskOutput output = new FlowTaskOutput();
        schema = new FlowTaskSchema();
        schema.setSourceType(DataSourceType.KAFKA);
        output.addSchema(schema);
        stage1.setOutput(output);

        System.out.println(JSON.toJSONString(task, true));

        // SparkActionLauncher
        SparkActionLauncher launcher = new SparkActionLauncher(conf);
        boolean success = launcher.waitForCompletion();
        System.out.println(success);
    }
}
