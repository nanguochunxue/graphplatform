package com.haizhi.graph.sys.file.service;

import com.haizhi.graph.sys.file.constant.StoreType;
import com.haizhi.graph.sys.file.model.po.SysFilePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by chengangxiong on 2019/01/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class SysFileServiceTest {

    @Autowired
    private SysFileService sysFileService;

    @Test
    public void save(){
        SysFilePo sysFilePo = new SysFilePo();
        sysFilePo.setName("fileName");
        sysFilePo.setStoreType(StoreType.HDFS);
        sysFilePo.setUrl("hdfs://graph/data/dte/aaaa");
        sysFilePo = sysFileService.saveOrUpdate(sysFilePo);
        assert sysFilePo.getId() != null;
    }
}