package com.haizhi.graph.tag.core.dao;

import com.github.wenhao.jpa.Specifications;
import com.haizhi.graph.common.constant.Constants;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.tag.core.domain.Tag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengmo on 2018/3/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class TagDAOTest {

    @Autowired
    TagDAO tagDAO;

    @Test
    public void findPage(){
/*        PageRequest pageRequest = new PageRequest(0, 3,
                new Sort(new Sort.Order(Sort.Direction.ASC, "id")));*/

        PageRequest pageRequest = new PageRequest(0, 3);
        Specification<Tag> specification = Specifications.<Tag>and()
                .eq("graph", "crm_dev2")
                .eq("enabledFlag", Constants.Y)
                //.eq("tagGroup", TagGroup.SYSTEM)
                //.in("tagCategoryId", 701, 702)
                //.ge("upTime", DateUtils.toLocal("2018-07-20 17:48:35"))
                //.like("tagName", "%存款%")
                .build();
        Page<Tag> page = tagDAO.findAll(specification, pageRequest);
        JSONUtils.println(page.getContent());
    }

    @Test
    public void findAllTags(){
        List<Tag> tags = tagDAO.findAll("crm_dev2");
        JSONUtils.println(tags.size());
    }
}
