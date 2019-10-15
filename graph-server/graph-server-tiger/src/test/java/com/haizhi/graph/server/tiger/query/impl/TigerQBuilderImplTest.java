package com.haizhi.graph.server.tiger.query.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.search.GQuery;
import com.haizhi.graph.server.tiger.util.TigerWrapperTest;
import org.junit.Test;

import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/18.
 */
public class TigerQBuilderImplTest {

    private static final GLog LOG = LogFactory.getLogger(TigerQBuilderImplTest.class);

    @Test
    public void buildGQueryTest(){
        GQuery gQuery = TigerWrapperTest.buildGQuery();
        Map<String, Object> filter =  gQuery.getFilter();
        StringBuilder expression = new StringBuilder();
        TigerQBuilderImpl tigerQBuilderImpl = new TigerQBuilderImpl();
        tigerQBuilderImpl.buildFilter(expression, filter);
        gQuery.setFilterExpression(expression.toString());

        LOG.info("gQuery:\n{0}", JSON.toJSONString(gQuery));
        LOG.info("gQuery:\n{0}", JSON.toJSONString(gQuery, SerializerFeature.PrettyFormat));
    }

}
