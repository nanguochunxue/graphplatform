package com.haizhi.graph.sys.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import com.haizhi.graph.sys.auth.service.SysRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by tanghaiyang on 2019/2/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
@EnableJpaRepositories({"com.haizhi.graph"})
@EntityScan({"com.haizhi.graph"})
@ComponentScan({"com.haizhi.graph"})
public class SysRoleServiceTest {

    @Autowired
    private SysRoleService sysRoleService;

    @Test
    public void update() {
    }

    @Test
    public void getResourceUrlByRoleIds() {
        ImmutableSet<String> roleIds = ImmutableSet.of("1000000");
        List<String> resourceByRoleIds = sysRoleService.findResourceByRoleIds(roleIds);
        System.out.println(JSON.toJSONString(resourceByRoleIds, true));
    }

}
