package com.haizhi.graph.tag.analytics.util;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.engine.base.rule.logic.LogicExpress;
import com.haizhi.graph.engine.base.rule.script.RuleScript;
import com.haizhi.graph.tag.analytics.bean.TagRule;
import com.haizhi.graph.tag.analytics.model.TagReq;
import com.haizhi.graph.tag.core.dao.TagParameterDAO;
import com.haizhi.graph.tag.core.domain.TagParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/7/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag")
public class TagRuleParserTest {

    @Autowired
    TagParameterDAO tagParameterDAO;

    @Test
    public void getRuleScript() {
        TagReq params = FileUtils.readJSONObject("tag_insert.json", TagReq.class);
        String originalRule = JSON.toJSONString(params.getOriginalRule());
        String rule = LogicExpress.toExpression(originalRule);
        TagRule tagRule = TagRuleParser.parseRuleExpression(rule);

        String graph = "crm_dev2";
        Map<Long, TagParameter> parameterMap = tagParameterDAO.getTagParameters(graph,
                tagRule.getLogicParamIds()).stream()
                .collect(Collectors.toMap(TagParameter::getId, Function.identity()));

        RuleScript ruleScript = TagRuleParser.getRuleScript(parameterMap, tagRule);
        JSONUtils.println(ruleScript);
    }
}
