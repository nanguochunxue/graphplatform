package com.haizhi.graph.engine.flow.tools.hbase;

import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.model.CudResponse;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/3/30.
 */
public class HBaseContextTest {

    static HBaseContext context = new HBaseContext(null);

    private StoreURL storeURL;

    @Test
    public void createTableIfNotExists(){
        boolean success = context.createTableIfNotExists(storeURL, "crm_dev", "");
        Assert.assertTrue(success);
    }

    @Test
    public void bulkUpsert(){
        HBaseRows rows = new HBaseRows();
        Map<String, String> row = new HashMap<>();
        row.put(Keys._ROW_KEY, "000#1#");
        row.put("tagId", "1");
        row.put("objectKey", "EFF1213F8A5D883A86F5D6B5FBE20E3C");
        row.put("dataType", "STRING");
        row.put("value", "3");
        rows.addStringRow(row);
        row = new HashMap<>();
        row.put(Keys._ROW_KEY, "456#2#EFF1213F8A5D883A86F5D6B5FBE20E3C");
        row.put("tagId", "2");
        row.put("objectKey", "EFF1213F8A5D883A86F5D6B5FBE20E3C");
        row.put("dataType", "STRING");
        row.put("value", "5");
        rows.addStringRow(row);
        CudResponse response = context.bulkUpsert(storeURL, "crm_dev:tag_value", rows);
        Assert.assertTrue(response.isSuccess());
    }

    @Test
    public void deleteByScan(){
        context.deleteByScan(storeURL, "crm_dev", "tag_value", "000#1", "999#~");
    }
}
