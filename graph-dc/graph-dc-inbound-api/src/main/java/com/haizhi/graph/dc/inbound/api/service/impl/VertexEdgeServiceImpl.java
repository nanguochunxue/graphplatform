package com.haizhi.graph.dc.inbound.api.service.impl;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
import com.haizhi.graph.dc.core.redis.DcPubService;
import com.haizhi.graph.dc.core.service.DcVertexEdgeService;
import com.haizhi.graph.dc.inbound.api.service.VertexEdgeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Create by zhoumingbing on 2019-06-20
 */
@Service
public class VertexEdgeServiceImpl implements VertexEdgeService {
    private static final GLog LOG = LogFactory.getLogger(VertexEdgeServiceImpl.class);

    private static Map<String, DcVertexEdgePo> edgeVertexQueue = new ConcurrentHashMap<>();

    @Autowired
    private DcVertexEdgeService dcVertexEdgeService;

    @Autowired
    private DcPubService dcPubService;

    /**
     * unit: seconds
     */
    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void transfer() {
        try {
            if (!edgeVertexQueue.isEmpty()) {
                doTransfer();
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (!edgeVertexQueue.isEmpty()) {
            doTransfer();
        }
    }

    @Override
    public void addVertexEdge(DcVertexEdgePo edgePo) {
        try {
            if (!validateEdge(edgePo)) {
                return;
            }
            LOG.info("add vertex edge,graph={0},toVertex={1},fromVertex={2} ",
                    edgePo.getGraph(), edgePo.getToVertex(), edgePo.getFromVertex());
            edgeVertexQueue.put(getEdgeKey(edgePo), edgePo);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private void doTransfer() {
        List<DcVertexEdgePo> edgePoList = getAndClearQueue();
        List<DcVertexEdgePo> legalEdgePoList = edgePoList.stream().filter(this::validateEdge)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(legalEdgePoList)) {
            return;
        }
        List<String> keys = legalEdgePoList.stream().map(edgePo -> getEdgeKey(edgePo))
                .collect(Collectors.toList());
        List<DcVertexEdgePo> dcVertexEdgePoList = dcVertexEdgeService.findByKeyIn(keys);
        if (CollectionUtils.isNotEmpty(dcVertexEdgePoList)) {
            Set<String> existKeys = dcVertexEdgePoList.stream()
                    .map(DcVertexEdgePo::getKey).collect(Collectors.toSet());
            legalEdgePoList = legalEdgePoList.stream()
                    .filter(edgePo -> !existKeys.contains(edgePo.getKey()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(legalEdgePoList)) {
            boolean result = batchSaveEdgePos(legalEdgePoList);
            if (!result) {
                legalEdgePoList.forEach(edgePo -> saveEdgePo(edgePo));
            }
            Set<String> set = new HashSet<>();
            for (DcVertexEdgePo vertexEdgePo : legalEdgePoList) {
                if (set.add(vertexEdgePo.getGraph())) {
                    dcPubService.publish(vertexEdgePo.getGraph());
                }
            }
        }
    }

    private String getEdgeKey(DcVertexEdgePo edgePo) {
        if (StringUtils.isBlank(edgePo.getKey())) {
            edgePo.setKey(edgePo.getMd5Key());
        }
        return edgePo.getKey();
    }

    private boolean validateEdge(DcVertexEdgePo edgePo) {
        if (StringUtils.isAnyBlank(edgePo.getGraph(), edgePo.getFromVertex(), edgePo.getToVertex(), edgePo.getEdge())) {
            LOG.info("edgePo is illegal,graph={0},fromVertex={1},toVertex={2},edge={3}",
                    edgePo.getGraph(), edgePo.getFromVertex(), edgePo.getToVertex(), edgePo.getEdge());
            return false;
        }
        return true;
    }

    private List<DcVertexEdgePo> getAndClearQueue() {
        synchronized (VertexEdgeServiceImpl.class) {
            Collection<DcVertexEdgePo> values = edgeVertexQueue.values();
            List<DcVertexEdgePo> target = new ArrayList<>(values);
            edgeVertexQueue.clear();
            return target;
        }
    }

    private boolean batchSaveEdgePos(List<DcVertexEdgePo> edgePos) {
        try {
            dcVertexEdgeService.save(edgePos);
            return true;
        } catch (Exception e) {
            LOG.error("batch save vertex_edge fail", e);
        }
        return false;
    }

    private boolean saveEdgePo(DcVertexEdgePo edgePo) {
        try {
            dcVertexEdgeService.save(edgePo);
            return true;
        } catch (Exception e) {
            LOG.error("save vertex_edge fail", e);
        }
        return false;
    }


}
