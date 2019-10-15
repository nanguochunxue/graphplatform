package com.haizhi.graph.dc.inbound.metric;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.common.model.DcInboundTaskMetric;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.service.DcSchemaService;
import com.haizhi.graph.dc.core.constant.TaskInstanceState;
import com.haizhi.graph.dc.core.model.po.DcTaskInstancePo;
import com.haizhi.graph.dc.core.model.po.DcTaskPo;
import com.haizhi.graph.dc.inbound.service.DcTaskInstanceService;
import com.haizhi.graph.dc.inbound.service.DcTaskService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by chengangxiong on 2019/03/07
 */
@Component
public class MetricConsumer {

    private static final GLog LOG = LogFactory.getLogger(MetricConsumer.class);

    @Autowired
    private DcTaskService dcTaskService;

    @Autowired
    private DcSchemaService dcSchemaService;

    @Autowired
    private DcTaskInstanceService taskInstanceService;

    @Autowired
    private TaskRedisService taskRedisService;

    private LinkedBlockingQueue<DcInboundTaskMetric> metricQueue = new LinkedBlockingQueue<>(1000);

    @PostConstruct
    public void postConstruct() {
    }

    @KafkaListener(topicPattern = "${graph.dc.inbound.metric.topic}", containerFactory = "batchFactory")
    public void listen(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        ack.acknowledge();
        records.forEach(record -> {
            DcInboundTaskMetric metric = JSON.parseObject(record.value(), DcInboundTaskMetric.class);
            if (!metricQueue.offer(metric)) {
                doSaveMetric();
                metricQueue.offer(metric);
            }
        });
    }

    @Scheduled(initialDelayString = "${graph.dc.inbound.metric.scheduled.initialDelay}", fixedDelayString = "${graph.dc.inbound.metric.scheduled.fixDelay}")
    public void scheduleMetric() {
        int processedCount = doSaveMetric();
        if (processedCount != 0) {
            LOG.info("process [{0}] metrics at [{1}]", processedCount, new Date());
        }
    }

    private int doSaveMetric() {
        List<DcInboundTaskMetric> metricToProcess = new LinkedList<>();
        metricQueue.drainTo(metricToProcess);
        processMetric(metricToProcess);
        return metricToProcess.size();
    }

    public void processMetric(List<DcInboundTaskMetric> metricToProcess) {
        Supplier<Stream<DcInboundTaskMetric>> streamSupplier = metricToProcess::stream;
        Stream<DcInboundTaskMetric> taskStream = streamSupplier.get().map(new Function<DcInboundTaskMetric, DcInboundTaskMetric>() {
            @Override
            public DcInboundTaskMetric apply(DcInboundTaskMetric dcInboundTaskMetric) {
                if (Objects.isNull(dcInboundTaskMetric.getTaskInstanceId()) && Objects.nonNull(dcInboundTaskMetric.getTaskId())) {
                    if (StringUtils.isNotEmpty(dcInboundTaskMetric.getDailyStr())) {
                        DcTaskInstancePo taskInstancePo = taskInstanceService.findOrCreate(dcInboundTaskMetric.getTaskId(), dcInboundTaskMetric.getDailyStr());
                        dcInboundTaskMetric.setTaskInstanceId(taskInstancePo.getId());
                    } else if (StringUtils.isNotEmpty(dcInboundTaskMetric.getBatchId())) {

                    }
                }
                return dcInboundTaskMetric;
            }
        }).filter(new Predicate<DcInboundTaskMetric>() {
            @Override
            public boolean test(DcInboundTaskMetric dcInboundTaskMetric) {
                return Objects.nonNull(dcInboundTaskMetric.getTaskInstanceId());
            }
        });

        Map<Boolean, List<DcInboundTaskMetric>> taskStream2 = taskStream.collect(Collectors.groupingBy(new Function<DcInboundTaskMetric, Boolean>() {
            @Override
            public Boolean apply(DcInboundTaskMetric dcInboundTaskMetric) {
                return Objects.isNull(dcInboundTaskMetric.getStoreType());
            }
        }));

        List<DcInboundTaskMetric> taskStoreStream = taskStream2.get(Boolean.FALSE);
        // spark task total count
//        List<DcInboundTaskMetric> taskTotalStream = taskStream2.get(Boolean.TRUE);

//        if (Objects.nonNull(taskTotalStream)){
//            taskTotalStream.stream().collect(Collectors.groupingBy(new Function<DcInboundTaskMetric, Long>() {
//                @Override
//                public Long apply(DcInboundTaskMetric dcInboundTaskMetric) {
//                    return dcInboundTaskMetric.getTaskInstanceId();
//                }
//            }, Collectors.summingLong(new ToLongFunction<DcInboundTaskMetric>() {
//                @Override
//                public long applyAsLong(DcInboundTaskMetric value) {
//                    return value.getRows();
//                }
//            }))).forEach(new BiConsumer<Long, Long>() {
//                @Override
//                public void accept(Long taskInstanceId, Long totalRow) {
//                    updateRowCount(taskInstanceId, totalRow);
//                }
//            });
//        }

        if (Objects.nonNull(taskStoreStream)) {
            taskStoreStream.stream()
                    .collect(Collectors.groupingBy(DcInboundTaskMetric::getTaskInstanceId,
                            Collectors.groupingBy(DcInboundTaskMetric::getStoreType,
                                    Collectors.summingLong(DcInboundTaskMetric::getRows))))
                    .forEach(this::updateAffectedRows);
        }

        if (Objects.nonNull(taskStoreStream)) {
            taskStoreStream.stream()
                    .collect(Collectors.groupingBy(DcInboundTaskMetric::getTaskInstanceId,
                            Collectors.groupingBy(DcInboundTaskMetric::getStoreType,
                                    Collectors.summingLong(DcInboundTaskMetric::getErrorRows))))
                    .forEach(this::updateErrorRows);
        }
    }

