package com.haizhi.graph.tag.analytics.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.engine.base.rule.script.QLRuleScriptImpl;
import com.haizhi.graph.engine.base.rule.script.RuleScript;
import com.haizhi.graph.engine.base.rule.script.ScriptContext;
import com.haizhi.graph.tag.analytics.bean.TagRule;
import com.haizhi.graph.tag.analytics.bean.TagValue;
import com.haizhi.graph.tag.core.domain.AnalyticsMode;
import com.haizhi.graph.tag.core.domain.TagParameter;
import com.haizhi.graph.tag.core.domain.TagSchemaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2018/5/4.
 */
public class TagRuleParser {

    private static final Pattern LogicParamPattern = Pattern.compile("@((field)|(tag)|(sql))\\([A-Za-z0-9_.$]+\\)");

    public static TagRule parseRuleExpression(String ruleExpr) {
        TagRule tagRule = new TagRule(ruleExpr);
        Matcher matcher = LogicParamPattern.matcher(ruleExpr);
        while (matcher.find()) {
            String expr = matcher.group();
            String str = StringUtils.substringBetween(expr, "(", ")");
            if (str.contains(".")) {
                str = StringUtils.substringBefore(str, ".");
            }
            long paramId = NumberUtils.toLong(str, 0);
            if (paramId == 0) {
                continue;
            }
            tagRule.addLogicParam(expr, paramId);
            tagRule.addLogicParamId(paramId);
        }
        tagRule.setAnalyticsMode(recognizeAnalyticsMode(ruleExpr));
        return tagRule;
    }

    public static RuleScript getRuleScript(Map<Long, TagParameter> parameters, TagRule tagRule) {
        RuleScript script = new QLRuleScriptImpl();
        String source = tagRule.getRuleExpression();
        if (tagRule.isEmptyParams() || parameters.isEmpty()) {
            script.setSource(rebuildSource(source));
            return script;
        }
        Map<String, Set<String>> schemaToFields = new HashMap<>();
        for (Map.Entry<String, Long> entry : tagRule.getLogicParams().entrySet()) {
            String expr = entry.getKey();
            long paramId = entry.getValue();
            TagParameter tp = parameters.get(paramId);
            String reference = tp.getReference();
            Set<String> fields;
            switch (tp.getType()) {
                case FIELD:
                    // reference = Company.reg_amount
                    source = source.replace(expr, reference);
                    String schema = StringUtils.substringBefore(reference, ".");
                    String field = StringUtils.substringAfter(reference, ".");
                    fields = schemaToFields.get(schema);
                    if (fields == null) {
                        fields = new HashSet<>();
                        schemaToFields.put(schema, fields);
                    }
                    fields.add(field);
                    break;
                case TAG:
                    String str = StringUtils.substringBetween(expr, "(", ")");
                    str = TagUtils.appendTagIdPrefix(str);
                    source = source.replace(expr, str);
                    // reference = tag_value.tag_id_3
                    fields = schemaToFields.get(reference);
                    if (fields == null) {
                        fields = new HashSet<>();
                        schemaToFields.put(reference, fields);
                    }
                    break;
                case SQL:
                    String sqlStr = StringUtils.substringBetween(expr, "(", ")");
                    sqlStr = "SQL_" + sqlStr;
                    source = source.replace(expr, sqlStr);
                    String sqlSchema = StringUtils.substringBefore(sqlStr, ".");
                    String sqlField = StringUtils.substringAfter(sqlStr, ".");
                    fields = schemaToFields.get(sqlSchema);
                    if (fields == null) {
                        fields = new HashSet<>();
                        schemaToFields.put(sqlSchema, fields);
                    }
                    fields.add(sqlField);
                    break;
                case ALG:
                case RULE:
                    break;
            }
        }
        // source
        script.setSource(rebuildSource(source));

        // ScriptContext
        ScriptContext ctx = new ScriptContext();
        Map<String, ScriptContext.Metadata> metadataMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : schemaToFields.entrySet()) {
            String schema = entry.getKey();
            Set<String> fields = entry.getValue();
            ScriptContext.Metadata md = new ScriptContext.Metadata();
            md.setName(schema);
            // tag
            if (schema.toLowerCase().startsWith(TagValue._schema)) {
                md.setType(ScriptContext.Type.TAG);
                md.setFields(Lists.newArrayList(TagValue.tagId, TagValue.objectKey, TagValue.value));
            }
            // sql
            else if (schema.toUpperCase().startsWith(TagSchemaType.SQL.name())) {
                md.setType(ScriptContext.Type.SQL);
                md.setFields(Lists.newArrayList(fields));
            }
            // vertex
            else {
                md.setType(ScriptContext.Type.VERTEX);
                md.setFields(Lists.newArrayList(fields));
                md.getFields().add(Keys.OBJECT_KEY);
            }
            metadataMap.put(md.getName(), md);
        }
        ctx.setMetadataMap(metadataMap);
        script.setContext(ctx);
        tagRule.setRuleScript(script);
        return script;
    }

    private static String rebuildSource(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        if (source.trim().startsWith("if") &&
                source.contains("return") &&
                source.contains("else")) {
            return source;
        }
        source = MessageFormat.format("if ({0})", source);
        source += " {return 1} else {return 0}";
        return source;
    }

    public static AnalyticsMode recognizeAnalyticsMode(String ruleExpr) {
        AnalyticsMode mode = AnalyticsMode.LOGIC;
        ruleExpr = ruleExpr.trim().toLowerCase();
        // stat
        if (ruleExpr.startsWith("select")) {
            mode = AnalyticsMode.STAT;
        }
        // alg
        else if (ruleExpr.contains("@alg(")) {
            mode = AnalyticsMode.DM;
        }
        return mode;
    }

    public static void main(String[] args) {
        String rule = "@field(1) > 10000 and @field(2) in (央企，国企) " +
                "and @yearAgo(@field(3)) < 10 and @tag(4.value) and @tag(5.value.count$1$)";
        TagRule tagRule = parseRuleExpression(rule);
        System.out.println(JSON.toJSONString(tagRule, true));

        rule = "select * from table";
        tagRule = parseRuleExpression(rule);
        System.out.println(JSON.toJSONString(tagRule, true));

        rule = "@alg(1) > 1000";
        tagRule = parseRuleExpression(rule);
        System.out.println(JSON.toJSONString(tagRule, true));

        rule = "@alg(1) > 1000 return else";
        System.out.println(rebuildSource(rule));

        RuleScript script = new QLRuleScriptImpl();
        script.setSource("Company.field1 > 10000 and Company.field2");
        ScriptContext ctx = new ScriptContext();
        ScriptContext.Metadata md = new ScriptContext.Metadata();
        md.setType(ScriptContext.Type.VERTEX);
        md.setName("Company");
        md.setFields(Lists.newArrayList("field1", "field2"));

        Map<String, ScriptContext.Metadata> map = new HashMap<>();
        map.put(md.getName(), md);
        ctx.setMetadataMap(map);
        script.setContext(ctx);
        System.out.println(JSON.toJSONString(script, true));

        System.out.println(StringUtils.substringBetween("@tag(5.value.count$1$)", "(", ")"));
    }
}
