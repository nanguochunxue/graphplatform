package com.haizhi.graph.sys.auth.service.impl;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableSet;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.sys.auth.dao.SysRoleDao;
import com.haizhi.graph.sys.auth.dao.SysUserDao;
import com.haizhi.graph.sys.auth.dao.SysUserRoleDao;
import com.haizhi.graph.sys.auth.model.po.QSysUserPo;
import com.haizhi.graph.sys.auth.model.po.SysUserPo;
import com.haizhi.graph.sys.auth.model.po.SysUserRolePo;
import com.haizhi.graph.sys.auth.model.qo.SysUserPageQo;
import com.haizhi.graph.sys.auth.model.suo.SysUserSuo;
import com.haizhi.graph.sys.auth.model.uo.SysUserPwdUo;
import com.haizhi.graph.sys.auth.model.vo.SysUserSimpleVo;
import com.haizhi.graph.sys.auth.service.SysUserService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tanghaiyang on 2018/1/4.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
@EnableJpaRepositories({"com.haizhi.graph"})
@EntityScan({"com.haizhi.graph"})
@ComponentScan({"com.haizhi.graph"})
public class SysUserServiceTest {
    private static final GLog LOG = LogFactory.getLogger(SysUserServiceTest.class);

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Test
    public void find() {
        SysUserPageQo qo = new SysUserPageQo();
        qo.setUserNo("test");
        QSysUserPo table = QSysUserPo.sysUserPo;
        BooleanBuilder builder = new BooleanBuilder();
        if (!StringUtils.isEmpty(qo.getUserNo())) {
            builder = builder.and(table.userNo.like(JQL.likeWrap(qo.getUserNo())));
        }

        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "updatedDt"));
        Iterable<SysUserPo> results = sysUserDao.findAll(builder, sort);
        List<SysUserSimpleVo> rows = new ArrayList<>();
        results.forEach(sysUserPo -> {
            rows.add(new SysUserSimpleVo(sysUserPo));
        });
        LOG.info(Response.success(rows));
    }


    @Test
    public void delete() {
        try {
            SysUserSuo suo = new SysUserSuo();
            suo.setId(9L);
            sysUserDao.delete(suo.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info(Response.success());
    }

    @Test
    public void saveOrUpdate() {
        try {
            SysUserSuo suo = new SysUserSuo();
            suo.setUserNo("haision3");
            suo.setName("测试3");
            suo.setEmail("test3@163.com");
            suo.setPhone("153xxxx");
            suo.setPassword("123456");
            suo.setRoleIds(Collections.singleton(13L));

//            SysUserPo po = new SysUserPo(suo, true);
//            po.setCreatedById("aa");
//            po.setUpdateById("ffffff");
//            sysUserDao.save(po);

            sysUserService.saveOrUpdate(suo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info(Response.success());
    }

    @Test
    public void saveUserRole() {
        try {
            SysUserRolePo sysUserRolePo = new SysUserRolePo();
            sysUserRolePo.setUserId(11L);
            sysUserRolePo.setRoleId(22L);
            sysUserRoleDao.save(sysUserRolePo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info(Response.success());
    }

    @Test
    public void resetPassword() {
        try {
            SysUserPwdUo suo = new SysUserPwdUo();
            suo.setUserNo("haision2");
            suo.setPasswordOld("123456");
            suo.setPassword("111111");
            sysUserService.updatePassword(suo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info(Response.success());
    }


}
