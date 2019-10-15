package com.haizhi.graph.tag.analytics.engine.evaluator;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.tag.analytics.bean.TagResult;
import com.haizhi.graph.tag.analytics.bean.TagSourceData;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.domain.DataType;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/22.
 */
public class TagEvaluatorTest {

    @Test
    public void execute(){
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

        TagSourceData data = new TagSourceData();
        data.setObjectKey("111");
        Map<String, Object> row = new HashMap<>();
        row.put("object_key", "111");
        row.put("capital", 200000000);
        data.addTableRow("Company", row);

        TagResult result = TagEvaluator.execute(tagInfo, data);
        List<TagResult> list = Lists.newArrayList(result);
        List<Map<String, Object>> listObject = JSONUtils.toListMap(list);
        Map<String, Object> map = JSONUtils.toMap(result);
        System.out.println(JSON.toJSONString(map, true));
        System.out.println(JSON.toJSONString(listObject, true));
    }
}
