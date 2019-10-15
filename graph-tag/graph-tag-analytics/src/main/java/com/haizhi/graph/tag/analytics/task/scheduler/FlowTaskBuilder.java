package com.haizhi.graph.tag.analytics.task.scheduler;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.bean.SchemaField;
import com.haizhi.graph.engine.flow.conf.FKeys;
import com.haizhi.graph.tag.analytics.bean.TagContext;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.analytics.bean.TagValueDaily;
import com.haizhi.graph.tag.analytics.engine.conf.*;
import com.haizhi.graph.tag.analytics.engine.driver.LogicSparkOnHBaseDriver;
import com.haizhi.graph.tag.analytics.engine.driver.LogicSparkOnHiveDriver;
import com.haizhi.graph.tag.analytics.engine.driver.StatSparkSqlOnHiveDriver;
import com.haizhi.graph.tag.analytics.util.Constants;
import com.haizhi.graph.tag.analytics.util.FieldTypeUtils;
import com.haizhi.graph.tag.analytics.util.SqlUtils;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.domain.AnalyticsMode;
import com.haizhi.graph.tag.core.domain.TagParameter;
import com.haizhi.graph.tag.core.domain.TagSchemaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by chengmo on 2018/4/9.
 */
public class FlowTaskBuilder {

    private static final GLog LOG = LogFactory.getLogger(FlowTaskBuilder.class);
    private static final String TASK_ID_FORMAT = "{0}=>{1}.{2}.tags[{3}]";

    /**
     * Build FlowTasks.
     *
     * @param ctx
     */
    public static void build(TagContext ctx) {
        for (Stage.Task task : ctx.getStageSet().getTasks().values()) {
            FlowTask flowTask = build(task, ctx);
            flowTask.setPartitions(ctx.getPartitions());
            buildProperties(task, flowTask);
            buildSecurity(ctx, flowTask);
            task.setFlowTask(flowTask);
        }
    }

    /**
     * @param task
     * @param ctx
     * @return
     */
    public static FlowTask build(Stage.Task task, TagContext ctx) {
        Set<Long> tagIds = task.getTagIds();
        int stageId = task.getStageId();
        switch (stageId) {
            case 1:
                if (tagIds.size() > 1) {
                    LOG.warn("[STAT]task tagId is not unique,{0}", tagIds);
                    break;
                }
                return createOnStat(task, ctx);
            case 2:
                return createOnLogic(task, ctx);
            case 3:
                return createOnDM(task, ctx);
            case 4:
                return createOnLogic(task, ctx);
        }
        return new FlowTask();
    }

    /**
     * @param graph
     * @return
     */
    public static String getTagValueDailyHiveTableScript(String graph) {
        return getHiveTableCreationScript(graph, getTagValueDailySchema());
    }

