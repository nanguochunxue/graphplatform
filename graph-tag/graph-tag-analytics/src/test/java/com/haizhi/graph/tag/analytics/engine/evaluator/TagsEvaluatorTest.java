package com.haizhi.graph.tag.analytics.engine.evaluator;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import com.haizhi.graph.tag.analytics.bean.TagResult;
import com.haizhi.graph.tag.analytics.bean.TagSourceData;
import com.haizhi.graph.tag.analytics.engine.conf.FlowTask;
import com.haizhi.graph.tag.analytics.util.Constants;
import com.haizhi.graph.tag.core.bean.TagDomain;
import com.haizhi.graph.tag.core.bean.TagInfo;
import com.haizhi.graph.tag.core.bean.TagSchemaInfo;
import com.haizhi.graph.tag.core.domain.DataType;
import com.haizhi.graph.tag.core.domain.TagSchemaType;
import com.haizhi.graph.tag.core.service.TagMetadataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag")
public class TagsEvaluatorTest {

    static final String GRAPH = "crm_dev2";

    @Autowired
    TagMetadataService tagService;
    TagsEvaluator evaluator = new TagsEvaluator();

    @Test
    public void execute1(){
        TagDomain tagDomain = tagService.getTagDomain(GRAPH);
        HDFSHelper helper = new HDFSHelper();
        helper.upsertLine(Constants.PATH_TAG_DOMAIN, JSON.toJSONString(tagDomain));
        helper.close();

        TagsEvaluator handler = new TagsEvaluator();

        FlowTask task = new FlowTask();
        task.setGraph(GRAPH);
        task.getTagIds().add(405001L);
        String key = "0286ed071e9f16c216ec62d064757db3";
        String value = "crm_dev2.SQL_40500152\u0002{\"company_key\":\"0286ed071e9f16c216ec62d064757db3\"," +
                "\"deposit_balance\":\"100\"}\u0003crm_dev2.SQL_40500151\u0002{\"count$1$\":6.0}\u0003";
        Object result = handler.execute(key, value, task);
        System.out.println(JSON.toJSONString(result, true));
    }

    @Test
    public void execute(){
        this.initTagDomain();
        TagsEvaluator handler = new TagsEvaluator();

        FlowTask task = new FlowTask();
        task.setGraph(GRAPH);
        String key = "EE430ABF242E4E1F5EDDA0B82D02C8A0";
        String value = "enterprise:Company\u0002{\"capital\":50000000.0," +
                "\"object_key\":\"EE430ABF242E4E1F5EDDA0B82D02C8A0\"}";
        Object result = handler.execute(key, value, task);
        System.out.println(JSON.toJSONString(result, true));
    }

    @Test
    public void doExecute(){
        TagSourceData data = new TagSourceData();
        data.setObjectKey("111");
        Map<String, Object> row = new HashMap<>();
        row.put("reg_amount", 1500000000);
        data.addTableRow("Company", row);

        TagDomain tagDomain = tagService.getTagDomain("crm_dev");
        List<TagResult> results = evaluator.doExecute(data, tagDomain, null);
        System.out.println(JSON.toJSONString(results, true));
    }

    private void initTagDomain(){
        // TagDomain
        TagDomain tagDomain = new TagDomain(GRAPH);
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
        tsi.setGraph(GRAPH);
        tsi.setSchema("Company");
        tsi.setFields("object_key,capital");
        tsi.setType(TagSchemaType.VERTEX);
        tagDomain.getTagSchemaInfoList().add(tsi);
        System.out.println(JSON.toJSONString(tagDomain, true));

        HDFSHelper helper = new HDFSHelper();
        helper.upsertLine(Constants.PATH_TAG_DOMAIN, JSON.toJSONString(tagDomain));
        helper.close();
    }
}
