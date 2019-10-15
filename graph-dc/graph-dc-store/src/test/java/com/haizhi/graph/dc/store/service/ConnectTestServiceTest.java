package com.haizhi.graph.dc.store.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.dc.store.api.service.StoreUsageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengangxiong on 2019/04/01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class ConnectTestServiceTest {

    @Autowired
    private ConnectTestService connectTestService;

    @Autowired
    private StoreUsageService storeUsageService;

    @Test
    public void testArangoSuccess(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("192.168.1.176:8529");
        dcStoreSuo.setUser("sz-haizhi");
        dcStoreSuo.setPassword("sz-test@haizhi.com");
        dcStoreSuo.setType(StoreType.GDB);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testArangoFail(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("10.10.10.8:8529");
        dcStoreSuo.setUser("app");
        dcStoreSuo.setPassword("app");
        dcStoreSuo.setType(StoreType.GDB);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == false;
        System.out.println(response.getMessage());
    }

    @Test
    public void testEsSuccess(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("192.168.1.49:9300,192.168.1.51:9300,192.168.1.52:9300");
        dcStoreSuo.setType(StoreType.ES);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testEs6Success(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setEnvId(1000006L);
        dcStoreSuo.setUrl("192.168.1.223:24148,192.168.1.224:24148,192.168.1.225:24148");
        dcStoreSuo.setType(StoreType.ES);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testEs6KSYUNSuccess(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        //dcStoreSuo.setEnvId(1000006L);
        dcStoreSuo.setUrl("192.168.1.190:9200");
        dcStoreSuo.setType(StoreType.ES);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testEsFail(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("10.10.10.7:8529");
        dcStoreSuo.setType(StoreType.ES);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == false;
        System.out.println(response.getMessage());
    }

    @Test
    public void testHbaseSuccess(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("hbase://hadoop01.sz.haizhi.com,hadoop02.sz.haizhi.com,hadoop03.sz.haizhi.com:2181");
        dcStoreSuo.setType(StoreType.Hbase);
        //dcStoreSuo.setEnvId(35L);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testHbaseFiSuccess(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setEnvId(1L);
        dcStoreSuo.setUrl("192.168.1.223,192.168.1.224,192.168.1.225:24002");
        dcStoreSuo.setType(StoreType.Hbase);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testHbaseFail(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("hbase://hadoop04.sz.haizhi.com,hadoop05.sz.haizhi.com,hadoop06.sz.haizhi.com:2181");
        dcStoreSuo.setType(StoreType.Hbase);
        dcStoreSuo.setEnvId(35L);
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == false;
        System.out.println(response.getMessage());
    }

    @Test
    public void testHiveSuccess(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("jdbc:hive2://hadoop01.sz.haizhi.com:10000/default");
        dcStoreSuo.setType(StoreType.Hive);
        dcStoreSuo.setEnvId(35L);
        dcStoreSuo.setUser("root");
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testHiveFail(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("jdbc:hive2://hadoop02.sz.haizhi.com:10000/default");
        dcStoreSuo.setType(StoreType.Hive);
        dcStoreSuo.setEnvId(35L);
        dcStoreSuo.setUser("root");
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == false;
        System.out.println(response.getMessage());
    }

    @Test
    public void testHDFSSuccess(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("hdfs://hadoop01.sz.haizhi.com:8022/");
        dcStoreSuo.setType(StoreType.HDFS);
        dcStoreSuo.setEnvId(35L);
        dcStoreSuo.setUser("root");
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == true;
        System.out.println(response.getMessage());
    }

    @Test
    public void testHDFSFail(){
        DcStoreSuo dcStoreSuo = new DcStoreSuo();
        dcStoreSuo.setUrl("hdfs://hadoop02.sz.haizhi.com:8022/");
        dcStoreSuo.setType(StoreType.HDFS);
        dcStoreSuo.setEnvId(35L);
        dcStoreSuo.setUser("root");
        Response<String> response = connectTestService.process(dcStoreSuo);
        assert response.isSuccess() == false;
        System.out.println(response.getMessage());
    }

    @Test
    public void autoGenURLHBase(){
        Response<String> resp = connectTestService.autoGenUrl(1L, StoreType.Hbase);
        String url = resp.getPayload().getData();
        System.out.println(url);

        DcStoreSuo storeSuo = new DcStoreSuo();
        storeSuo.setType(StoreType.Hbase);
        storeSuo.setEnvId(1L);
        storeSuo.setUrl(url);
        Response<String> resp2 = connectTestService.process(storeSuo);
        System.out.println(resp2.getPayload().getData());
    }

    @Test
    public void autoGenURLHdfs(){
        Response<String> resp = connectTestService.autoGenUrl(1L, StoreType.HDFS);
        String url = resp.getPayload().getData();
        System.out.println(url);

        if (url == null){
            url = "hdfs://hadoop01.sz.haizhi.com:8022/";
        }
        DcStoreSuo storeSuo = new DcStoreSuo();
        storeSuo.setType(StoreType.HDFS);
        storeSuo.setEnvId(1L);
        storeSuo.setUrl(url);
        Response<String> resp2 = connectTestService.process(storeSuo);
        System.out.println(resp2.getPayload().getData());
    }

    @Test
    public void gpExporterTest(){
        Response<String> res = connectTestService.gpExportUrl("http://127.0.0.1:10041/plugins/etl/gp/");
        assert res.isSuccess();

        Response<String> res2 = connectTestService.gpExportUrl("http://127.0.0.1:10041/plugins/etl/gp3/");
        assert !res2.isSuccess();
    }

    @Test
    public void gpTest(){
        DcStoreSuo suo = new DcStoreSuo();
        suo.setType(StoreType.Greenplum);
        suo.setUrl("jdbc:postgresql://192.168.1.213:5432/demo_graph");
        suo.setUser("gpadmin");
        suo.setPassword("");
        assert connectTestService.process(suo).isSuccess();
    }

    @Test
    public void gpTestLocal(){
        DcStoreSuo suo = new DcStoreSuo();
        suo.setType(StoreType.Greenplum);
        suo.setUrl("jdbc:postgresql://localhost:5432/demo_graph");
        suo.setUser("postgres");
        suo.setPassword("postgres");
        assert connectTestService.process(suo).isSuccess();
    }

    @Test
    public void fi_hbase_test(){
        DcStoreSuo suo = new DcStoreSuo();
        suo.setType(StoreType.Hbase);
        suo.setEnvId(1000020L);
        suo.setUrl("fies01,fies02,fies03:24002");
        assert connectTestService.process(suo).isSuccess();
    }
}