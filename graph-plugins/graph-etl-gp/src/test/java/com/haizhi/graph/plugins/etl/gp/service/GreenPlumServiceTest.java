package com.haizhi.graph.plugins.etl.gp.service;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.model.plugins.etl.gp.EtlGreenPlumQo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chengangxiong on 2019/05/07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-remote")
public class GreenPlumServiceTest {

    @Autowired
    private GreenPlumService greenPlumService;

    @Test
    public void remoteRunPerlScript() throws InterruptedException {
        /*
        * 1,application-haizhi-remote.properties
        *   graph.etl.gp.perlPath=/export_data_remote.pl
        *   graph.etl.gp.exportDir=/Users/haizhi/temp/gp
        *
        * 2,export_data_remote.pl
        *   $command="/Library/PostgreSQL/10/bin/psql -h 192.168.1.213 -p 5432 -U gpadmin ";
        *
        * */
        EtlGreenPlumQo greenPlumQo = new EtlGreenPlumQo();
        greenPlumQo.setTaskInstanceId(-1L);
        greenPlumQo.setFields("object_key,demo_string_field,demo_long_field,demo_double_field,demo_date_field");
        greenPlumQo.setTable("demo_vertex");
        greenPlumQo.setFilter("demo_long_field<8188");
        greenPlumService.startExport(greenPlumQo);
        EtlGreenPlumQo greenPlumQo1 = JSON.parseObject(JSON.toJSONString(greenPlumQo), EtlGreenPlumQo.class);
        greenPlumQo1.setTaskInstanceId(0L);
        greenPlumService.startExport(greenPlumQo1);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}