//package com.haizhi.graph.tag.analytics.service;
//
//import com.alibaba.fastjson.JSON;
//import com.haizhi.graph.server.es.search.vo.EsQuery;
//import com.haizhi.graph.server.es.search.vo.EsQueryResult;
//import com.haizhi.graph.server.es.search.vo.EsSearchDao;
//import com.haizhi.graph.tag.analytics.bean.TagValue;
//import com.haizhi.graph.tag.analytics.util.TagUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * Created by chengmo on 2018/6/1.
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ActiveProfiles(profiles = "test")
//public class TagAggQueryTest {
//
//    @Autowired
//    EsSearchDao esSearchDao;
//
//    @Test
//    public void aggQuery(){
//        String graph = "crm_dev";
//        EsQuery esQuery = new EsQuery(TagUtils.getTagEsIndex(graph));
//        esQuery.addType(TagValue._schema);
//        esQuery.setPageSize(0);
//        esQuery.addTermFilter(TagValue.tagId).addValues(70100101);
//        esQuery.addTermAggregation(TagValue.tagId);
//        EsQueryResult esResult = esSearchDao.search(esQuery);
//        System.out.println(JSON.toJSONString(esResult, true));
//    }
//}
