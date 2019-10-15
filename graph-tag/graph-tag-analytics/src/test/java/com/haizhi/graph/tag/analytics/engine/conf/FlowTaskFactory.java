package com.haizhi.graph.tag.analytics.engine.conf;

import com.haizhi.graph.common.constant.FieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/4.
 */
public class FlowTaskFactory {

    private static Map<String, FlowTask> TASKS = new HashMap<>();

    static {
        TASKS.put("crm_dev", getCRMDev());
    }

    public static FlowTask get(String graph){
        return TASKS.get(graph);
    }

    private static FlowTask getCRMDev(){
        String graph = "crm_dev";
        FlowTask task = new FlowTask("TAG.Full>stat.tag[]");
        task.setGraph(graph);
        task.setTagId(1);
        task.setAction(ActionType.SPARK_SQL);
        task.setKafkaTopic("tag-analytics-test");
        task.setLocationConfig("application-test.properties");

        /* stage1 */
        FlowTask.Stage stage1 = new FlowTask.Stage();
        stage1.setId("stage1:FirstStat");
        // input
        FlowTaskInput input = new FlowTaskInput();
        FlowTaskSchema schema = new FlowTaskSchema("Company");
        schema.setReduceKey("objects:object_key");
        schema.addField("objects:object_key", FieldType.STRING.name());
        schema.addField("objects:name", FieldType.STRING.name());
        schema.addField("objects:reg_amount", FieldType.DOUBLE.name());
        schema.addField("objects:reg_date", FieldType.DATETIME.name());
        input.setGraph(graph);
        input.addSchema(schema);
        input.setSql("select t.name AS name," +
                "date_format(t.reg_date,'yyyy-MM-dd') AS statTime," +
                "count(1)," +
                "min(t.reg_amount) " +
                "from Company t " +
                "where t.reg_amount > 500000000 " +
                "and t.reg_date > date_sub(current_date(), 365*10) " +
                "group by t.name,date_format(t.reg_date,'yyyy-MM-dd')");
        stage1.setInput(input);
        // output
        FlowTaskOutput output = new FlowTaskOutput();
        schema = new FlowTaskSchema("tag_value_daily");
        schema.addField("objects:tagId", FieldType.LONG.name());
        schema.addField("objects:objectKey", FieldType.STRING.name());
        schema.addField("objects:dataType", FieldType.STRING.name());
        schema.addField("objects:updateTime", FieldType.DATETIME.name());
        schema.addField("objects:value", FieldType.DATETIME.name());
        output.addSchema(schema);
        stage1.setOutput(output);
        task.addStage(stage1);

        /* stage2 */
        FlowTask.Stage stage2 = new FlowTask.Stage();
        stage2.setId("stage2:FirstStat");
        // input
        input = new FlowTaskInput();
        schema = new FlowTaskSchema("tag_value_daily");
        schema.setStartRow("000#1#2017-04-02");
        schema.setStopRow("999#1#2018-04-02~");
        schema.addField("objects:tagId", FieldType.LONG.name());
        schema.addField("objects:objectKey", FieldType.STRING.name());
        schema.addField("objects:dataType", FieldType.STRING.name());
        schema.addField("objects:statTime", FieldType.DATETIME.name());
        schema.addField("objects:value", FieldType.DATETIME.name());
        input.setGraph(graph);
        input.addSchema(schema);
        input.setSql("select t.objectKey," +
                "count(get_json_object(t.value,'$.count(1)'))," +
                "min(get_json_object(t.value,'$.min(reg_amount)')) " +
                "from tag_value_daily t group by t.objectKey");
        stage2.setInput(input);
        // output
        output = new FlowTaskOutput();
        schema = new FlowTaskSchema("tag_value");
        schema.addField("objects:tagId", FieldType.LONG.name());
        schema.addField("objects:objectKey", FieldType.STRING.name());
        schema.addField("objects:dataType", FieldType.STRING.name());
        schema.addField("objects:updateTime", FieldType.DATETIME.name());
        schema.addField("objects:value", FieldType.DATETIME.name());
        output.addSchema(schema);
        schema = new FlowTaskSchema();
        schema.setSourceType(DataSourceType.KAFKA);
        output.addSchema(schema);
        stage2.setOutput(output);
        task.addStage(stage2);
        return task;
    }
}
