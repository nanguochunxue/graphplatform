package com.haizhi.graph.dc.core.redis;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.dao.DcSchemaDao;
import com.haizhi.graph.dc.core.dao.DcSchemaFieldDao;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.service.DcGraphService;
import com.haizhi.graph.dc.core.service.DcMetadataService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Create by zhoumingbing on 2019-06-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcPubServiceTest {

    @Autowired
    private DcPubService dcPubService;

    @Autowired
    private DcGraphService dcGraphService;

    @Autowired
    private DcMetadataService dcMetadataService;

    @Autowired
    private DcSchemaDao dcSchemaDao;

    @Autowired
    private DcSchemaFieldDao dcSchemaFieldDao;

    @Test
    public void publish() throws InterruptedException {
        String graph = "cache_refresh";
        String schema = "test_cache_refresh";
        DcGraphPo graphPo = dcGraphService.findByByGraph(graph);

        System.out.println("---------------更该之前的数据----------------");
        Domain domain = dcMetadataService.getDomain(graph);
        System.out.println(JSON.toJSONString(domain, true));

        DcSchemaPo schemaPo = dcSchemaDao.findByGraphAndSchema(graph, schema);
        List<DcSchemaFieldPo> dcSchemaFieldPos = dcSchemaFieldDao.findByGraphAndSchema(graph, schema);
        dcSchemaFieldPos.forEach(fieldPo -> {
            if (!StringUtils.equals(fieldPo.getField(), "object_key")) {
                fieldPo.setField(fieldPo.getField() + "_1");
                fieldPo.setFieldNameCn(fieldPo.getFieldNameCn() + "&&");
            }
        });
        dcSchemaFieldDao.save(dcSchemaFieldPos);

        System.out.println("---------------更新之后的数据----------------");
        domain = dcMetadataService.getDomain(graph);
        System.out.println(JSON.toJSONString(domain, true));


        dcPubService.publish(graph);
        Thread.sleep(3000L);
        System.out.println("---------------通知之后的数据----------------");
        domain = dcMetadataService.getDomain(graph);
        System.out.println(JSON.toJSONString(domain, true));

    }
}
