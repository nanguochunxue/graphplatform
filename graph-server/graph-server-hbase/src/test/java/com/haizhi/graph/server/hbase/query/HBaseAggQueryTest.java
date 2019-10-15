package com.haizhi.graph.server.hbase.query;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.server.api.bean.StoreURL;
import com.haizhi.graph.server.api.hbase.query.HBaseQueryDao;
import com.haizhi.graph.server.hbase.StoreURLFactory;
import com.haizhi.graph.server.api.hbase.admin.HBaseAdminDao;
import com.haizhi.graph.server.api.hbase.admin.bean.ColumnType;
import com.haizhi.graph.server.api.hbase.admin.bean.HBaseRows;
import com.haizhi.graph.server.api.hbase.query.bean.HBaseRangeQuery;
import com.haizhi.graph.server.api.hbase.query.bean.Stats;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/2/5.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class HBaseAggQueryTest {

    private static final String GRAPH = "AggTest";
    private static final String SCHEMA = "testTable";

    @Autowired
    HBaseAdminDao hBaseAdminDao;
    @Autowired
    HBaseQueryDao hBaseQueryDao;

    private StoreURL storeURL;

    @Before
    public void init() {
        storeURL = StoreURLFactory.createHBase_FIC80();
    }

    @Test
    public void aggregationQuery() {
        HBaseRangeQuery rangeQuery = new HBaseRangeQuery(GRAPH, SCHEMA);
        rangeQuery.addAggregation("count(stringField)", "stringField", ColumnType.STRING, Stats.COUNT);
        rangeQuery.addAggregation("min(longField)", "longField", ColumnType.LONG, Stats.MIN);
        rangeQuery.addAggregation("max(doubleField)", "doubleField", ColumnType.DOUBLE, Stats.MAX);
        rangeQuery.addAggregation("avg(doubleField)", "doubleField", ColumnType.DOUBLE, Stats.AVG);
        rangeQuery.addAggregation("sum(doubleField)", "doubleField", ColumnType.DOUBLE, Stats.SUM);
        Map<String, Object> result = hBaseQueryDao.aggregationQuery(storeURL, rangeQuery);
        System.out.println(JSON.toJSONString(result, true));
    }

    @Before
    public void dataInitialize() {
        if (!hBaseAdminDao.existsDatabase(storeURL, GRAPH)) {
            hBaseAdminDao.createDatabase(storeURL, GRAPH);
        }
        if (!hBaseAdminDao.existsTable(storeURL, GRAPH, SCHEMA)) {
            hBaseAdminDao.createTable(storeURL, GRAPH, SCHEMA, false);
        }
        hBaseAdminDao.addAggCoprocessor(storeURL, GRAPH, SCHEMA);

        HBaseRows rows = new HBaseRows();
        Map<String, String> row = new HashMap<>();
        row.put(Keys._ROW_KEY, "000#1");
        row.put("stringField", "string1");
        row.put("dateField", "2018-05-23 35:56:11");
        row.put("longField", "100");
        row.put("doubleField", "1000.1");
        rows.addStringRow(row);

        row = new HashMap<>();
        row.put(Keys._ROW_KEY, "000#2");
        row.put("stringField", "string2");
        row.put("dateField", "");
        row.put("longField", "200");
        row.put("doubleField", "2000.2");
        rows.addStringRow(row);

        rows.addColumnType("longField", ColumnType.LONG);
        rows.addColumnType("doubleField", ColumnType.DOUBLE);
        hBaseAdminDao.bulkUpsert(storeURL, GRAPH, SCHEMA, rows);
    }

    @After
    public void cleanup() {
        if (hBaseAdminDao.existsTable(storeURL, GRAPH, SCHEMA)) {
            hBaseAdminDao.deleteTable(storeURL, GRAPH, SCHEMA);
        }
    }
}
