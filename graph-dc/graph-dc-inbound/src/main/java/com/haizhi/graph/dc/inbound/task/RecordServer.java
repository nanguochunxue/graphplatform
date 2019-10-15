package com.haizhi.graph.dc.inbound.task;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.v1.Response;
import com.haizhi.graph.common.rest.RestFactory;
import com.haizhi.graph.common.rest.RestService;
import com.haizhi.graph.dc.core.constant.DcConstants;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.common.service.TaskRedisService;
import com.haizhi.graph.dc.core.constant.Constants;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by zhengyang on 2019/5/17
 */
public class RecordServer {

    private static final GLog LOG = LogFactory.getLogger(RecordServer.class);

    private final RestService restService = RestFactory.getRestService();

    private String apiUrl = null;
    private ForkJoinPool pool;
    private TaskRedisService taskRedisService;
    private Integer errorMode;

    public RecordServer(String apiUrl, TaskRedisService taskRedisService, Integer errorMode) {
        this.apiUrl = apiUrl;
        this.pool = new ForkJoinPool(Constants.CONCURRENCY);
        this.taskRedisService = taskRedisService;
        this.errorMode = errorMode;
    }

    public List<List<Map<String, Object>>> splitRows(List<Map<String, Object>> totalRows) {
        List<List<Map<String, Object>>> recordList = new LinkedList<>();
        List<Map<String, Object>> rows = null;
        for (Map<String, Object> row : totalRows) {
            if (rows == null) {
                rows = new LinkedList<>();
            }
            rows.add(row);
            if (rows.size() >= Constants.BATCH_SIZE) {
                recordList.add(rows);
                rows = null;
            }
        }
        if (rows != null) {
            recordList.add(rows);
        }
        return recordList;
    }

    public void sendRecords(List<DcInboundDataSuo> dcInboundDataSuoList, Consumer<Integer> errorCountConsumer) {
        this.sendRecords(dcInboundDataSuoList).forEach(resp -> {
            if (!resp.isSuccess()) {
                if (resp.getPayload() != null) {
                    int size = (int) resp.getPayload();
                    errorCountConsumer.accept(size);
                } else {
                    errorCountConsumer.accept(0);
                }
            }
        });
    }

    public Stream<Response> sendRecords(List<DcInboundDataSuo> dcInboundDataSuoList) {
        return dcInboundDataSuoList.parallelStream().map(
                suo -> this.sendRecords(suo)
        );
    }

    public Response sendRecords(DcInboundDataSuo dcInboundDataSuo) {
        try {
            if (taskRedisService.overErrorMode(dcInboundDataSuo)) {
                LOG.warn("send record cancel , over error mode ,taskId={0},recordSize={1}",
                        dcInboundDataSuo.getHeader().getOptions().get(DcConstants.KEY_TASK_ID),
                        dcInboundDataSuo.getRows().size());
                return Response.error();
            }
            Response response = restService.doPost(apiUrl, dcInboundDataSuo, Response.class);
            LOG.audit("send record taskId={0}, recordSize={1},result={2}",
                    dcInboundDataSuo.getHeader().getOptions().get(DcConstants.KEY_TASK_ID),
                    dcInboundDataSuo.getRows().size(), response);
            return response;
        } catch (Exception e) {
            LOG.error(e);
            Response resp = Response.error(e.getMessage());
            resp.setPayload(dcInboundDataSuo.getRows().size());
            return resp;
        }
    }

}
