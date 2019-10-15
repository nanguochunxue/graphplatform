package com.haizhi.graph.sys.auth.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.sys.auth.model.qo.SysUserPageQo;
import com.haizhi.graph.sys.auth.model.vo.SysUserSimpleVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengmo on 2018/12/25.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DemoServiceTest {

    @Autowired
    DemoService demoService;

    /**
     * Single table query.
     */
    @Test
    public void find(){
        SysUserPageQo qo = new SysUserPageQo();
        qo.setUserNo("admin");
        List<SysUserSimpleVo> list = demoService.find(qo);
        JSONUtils.println(list);
    }

    /**
     * Single table page query.
     */
    @Test
    public void findPage(){
        SysUserPageQo qo = new SysUserPageQo();
        qo.setUserNo("admin");
        PageResponse<SysUserSimpleVo> page = demoService.findPage(qo);
        JSONUtils.println(page);
    }

    /**
     * Multiple tables query.
     */
    @Test
    public void findUserRoles(){
        SysUserPageQo qo = new SysUserPageQo();
        qo.setUserNo("admin");
        Response<SysUserSimpleVo> response = demoService.findUserRoles(qo);
        JSONUtils.println(response);
    }

    /**
     * Multiple tables page query.
     */
    @Test
    public void findPageUserRoles(){
        SysUserPageQo qo = new SysUserPageQo();
        qo.setUserNo("admin");
        PageResponse<SysUserSimpleVo> page = demoService.findPageUserRoles(qo);
        JSONUtils.println(page);
    }
}
