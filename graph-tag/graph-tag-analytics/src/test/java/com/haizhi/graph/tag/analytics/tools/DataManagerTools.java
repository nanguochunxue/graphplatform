package com.haizhi.graph.tag.analytics.tools;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.es.index.EsIndexDao;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.tag.analytics.util.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by chengmo on 2018/4/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "tag")
public class DataManagerTools {

    static final String GRAPH = "crm_dev2";

    @Autowired
    HBaseAdminDao hBaseAdminDao;
    @Autowired
    EsIndexDao esIndexDao;

    private StoreURL storeURL;

    @Test
    public void clearAllResultData(){
        deleteEsIndex();
        this.clearHBaseTableData(GRAPH, "tag_value");
        this.clearHBaseTableData(GRAPH, "tag_value_daily");
    }

    @Test
    public void deleteEsIndex(){
        // TODO
        esIndexDao.deleteIndex(null, GRAPH + Constants.TAG_SUFFIX);
    }

    @Test
    public void deleteByScanFromHBase(){
        this.clearHBaseTableData(GRAPH, "tag_value");
    }

    @Test
    public void listTableNames(){
        List<String> results = hBaseAdminDao.listTableNames(storeURL, ".*.history");
        System.out.println(JSON.toJSONString(results, true));
    }

    private void clearHBaseTableData(String graph, String table){
        hBaseAdminDao.deleteByScan(storeURL, graph, table, "000#", "999#~");
    }
}
