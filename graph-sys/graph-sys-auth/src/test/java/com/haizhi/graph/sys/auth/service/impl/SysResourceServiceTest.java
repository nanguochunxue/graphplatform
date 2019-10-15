package com.haizhi.graph.sys.auth.service.impl;

import com.haizhi.graph.common.model.BaseTreeNodeVo;
import com.haizhi.graph.common.model.VoTreeBuilder;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.sys.auth.dao.SysResourceDao;
import com.haizhi.graph.sys.auth.model.po.SysResourcePo;
import com.haizhi.graph.sys.auth.model.vo.SysResourceVo;
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
import java.util.List;

/**
 * Created by tanghaiyang on 2019/1/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
@EnableJpaRepositories({"com.haizhi.graph"})
@EntityScan({"com.haizhi.graph"})
@ComponentScan({"com.haizhi.graph"})
public class SysResourceServiceTest {

    private static final GLog LOG = LogFactory.getLogger(SysResourceServiceTest.class);

    @Autowired
    private SysResourceDao sysResourceDao;

    @Test
    public void findTree() throws Exception{
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "parentId"));
        Iterable<SysResourcePo> results = sysResourceDao.findAll(sort);

        List<SysResourceVo> list = trans(results);
//        list.forEach(baseTreeNodeVo -> {
//            System.out.println(baseTreeNodeVo.toString());
//        });

        BaseTreeNodeVo tree = VoTreeBuilder.createTree(list, SysResourceVo.class);
        LOG.info(tree.toString());
    }

    private List<SysResourceVo> trans(Iterable<SysResourcePo> results){
        ArrayList<SysResourceVo> ret = new ArrayList<>();
        results.forEach(sysResourcePo -> {
            ret.add(new SysResourceVo(sysResourcePo));
        });
        return ret;
    }


}