    /**
     * @param graph
     * @return
     */
    public static String getTagValueHiveTableScript(String graph) {
        return getHiveTableCreationScript(graph, getTagValueSchema());
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static void buildProperties(Stage.Task task, FlowTask flowTask) {
        String mainClass = LogicSparkOnHBaseDriver.class.getName();
        switch (task.getStageId()) {
            case 1:
                mainClass = StatSparkSqlOnHiveDriver.class.getName();
                break;
            case 2:
                mainClass = LogicSparkOnHiveDriver.class.getName();
                break;
            case 3:
                break;
            case 4:
                mainClass = LogicSparkOnHiveDriver.class.getName();
                break;
        }
        flowTask.putProperty(FKeys.MAIN_CLASS, mainClass);
        flowTask.putProperty(Constants.TAG_DOMAIN_KEY, Constants.PATH_TAG_DOMAIN);
    }

    private static void buildSecurity(TagContext ctx, FlowTask flowTask){
        flowTask.setSecurityEnabled(ctx.isSecurityEnabled());
        flowTask.setUserPrincipal(ctx.getUserPrincipal());
        flowTask.setUserConfPath(ctx.getUserConfPath());
    }

    private static FlowTask createOnDM(Stage.Task task, TagContext ctx) {
        TagDomain tagDomain = ctx.getTagDomain();
        Domain domain = ctx.getDomain();
        String graphName = tagDomain.getGraphName();
        String taskIdPrefix = ctx.getTaskIdPrefix();
        String kafkaTopic = ctx.getKafkaTopic();
        String kafkaLocation = ctx.getLocationConfig();
        boolean debugEnabled = ctx.isDebugEnabled();

        Set<Long> tagIds = task.getTagIds();

        FlowTask flowTask = new FlowTask();
        flowTask.setGraph(graphName);
        String mode = AnalyticsMode.DM.toString();
        flowTask.setId(getFlowTaskId(taskIdPrefix, mode, task));
        flowTask.getTagIds().addAll(tagIds);
        flowTask.setKafkaTopic(kafkaTopic);
        flowTask.setLocationConfig(kafkaLocation);
        flowTask.setDebugEnabled(debugEnabled);

        //TODO
        return flowTask;
    }

    private static FlowTask createOnLogic(Stage.Task task, TagContext ctx) {
        TagDomain tagDomain = ctx.getTagDomain();
        Domain domain = ctx.getDomain();
        String graphName = tagDomain.getGraphName();
        String taskIdPrefix = ctx.getTaskIdPrefix();
        String kafkaTopic = ctx.getKafkaTopic();
        String kafkaLocation = ctx.getLocationConfig();
        boolean debugEnabled = ctx.isDebugEnabled();

        Set<Long> tagIds = task.getTagIds();

        FlowTask flowTask = new FlowTask();
        flowTask.setGraph(graphName);
        String mode = AnalyticsMode.LOGIC.toString();
        flowTask.setId(getFlowTaskId(taskIdPrefix, mode, task));
        flowTask.getTagIds().addAll(tagIds);
        flowTask.setKafkaTopic(kafkaTopic);
        flowTask.setLocationConfig(kafkaLocation);
        flowTask.setDebugEnabled(debugEnabled);

        /* stage1 */
        FlowTask.Stage stage1 = new FlowTask.Stage();
        stage1.setId("stage1:logicEvaluate");
        // input
        FlowTaskInput input = new FlowTaskInput();
        Map<String, Set<String>> tagSchemas = tagDomain.getTagSchemaNames(tagIds);
        input.addSchemas(getFlowTaskSchemas(tagSchemas, domain, tagDomain));
        input.setGraph(graphName);
        stage1.setInput(input);
        // output
        FlowTaskOutput output = new FlowTaskOutput();
        output.addSchema(getTagValueSchema());
        FlowTaskSchema fts = new FlowTaskSchema();
        fts.setSourceType(DataSourceType.KAFKA);
        output.addSchema(fts);
        stage1.setOutput(output);
        flowTask.addStage(stage1);
        return flowTask;
    }

    private static FlowTask createOnStat(Stage.Task task, TagContext ctx) {
        TagDomain tagDomain = ctx.getTagDomain();
        Domain domain = ctx.getDomain();
        String graphName = tagDomain.getGraphName();
        String taskIdPrefix = ctx.getTaskIdPrefix();
        String kafkaTopic = ctx.getKafkaTopic();
        String kafkaLocation = ctx.getLocationConfig();
        boolean debugEnabled = ctx.isDebugEnabled();

        Set<Long> tagIds = task.getTagIds();
        long tagId = tagIds.iterator().next();

        FlowTask flowTask = new FlowTask();
        flowTask.setGraph(graphName);
        String mode = AnalyticsMode.STAT.toString();
        flowTask.setId(getFlowTaskId(taskIdPrefix, mode, task));
        flowTask.getTagIds().add(tagId);
        flowTask.setKafkaTopic(kafkaTopic);
        flowTask.setLocationConfig(kafkaLocation);
        flowTask.setDebugEnabled(debugEnabled);

        /* stage1 */
        FlowTask.Stage stage1 = new FlowTask.Stage();
        stage1.setId("stage1:firstStat");
        // input
        FlowTaskInput input = new FlowTaskInput();
        Map<String, Set<String>> tagSchemas = tagDomain.getTagSchemaNames(tagIds);
        input.addSchemas(getFlowTaskSchemas(tagSchemas, domain, tagDomain));
        input.setGraph(graphName);
        TagInfo tagInfo = tagDomain.getTagInfo(tagId);
        String ruleScript = tagInfo.getRuleScript();
        String sql = JSONObject.parseObject(ruleScript).getString("source");
        input.setSql(sql);
        stage1.setInput(input);
        // output
        FlowTaskOutput output = new FlowTaskOutput();
        output.addSchema(getTagValueDailySchema());
        stage1.setOutput(output);
        flowTask.addStage(stage1);

        /* stage2 */
        FlowTask.Stage stage2 = new FlowTask.Stage();
        stage2.setId("stage2:secondStat");
        // input
        input = new FlowTaskInput();
        FlowTaskSchema schema = getTagValueDailySchema();
        schema.setStartRow("");
        schema.setStopRow("");
        input.addSchema(schema);
        input.setGraph(graphName);
        input.setSql(getSecondStatSQL(sql, tagId));
        stage2.setInput(input);
        // output
        output = new FlowTaskOutput();
        output.addSchema(getTagValueSchema());
        schema = new FlowTaskSchema();
        schema.setSourceType(DataSourceType.KAFKA);
        output.addSchema(schema);
        stage2.setOutput(output);
        flowTask.addStage(stage2);
        return flowTask;
    }

    private static String getFlowTaskId(String taskIdPrefix, String mode, Stage.Task task) {
        String tagIdStr = task.getTagIdsString();
        String taskId = task.getTaskId();
        return MessageFormat.format(TASK_ID_FORMAT, taskIdPrefix, mode, taskId, tagIdStr);
    }

    private static String getSecondStatSQL(String sql, long tagId) {
        StringBuilder sb = new StringBuilder();
        sb.append("select t.objectKey,");
        Set<String> projections = SqlUtils.getProjections(sql);
        for (String projection : projections) {
            String func = StringUtils.substringBefore(projection, "(");
            if (!SqlUtils.isAggFunction(func)) {
                continue;
            }
            if ("count".equals(func.toLowerCase())) {
                func = "sum";
            }
            sb.append(func);
            sb.append("(get_json_object(t.value,'$.");
            sb.append(projection);
            sb.append("')),");
        }
        sb = sb.delete(sb.length() - 1, sb.length());
        sb.append(" from tag_value_daily t");
        sb.append(" where t.tagId=").append(tagId);
        sb.append(" group by t.objectKey");
        return sb.toString();
    }

    private static FlowTaskSchema getTagValueDailySchema() {
        FlowTaskSchema schema = new FlowTaskSchema(TagValueDaily._schema);
        schema.addField(TagValueDaily.tagId, FieldType.STRING.name());
        schema.addField(TagValueDaily.objectKey, FieldType.STRING.name());
        schema.addField(TagValueDaily.dataType, FieldType.DATETIME.name());
        schema.addField(TagValueDaily.statTime, FieldType.DATETIME.name());
        schema.addField(TagValueDaily.value, FieldType.STRING.name());
        schema.setReduceKey(TagValueDaily.objectKey);
        return schema;
    }

    private static FlowTaskSchema getTagValueSchema() {
        FlowTaskSchema schema = new FlowTaskSchema(TagValue._schema);
        schema.addField(TagValue.tagId, FieldType.STRING.name());
        schema.addField(TagValue.objectKey, FieldType.STRING.name());
        schema.addField(TagValue.dataType, FieldType.DATETIME.name());
        schema.addField(TagValue.updateTime, FieldType.DATETIME.name());
        schema.addField(TagValue.value, FieldType.STRING.name());
        schema.setReduceKey(TagValue.objectKey);
        schema.setSchemaType(TagSchemaType.TAG.name());
        return schema;
    }

    private static List<FlowTaskSchema> getFlowTaskSchemas(Map<String, Set<String>> tagSchemas, Domain domain,
                                                           TagDomain tagDomain) {
        List<FlowTaskSchema> results = new ArrayList<>();
        boolean flag = false;
        for (Map.Entry<String, Set<String>> entry : tagSchemas.entrySet()) {
            String schemaName = entry.getKey();
            if (schemaName.startsWith(TagValue._schema + ".")) {
                if (!flag) {
                    results.add(getTagValueSchema());
                    flag = true;
                }
                continue;
            }

            // schemaName=SQL_40200201
            if (schemaName.toUpperCase().startsWith(TagSchemaType.SQL.name() + "_")) {
                FlowTaskSchema fts = getFlowTaskSchemaBySQL(schemaName, domain, tagDomain);
                results.add(fts);
                continue;
            }

            Schema sch = domain.getSchema(schemaName.toLowerCase());
            if (sch == null) {
                LOG.error("Schema[{0}] does not exists.", schemaName);
                continue;
            }

            // input.schema
            FlowTaskSchema fts = new FlowTaskSchema(schemaName);
            if (SchemaType.isVertex(sch.getType())){
                fts.setSchemaType(TagSchemaType.VERTEX.name());
            } else {
                fts.setSchemaType(TagSchemaType.EDGE.name());
            }

            // input.schema.reduceKey
            if (sch.getFieldMap().containsKey(TagValue.COMPANY_KEY)){
                fts.setReduceKey(TagValue.COMPANY_KEY);
            } else {
                fts.setReduceKey(Keys.OBJECT_KEY);
            }

            // input.schema.fields
            for (String field : entry.getValue()) {
                SchemaField sf = sch.getField(field);
                if (sf == null) {
                    LOG.error("SchemaField[{0}] does not exists.", field);
                    continue;
                }
                fts.addField(field, sf.getType().toString());
            }
            results.add(fts);
        }
        return results;
    }

    private static FlowTaskSchema getFlowTaskSchemaBySQL(String schemaName, Domain domain, TagDomain tagDomain) {
        Long paramId = NumberUtils.toLong(StringUtils.substringAfter(schemaName, "_"));
        TagParameter tp = tagDomain.getTagParameter(paramId);
        String sql = tp.getReference();

        FlowTaskSchema fts = new FlowTaskSchema(schemaName);
        fts.setSchemaType(TagSchemaType.SQL.name());
        fts.setSql(sql);
        fts.setReduceKey(Keys.OBJECT_KEY);

        Map<String, Set<String>> map = SqlUtils.getTableAndFields(sql);
        Set<String> projections = SqlUtils.getProjections(sql);
        // example: select count(1) from table t;
        if (projections.size() == 1){
            String field = projections.iterator().next();
            String func = StringUtils.substringBefore(field, "(");
            if (SqlUtils.isAggFunction(func)){
                String fieldType = FieldType.DOUBLE.name();
                switch (func.toLowerCase()){
                    case "count":
                        fieldType = FieldType.LONG.name();
                        break;
                }
                fts.addField(field, fieldType);
                fts.setReduceKey(func);
                return fts;
            }
        }

        Map<String, String> field2Table = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            String table = entry.getKey();
            for (String field : entry.getValue()) {
                field2Table.put(field, table);
            }
        }

        for (String projection : projections) {
            String table = field2Table.get(projection);
            if (table == null){
                switch (projection.toLowerCase()){
                    case "rank":
                        fts.addField(projection, FieldType.LONG.name());
                        break;
                    default:
                        fts.addField(projection, FieldType.STRING.name());
                        break;
                }
                continue;
            }
            Schema sch = domain.getSchema(table.toLowerCase());
            if (sch == null) {
                LOG.error("Schema[{0}] does not exists.", table);
                continue;
            }
            // input.schema.reduceKey
            if (sch.getFieldMap().containsKey(TagValue.COMPANY_KEY)){
                fts.setReduceKey(TagValue.COMPANY_KEY);
            } else {
                fts.setReduceKey(Keys.OBJECT_KEY);
            }
            SchemaField sf = sch.getField(projection);
            if (sf == null) {
                LOG.error("SchemaField[{0}] does not exists.", projection);
                continue;
            }
            fts.addField(projection, sf.getType().toString());
        }
        return fts;
    }

