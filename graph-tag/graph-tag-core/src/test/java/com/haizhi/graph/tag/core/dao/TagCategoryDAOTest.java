package com.haizhi.graph.tag.core.dao;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.tag.core.domain.TagCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengmo on 2018/3/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class TagCategoryDAOTest {

    @Autowired
    TagCategoryDAO tagDAO;

    @Test
    public void findAll(){
        List<TagCategory> tagList = tagDAO.findAll("crm_dev");
        System.out.println(JSON.toJSONString(tagList, true));
    }
}
