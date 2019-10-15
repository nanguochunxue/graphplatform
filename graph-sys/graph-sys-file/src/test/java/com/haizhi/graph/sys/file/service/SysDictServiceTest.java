package com.haizhi.graph.sys.file.service;

import com.haizhi.graph.sys.file.model.po.SysDictPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengangxiong on 2019/03/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class SysDictServiceTest {

    @Autowired
    private SysDictService sysDictService;

    @Test
    public void test(){
        List<SysDictPo> res = sysDictService.findStoreSupportedVersion("ES");
        System.out.println(res);
    }
}