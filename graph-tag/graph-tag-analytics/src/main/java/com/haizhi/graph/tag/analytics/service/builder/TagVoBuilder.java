package com.haizhi.graph.tag.analytics.service.builder;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.engine.base.rule.logic.LogicExpress;
import com.haizhi.graph.engine.base.rule.logic.LogicRule;
import com.haizhi.graph.tag.analytics.bean.TagRule;
import com.haizhi.graph.tag.analytics.model.TagVo;
import com.haizhi.graph.tag.core.domain.Tag;
import com.haizhi.graph.tag.core.domain.TagParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by chengmo on 2018/5/4.
 */
public class TagVoBuilder {

    public static TagVo create(Tag tag) {
        TagVo tagVo = new TagVo();
        tagVo.setTagId(tag.getId());
        tagVo.setGraph(tag.getGraph());
        tagVo.setTagCategoryId(tag.getTagCategoryId());
        tagVo.setTagName(tag.getTagName());
        tagVo.setTagDesc(tag.getTagDesc());
        tagVo.setTagGroup(tag.getTagGroup());
        tagVo.setTagStatus(tag.getTagStatus());
        tagVo.setUpTime(tag.getUpTime());
        tagVo.setApplyUpTime(tag.getApplyUpTime());
        tagVo.setApplyBy(tag.getApplyBy());
        tagVo.setApproveBy(tag.getApproveBy());
        tagVo.setWeight(tag.getWeight());
        tagVo.setDataType(tag.getDataType());
        tagVo.setRule(tag.getRule());
        tagVo.setCreatedDt(tag.getCreatedDt());
        tagVo.setUpdatedDt(tag.getUpdatedDt());
        // rule -> originalRule
        if (StringUtils.isBlank(tag.getOriginalRule())) {
            if (StringUtils.isNotBlank(tag.getRule())) {
                LogicRule rule = LogicExpress.toLogicRule(tag.getRule());
                tagVo.setOriginalRule(rule);
            }
        } else {
            tagVo.setOriginalRule(JSON.parseObject(tag.getOriginalRule(), LogicRule.class));
        }
        return tagVo;
    }

    public static void addLogicParameters(Map<Long, TagParameter> map, TagRule tagRule, TagVo tagVo) {
        if (tagRule.isEmptyParams()) {
            return;
        }
        for (Map.Entry<String, Long> entry : tagRule.getLogicParams().entrySet()) {
            String expr = entry.getKey();
            long paramId = entry.getValue();
            TagParameter tp = map.get(paramId);
            if (tp == null){
                continue;
            }
            TagVo.Parameter param = new TagVo.Parameter();
            param.setId(paramId);
            param.setType(tp.getType());
            param.setName(tp.getName());
            param.setReference(tp.getReference());
            param.setValue(tp.getValue());
            tagVo.addParameter(expr, param);
        }
    }
}
