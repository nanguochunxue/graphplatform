package com.haizhi.graph.tag.analytics.engine.conf;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.tag.analytics.engine.alg.AlgFlowTask;
import com.haizhi.graph.tag.analytics.engine.alg.AlgSchema;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangxy on 2018/5/29.
 */


public class AlgFlowTaskTest {
    @Test
    public void example() {
        //String json = "{\"algname\": \"PageRank\",\"schema\": [{\"table\": \"Company\",\"fields\": \"1,2,3\"},{\"table\": \"Person\",\"fields\": \"4,5,6\"}]}";
        String json = "{\n" +
                "    \"algname\": \"PageRank\",\n" +
                "    \"namespace\": \"crm\",\n" +
                "    \"schema\": [\n" +
                "        {\n" +
                "            \"table\": \"Person\",\n" +
                "            \"fields\": \"name\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"table\": \"Company\",\n" +
                "            \"fields\": \"name,capital,enterprise_type\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"table\": \"invest\",\n" +
                "            \"fields\": \"invest_amount\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"table\": \"officer\",\n" +
                "            \"fields\": \"position\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"table\": \"tradable_share\",\n" +
                "            \"fields\": \"total_stake_distribution,increase_decrease_share\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        AlgFlowTask algFlowTask = JSON.parseObject(json, AlgFlowTask.class);
        System.out.println(algFlowTask.algName);
        //List<Map<String, String>> schema = algFlowTask.schema;
        //schema.forEach(e-> System.out.println(e.toString()));
        List<AlgSchema> schema = algFlowTask.schema;
        schema.forEach(e-> System.out.println(e.table+" "+e.fields));


        String str = "{\n" +
                "\t\"graph\": \"crm\",\n" +
                "\t\"alg\": \"ActualController\",\n" +
                "\t\"PersonTable\": \"Person\",\n" +
                "\t\"PersonName\": \"name\",\n" +
                "\t\"companyTable\": \"Company\",\n" +
                "\t\"companyName\": \"name\",\n" +
                "\t\"capital\": \"capital\",\n" +
                "\t\"enterpriseType\": \"enterprise_type\",\n" +
                "\t\"investTable\": \"invest\",\n" +
                "\t\"investAmount\": \"invest_amount\",\n" +
                "\t\"officerTable\": \"officer\",\n" +
                "\t\"position\": \"position\",\n" +
                "\t\"tradableShareTable\": \"tradable_share\",\n" +
                "\t\"totalStakeDistribution\": \"total_stake_distribution\",\n" +
                "\t\"increaseDecreaseShare\": \"increase_decrease_share\",\n" +
                "\t\"partitions\": \"240\",\n" +
                "\t\"hdfsPath\": \"/user/graph/tag/alg/\"\n" +
                "}";

        Map parse = JSON.parseObject(str);

        parse.keySet().forEach(e-> System.out.println(
                e + " "+parse.get(e)
        ));

    }
}
