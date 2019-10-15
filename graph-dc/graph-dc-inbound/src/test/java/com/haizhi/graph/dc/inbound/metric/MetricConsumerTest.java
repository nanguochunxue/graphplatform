package com.haizhi.graph.dc.inbound.metric;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.common.model.DcInboundTaskMetric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chengangxiong on 2019/03/09
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class MetricConsumerTest {

    @Autowired
    private MetricConsumer consumer;

    private List<DcInboundTaskMetric> metricToProcess = new LinkedList<>();

    @Test
    public void testTaskTotal(){
        add(1L, 3L);
        add(1L, 5L);
        consumer.processMetric(metricToProcess);
    }

    @Test
    public void testTaskStore(){
        add(1L, 3L, StoreType.ES);
        add(1L, 4L, StoreType.ES);
        add(1L, 5L, StoreType.Hbase);
        add(1L, 6L, StoreType.Hbase);
        add(1L, 7L, StoreType.GDB);
        add(1L, 8L, StoreType.GDB);
        consumer.processMetric(metricToProcess);
    }

    @Test
    public void testApiTotal(){
        addApi(1L, 1L, "2019-01-01");
        addApi(1L, 2L, "2019-01-01");
        addApi(1L, 3L, "2019-01-01");
        addApi(1L, 4L, "2019-01-02");
        addApi(1L, 5L, "2019-01-02");
        consumer.processMetric(metricToProcess);
    }

    @Test
    public void testApiStore(){
        addApi(1L, 1L, "2019-01-01", StoreType.Hbase);
        addApi(1L, 2L, "2019-01-01", StoreType.ES);
        addApi(1L, 3L, "2019-01-03", StoreType.ES);
        addApi(1L, 4L, "2019-01-03", StoreType.GDB);
        addApi(1L, 5L, "2019-01-03", StoreType.GDB);
        addApi(1L, 6L, "2019-01-03", StoreType.Hbase);
        addApi(1L, 7L, "2019-01-03", StoreType.Hbase);
        consumer.processMetric(metricToProcess);
    }

    private void addApi(long taskId, long rowCount, String daily, StoreType storeType) {
        DcInboundTaskMetric metric = new DcInboundTaskMetric();
        metric.setRows(rowCount);
        metric.setTaskId(taskId);
        metric.setDailyStr(daily);
        metric.setStoreType(storeType);
        metricToProcess.add(metric);
    }

    private void addApi(long taskId, long rowTotal, String dailyStr) {
        DcInboundTaskMetric metric = new DcInboundTaskMetric();
        metric.setRows(rowTotal);
        metric.setTaskId(taskId);
        metric.setDailyStr(dailyStr);
        metricToProcess.add(metric);
    }

    private void add(long instanceId, long totalCount) {
        DcInboundTaskMetric metric = new DcInboundTaskMetric();
        metric.setTaskInstanceId(instanceId);
        metric.setRows(totalCount);
        metricToProcess.add(metric);
    }

    private void add(long taskInstanceId, long rowCount, StoreType storeType) {
        DcInboundTaskMetric metric = new DcInboundTaskMetric();
        metric.setTaskInstanceId(taskInstanceId);
        metric.setRows(rowCount);
        metric.setStoreType(storeType);
        metricToProcess.add(metric);
    }
}