package com.haizhi.graph.tag.analytics.engine.evaluator;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.base.rule.BasicRule;
import com.haizhi.graph.engine.base.rule.Rule;
import com.haizhi.graph.engine.base.rule.script.RuleScript;
import com.haizhi.graph.engine.base.rule.script.ScriptContext;
import com.haizhi.graph.engine.base.rule.script.ScriptParams;
import com.haizhi.graph.tag.analytics.bean.TableRow;
import com.haizhi.graph.tag.analytics.bean.TagResult;
import com.haizhi.graph.tag.analytics.bean.TagSourceData;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.domain.DataType;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/1.
 */
public class TagEvaluator {

    private static final GLog LOG = LogFactory.getLogger(TagEvaluator.class);

    public static TagResult execute(TagInfo tagInfo, TagSourceData sourceData){
        TagResult tagResult = new TagResult();
        try {
            Rule rule = new BasicRule();
            RuleScript ruleScript = rule.script();
            ruleScript.setScript(tagInfo.getRuleScript());

            // params
            ScriptContext context = ruleScript.getContext();
            ScriptParams params = ruleScript.getParams();
            boolean flag = true;
            for (ScriptContext.Metadata meta : context.getMetadataMap().values()) {
                String tableName = meta.getName();
                if (!sourceData.hasTableRow(tableName)){
                    LOG.warn("Table row[{0}] missing", tableName);
                    flag = false;
                    break;
                }

                TableRow td = sourceData.getTableRow(tableName);
                Map<String, Object> row = new HashMap<>();
                for (String field : meta.getFields()) {
                    row.put(field, td.getFieldValue(field));
                }
                // tableName = tag_value.tagId
                if (tableName.startsWith(TagValue._schema + ".")){
                    tableName = StringUtils.substringAfter(tableName, ".");
                    params.put(tableName, row);
                    continue;
                }
                params.put(tableName, row);
            }

            if (!flag){
                tagResult.setValue(Rule.INVALID_RESULT);
                return tagResult;
            }

            // evaluate
            tagResult.setValue(rule.execute());

            // returns
            tagResult.setTagId(tagInfo.getTagId());
            tagResult.setObjectKey(sourceData.getObjectKey());
            tagResult.setDataType(tagInfo.getDataType());
            tagResult.setUpdateTime(new Date());
        } catch (Exception e) {
            LOG.error(e);
        }
        return tagResult;
    }

    public static void main(String[] args) {
        TagResult tr = new TagResult();
        tr.setTagId(1);
        tr.setObjectKey("fffffff");
        tr.setDataType(DataType.BOOLEAN);
        tr.setUpdateTime(new Date());

        String json = JSON.toJSONString(tr);
        System.out.println(json);
        TagResult result = JSON.parseObject(json, TagResult.class);
        System.out.println(result.getDataType());
        System.out.println(result.getUpdateTime());
    }
}