    private static String getHiveTableCreationScript(String graph, FlowTaskSchema sch) {
        StringBuilder sb = new StringBuilder();
        String tableName = sch.getSchema();
        String hbaseTableName = graph + ":" + tableName;
        sb.append("CREATE EXTERNAL TABLE IF NOT EXISTS ").append(tableName).append("(key string,");

        StringBuilder sbh = new StringBuilder();
        sbh.append(":key,");
        for (FlowTaskSchema.Field sf : sch.getFields().values()) {
            String field = sf.getField();
            String dataType = FieldTypeUtils.toHiveDataType(sf.getDataType());
            sb.append(field).append(" ").append(dataType).append(",");
            sbh.append("objects:").append(field).append(",");
        }
        sb = sb.delete(sb.length() - 1, sb.length());
        sbh = sbh.delete(sbh.length() - 1, sbh.length());
        sb.append(") \n");
        sb.append("STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' \n");
        sb.append("WITH SERDEPROPERTIES ('hbase.columns.mapping'=");
        sb.append("'").append(sbh.toString()).append("') \n");
        sb.append("TBLPROPERTIES('hbase.table.name'=");
        sb.append("'").append(hbaseTableName).append("',");
        sb.append("'hbase.mapred.output.outputtable'=");
        sb.append("'").append(hbaseTableName).append("')");
        return sb.toString();
    }
}
