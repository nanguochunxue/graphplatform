package com.haizhi.graph.server.tiger.util;

import com.haizhi.graph.common.constant.GOperation;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.admin.model.GdbSuo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/5.
 */
public class WrapUtilsTest {
    private static final GLog LOG = LogFactory.getLogger(WrapUtilsTest.class);




    @Test
    public void buildDelteUrlsTest(){
        String url = "http://192.168.1.234:9000/graph";
        GdbSuo suoVertices = buildVerticesTest();
        List<String> listVertices = TigerWrapper.wrapDelete(suoVertices, url);
        LOG.info("listVertices: \n{0}", listVertices);

        GdbSuo suoEdges = buildEdgesTest();
        List<String> listEdges = TigerWrapper.wrapDelete(suoEdges, url);
        LOG.info("listEdges: \n{0}", listEdges);
    }

    public static GdbSuo buildVerticesTest(){
        GdbSuo suo = new GdbSuo();
        suo.setGraph("work_graph");
        suo.setSchema("company");
        suo.setType(SchemaType.VERTEX);
        suo.setOperation(GOperation.UPDATE);

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put(Keys.OBJECT_KEY, "5729391EB83550536AFF9DB181696F1E");
        map1.put("business_status", "吊销，未注销");
        map1.put("capital", "30000.00");
        map1.put("city", "河北石家庄");
        map1.put("ctime", "2017-07-17 00:19:37");
        map1.put("enterprise_type", "有限责任公司");
        map1.put("industry", "批发业");
        map1.put("name", "石家庄台飞凯商贸有限公司");
        map1.put("operation_startdate", "2011-01-19");
        map1.put("province", "河北");
        map1.put("utime", "2017-07-17 00:19:37");
        rows.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put(Keys.OBJECT_KEY, "F22995C230ED6673A8738D09B3CA4E94");
        map2.put("business_status", "存续");
        map2.put("capital", "30000.00");
        map2.put("city", "广东佛山");
        map2.put("ctime", "2017-07-17 00:19:37");
        map2.put("enterprise_type", "有限责任公司(自然人投资或控股)");
        map2.put("industry", "道路运输业");
        map2.put("name", "佛山市顺德区博浱汇骏达物流有限公司");
        map2.put("operation_startdate", "2011-01-19");
        map2.put("province", "广东");
        map2.put("utime", "2017-07-17 00:19:37");
        rows.add(map2);

        suo.setRows(rows);

        return suo;
    }

    public static GdbSuo buildEdgesTest(){
        GdbSuo suo = new GdbSuo();
        suo.setGraph("work_graph");
        suo.setSchema("guarantee");
        suo.setType(SchemaType.EDGE);
        suo.setOperation(GOperation.UPDATE);

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put(Keys._FROM, "Company/74DFB7AAAC745B53E4D921A112CDAB46");
        map1.put(Keys._TO, "Company/DCF3F5B4D4D4461412E9C8E88FFA9421");
        map1.put("begin_date", "2015-06-01");
        map1.put("currency", "人民币");
        map1.put("end_date", "2017-06-01");
        map1.put("type", "020");
        map1.put("value", 100);
        rows.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put(Keys._FROM, "Company/74DFB7AAAC745B53E4D921A112111111");
        map2.put(Keys._TO, "Company/DCF3F5B4D4D4461412E9C8E88222222");
        map2.put("begin_date", "2014-06-01");
        map2.put("currency", "人民币");
        map2.put("end_date", "2014-12-15");
        map2.put("type", "020");
        map2.put("value", 200);
        rows.add(map2);
        suo.setRows(rows);
        return suo;
    }

}



