package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import com.haizhi.graph.dc.core.model.qo.DcStoreQo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.dc.core.model.vo.DcStoreSelectorVo;
import com.haizhi.graph.dc.core.model.vo.DcStoreVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "haizhi-fi")
public class DcStoreServiceTest {

    @Autowired
    private DcStoreService storeService;

    @Test
    public void findByGraph() {
        List<DcStorePo> res = storeService.findByGraph("graph_one");
        assert res.size() == 3;
    }

    @Test
    public void findVoByGraph() {
        List<DcStoreVo> res = storeService.findVoByGraph("demo_graph");
        JSONUtils.println(res);
    }

    @Test
    public void findPage(){

        DcStoreQo qo = new DcStoreQo();
        qo.setName("hbase");
//        PageResponse res = storeService.findPage(qo);
//        assert res.getPayload().getData().size() == 1;
    }

    @Test
    public void saveOrUpdate(){

        DcStoreSuo suo = new DcStoreSuo();
        suo.setId(3L);
        suo.setType(StoreType.GDB);
        suo.setUrl("http://99999");
        suo.setName("arango_update_name");
        Response res = storeService.saveOrUpdate(suo);
        assert res.isSuccess();
    }

    @Test
    @Rollback
    @Transactional
    public void delete(){
        Response res = storeService.delete(3L);
        assert res.isSuccess();
    }

    @Test
    public void selectByStoreType(){
        Response<List<DcStoreSelectorVo>> res = storeService.findAll(StoreType.ES);
        System.out.println(res.getPayload().getData());
    }
}