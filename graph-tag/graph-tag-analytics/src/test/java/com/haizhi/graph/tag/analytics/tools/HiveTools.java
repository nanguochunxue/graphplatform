package com.haizhi.graph.tag.analytics.tools;

import com.haizhi.graph.common.json.JSONUtils;
import com.haizhi.graph.common.util.FileUtils;
import com.haizhi.graph.engine.flow.tools.hive.HiveHelper;
import com.haizhi.graph.engine.flow.tools.hive.HiveTable;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/7/31.
 */
public class HiveTools {

    static final String ACTIVE_PROFILE = "application-haizhi-tdh.properties";

    @Test
    public void query(){
        HiveHelper hiveHelper = new HiveHelper(ACTIVE_PROFILE);
        String sql = "select * from crm_dev2.to_balance limit 10";
        List<Map<String, Object>> rows = hiveHelper.executeQuery(sql);
        JSONUtils.println(rows);
    }

    @Test
    public void describeTables(){
        HiveHelper hiveHelper = new HiveHelper(ACTIVE_PROFILE);
        List<HiveTable> list = hiveHelper.describeTables("crm_dev2");
        JSONUtils.println(list);
    }

    @Test
    public void executeSQL(){
        String sql = FileUtils.readTxtFile("hive.SQL");
        List<String> sqlList = new ArrayList<>();
        for (String str : sql.split(";")) {
            if (StringUtils.isBlank(str)){
                continue;
            }
            sqlList.add(str);
        }
        HiveHelper hiveHelper = new HiveHelper(ACTIVE_PROFILE);
        boolean success = hiveHelper.execute(sqlList);
        System.out.println(success);
    }
}
