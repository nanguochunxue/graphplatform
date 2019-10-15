package com.haizhi.graph.server.es.client.rest.http;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.CudResponse;
import org.elasticsearch.client.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/11/8.
 */
public class RestResponses {

    private static final GLog LOG = LogFactory.getLogger(RestResponses.class);
    private static final int MAX_BULK_ERROR_MESSAGES = 5;

    /**
     * @param resp
     * @return
     */
    public static CudResponse processBulkResponse(Response resp, CudResponse cudResponse) {
        int rowsAffected = 0;
        int rowsErrors = 0;
        try {
            Map<String, Object> body = JSON.parseObject(resp.getEntity().getContent(), Map.class);
            List<String> errorMessageSample = new ArrayList<>(MAX_BULK_ERROR_MESSAGES);
            int errorMessagesSoFar = 0;
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            for (Map<String, Object> item : items) {
                Map values = (Map) item.values().iterator().next();
                Integer status = (Integer) values.get("status");
                String error = extractError(values);
                if (error != null && !error.isEmpty()) {
                    rowsErrors++;
                    if ((status != null && HttpStatus.canRetry(status))
                            || error.contains("EsRejectedExecutionException")) {
                        if (errorMessagesSoFar < MAX_BULK_ERROR_MESSAGES) {
                            errorMessageSample.add(error);
                            errorMessagesSoFar++;
                        }
                    } else {
                        String message = (status != null ?
                                String.format("[%s] returned %s(%s) - %s",
                                        resp.getHost(),
                                        HttpStatus.getText(status),
                                        status,
                                        prettify(error))
                                : prettify(error));
                        cudResponse.setMessage(String.format("Found unrecoverable error %s; Bailing out..",
                                message));
                    }
                } else {
                    rowsAffected++;
                }
            }
        } catch (IOException ex) {
            cudResponse.setMessage(ex.getMessage());
            LOG.error(ex);
        } finally {
            cudResponse.setResult(rowsAffected, rowsErrors);
        }
        return cudResponse;
    }

    private static String prettify(String error) {
        String invalidFragment = ErrorUtils.extractInvalidXContent(error);
        String header = (invalidFragment != null ? "Invalid JSON fragment received[" + invalidFragment + "]" : "");
        return header + "[" + error + "]";
    }

    private static String extractError(Map jsonMap) {
        Object err = jsonMap.get("error");
        String error = "";
        if (err != null) {
            // part of ES 2.0
            if (err instanceof Map) {
                Map m = ((Map) err);
                err = m.get("root_cause");
                if (err == null) {
                    if (m.containsKey("reason")) {
                        error = m.get("reason").toString();
                    } else if (m.containsKey("caused_by")) {
                        error += ";" + ((Map) m.get("caused_by")).get("reason");
                    } else {
                        error = m.toString();
                    }
                } else {
                    if (err instanceof List) {
                        Object nested = ((List) err).get(0);
                        if (nested instanceof Map) {
                            Map nestedM = (Map) nested;
                            if (nestedM.containsKey("reason")) {
                                error = nestedM.get("reason").toString();
                            } else {
                                error = nested.toString();
                            }
                        } else {
                            error = nested.toString();
                        }
                    } else {
                        error = err.toString();
                    }
                }
            } else {
                error = err.toString();
            }
        }
        return error;
    }
}
