package com.haizhi.graph.plugins.etl.gp.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.common.model.plugins.etl.gp.EtlGreenPlumQo;
import com.haizhi.graph.common.redis.RedisService;
import com.haizhi.graph.common.redis.key.RKeys;
import com.haizhi.graph.plugins.etl.gp.constant.GpStatus;
import com.haizhi.graph.plugins.etl.gp.service.GreenPlumService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by chengmo on 2019/4/15.
 */
@Service
public class GreenPlumServiceImpl implements GreenPlumService {

    private static final GLog LOG = LogFactory.getLogger(GreenPlumServiceImpl.class);

    @Value("${graph.etl.gp.perlParams:}")
    private String perlScriptParams;

    @Value("${graph.etl.gp.perlPath:}")
    private String perlScriptPath;

    @Value("${graph.etl.gp.exportDir:}")
    private String exportDir;

    @Value("${graph.etl.gp.executor.pool.size:10}")
    private Integer poolSize;

    @Autowired
    private RedisService redisService;

    private ExecutorService executor;

    @PostConstruct
    public void postConstruct() {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    @Override
    public Response startExport(EtlGreenPlumQo greenPlumOo) {
        executor.execute(() -> {
            doExecuteExport(greenPlumOo);
        });
        return Response.success();
    }

    private void doExecuteExport(EtlGreenPlumQo greenPlumOo) {
        Path path = Paths.get(exportDir, "export-" + UUID.randomUUID().toString() + ".csv");
        String outputFile = path.toString();
        String perlPath;
        int exitCode = -1;
        File file = new File(perlScriptPath);
        if (file.exists()) {
            perlPath = perlScriptPath;
        } else {
            perlPath = Resource.getResourcePath(perlScriptPath);
            if (StringUtils.isBlank(perlPath)) {
                throw new UnexpectedStatusException(GpStatus.PERL_SCRIPT_NOT_FOUND);
            }
        }
        List<String> commandList = new ArrayList<>();
        commandList.add("perl");
        commandList.add(perlPath);
        String formatParams = greenPlumOo.format(perlScriptParams);
        commandList.addAll(Splitter.on(" ").splitToList(formatParams));
        commandList.add("-outfile=" + outputFile);
        Response response = Response.success();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder().command(commandList);
            Process process = processBuilder.start();
            AtomicBoolean finishFlag = new AtomicBoolean(false);
            Thread[] threads = startOutputThread(process, finishFlag);
            exitCode = process.waitFor();
            LOG.info("sub process finished with exitCode : " + exitCode);
            finishFlag.set(true);
            Arrays.asList(threads).stream().forEach(thread -> thread.interrupt());
        } catch (Exception e) {
            LOG.error("", e);
            response.setMessage(e.getMessage());
        }

        Map<String, Object> respData = new HashMap<>();
        respData.put("exitCode", exitCode);
        respData.put("outputFile", outputFile);
        response.setData(respData);
        sendResponseToRedis(response, greenPlumOo);
    }

    private void sendResponseToRedis(Response response, EtlGreenPlumQo greenPlumOo) {
        String redisKey = RKeys.DC_TASK_GRAPH_ETL_GP + ":" + greenPlumOo.getTaskInstanceId();
        boolean success = redisService.put(redisKey, response);
        LOG.info("send response to redis, success={0}, redisKey={1}, response:\n{2}", success, redisKey,
                JSON.toJSONString(response, true));
    }

    private Thread[] startOutputThread(Process process, AtomicBoolean finishFlag) {
        Thread stdoutThread = new Thread(() -> {
            String s = null;
            BufferedReader stdout = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            while (true & !finishFlag.get()) {
                try {
                    if (!((s = stdout.readLine()) != null)) break;
                } catch (IOException e) {
                    LOG.warn("", e);
                }
                LOG.info("info | " + s);
            }
        });
        stdoutThread.start();
        Thread errorStdoutThread = new Thread(() -> {
            String error = null;
            BufferedReader errorStdout = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while (true & !finishFlag.get()) {
                try {
                    if (!((error = errorStdout.readLine()) != null)) break;
                } catch (IOException e) {
                    LOG.warn("", e);
                }
                LOG.error("error | " + error);
            }
        });
        errorStdoutThread.start();
        return new Thread[]{stdoutThread, errorStdoutThread};
    }

    @Override
    public Response getExportProgress(EtlGreenPlumQo greenPlumOo) {
        return Response.success();
    }
}