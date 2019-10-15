package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.vo.DcGraphFrameVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcGraphServiceTest {

    @Autowired
    private DcGraphService dcGraphService;

    @Test
    public void test(){
        Response<List<DcGraphFrameVo>> res = dcGraphService.findGraphFrame();
        System.out.println(res);
    }
}