package com.haizhi.graph.tag.core.dao;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.haizhi.graph.tag.core.domain.TagParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

/**
 * Created by chengmo on 2018/3/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class TagParameterDAOTest {

    @Autowired
    TagParameterDAO tagParameterDAO;

    @Test
    public void getTagParameters(){
        Set<Long> ids = Sets.newHashSet(1L, 2L);
        List<TagParameter> list = tagParameterDAO.getTagParameters("crm_dev", ids);
        System.out.println(JSON.toJSONString(list, true));
    }
}