    private void updateErrorRows(Long taskInstanceId, Map<StoreType, Long> storeTypeRowMap) {
        DcTaskInstancePo taskInstancePo = taskInstanceService.findOne(taskInstanceId);
        if (Objects.nonNull(taskInstancePo)) {
            Integer hbaseErrorRows = getCount(storeTypeRowMap, StoreType.Hbase);
            Integer esErrorRows = getCount(storeTypeRowMap, StoreType.ES);
            Integer gdbErrorRows = getCount(storeTypeRowMap, StoreType.GDB);
            if (hbaseErrorRows + esErrorRows + gdbErrorRows == 0) {
                return;
            }
            if (hbaseErrorRows != 0) {
                taskInstancePo.setHbaseErrorRows(taskInstancePo.getHbaseErrorRows() + hbaseErrorRows);
            }
            if (esErrorRows != 0) {
                taskInstancePo.setEsErrorRows(taskInstancePo.getEsErrorRows() + esErrorRows);
            }
            if (gdbErrorRows != 0) {
                taskInstancePo.setGdbErrorRows(taskInstancePo.getGdbErrorRows() + gdbErrorRows);
            }
            finishedOrInterrupt(taskInstancePo);
            taskInstanceService.saveOrUpdate(taskInstancePo);
        } else {
            LOG.warn("not exists instanceId : " + taskInstanceId);
        }
    }

    private void finishedOrInterrupt(DcTaskInstancePo taskInstancePo) {
        Long taskId = taskInstancePo.getTaskId();
        DcTaskPo dcTask = dcTaskService.findOne(taskId);
        DcSchemaPo dcSchema = dcSchemaService.findByGraphAndSchema(dcTask.getGraph(), dcTask.getSchema());

        if (taskRedisService.overErrorMode(taskInstancePo.getId(), taskInstancePo.getErrorMode())) {
            taskInstancePo.setState(TaskInstanceState.INTERRUPTED);
        } else if (taskInstancePo.success(dcSchema.isUseHBase(), dcSchema.isUseSearch(), dcSchema.isUseGdb())) {
            taskInstancePo.setState(TaskInstanceState.SUCCESS);
        } else if (taskInstancePo.failed(dcSchema.isUseHBase(), dcSchema.isUseSearch(), dcSchema.isUseGdb())) {
            taskInstancePo.setState(TaskInstanceState.FAILED);
        }
    }

    private void updateAffectedRows(Long taskInstanceId, Map<StoreType, Long> storeTypeRowMap) {
        DcTaskInstancePo taskInstancePo = taskInstanceService.findOne(taskInstanceId);
        if (Objects.nonNull(taskInstancePo)) {
            taskInstancePo.setHbaseAffectedRows(taskInstancePo.getHbaseAffectedRows() + getCount(storeTypeRowMap, StoreType.Hbase));
            taskInstancePo.setEsAffectedRows(taskInstancePo.getEsAffectedRows() + getCount(storeTypeRowMap, StoreType.ES));
            taskInstancePo.setGdbAffectedRows(taskInstancePo.getGdbAffectedRows() + getCount(storeTypeRowMap, StoreType.GDB));
            finishedOrInterrupt(taskInstancePo);
            taskInstanceService.saveOrUpdate(taskInstancePo);
        } else {
            LOG.warn("not exists instanceId : " + taskInstanceId);
        }
    }

    private Integer getCount(Map<StoreType, Long> storeTypeRowMap, StoreType storeType) {
        return storeTypeRowMap.get(storeType) == null ? 0 : storeTypeRowMap.get(storeType).intValue();
    }

    @PreDestroy
    public void preDestroy() {
        doSaveMetric();
    }
}
