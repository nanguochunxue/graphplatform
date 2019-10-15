package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chengangxiong on 2019/04/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcEnvFileServiceTest {

    @Autowired
    private DcEnvFileService dcEnvFileService;

    @Test
    public void find(){
        List<DcEnvFilePo> res1 = dcEnvFileService.findFileListById(1000001L);
        System.out.println(res1.size());
//        dcEnvFileService.delete(1000001L, 8L);
        List<DcEnvFilePo> res2 = dcEnvFileService.findFileListById(1000001L);
        System.out.println(res2.size());
    }
}