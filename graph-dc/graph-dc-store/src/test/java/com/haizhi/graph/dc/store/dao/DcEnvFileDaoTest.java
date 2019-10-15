package com.haizhi.graph.dc.store.dao;

import com.google.common.base.Charsets;
import com.haizhi.graph.dc.core.dao.DcEnvFileDao;
import com.haizhi.graph.dc.core.model.po.DcEnvFilePo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/03/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class DcEnvFileDaoTest {

    @Autowired
    private DcEnvFileDao dcEnvFileDao;

    @Test
//    @Transactional
//    @Commit
    public void test(){
        DcEnvFilePo po = new DcEnvFilePo();
        po.setEnvId(1L);
        po.setName("name");
        po.setContent("aaa".getBytes(Charsets.UTF_8));
        dcEnvFileDao.save(po);
    }
}