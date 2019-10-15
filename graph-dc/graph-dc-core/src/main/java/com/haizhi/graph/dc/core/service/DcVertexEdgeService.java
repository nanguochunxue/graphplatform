package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
import com.haizhi.graph.dc.core.model.qo.DcVertexEdgeQo;
import com.haizhi.graph.dc.core.model.suo.DcVertexEdgeSuo;
import com.haizhi.graph.dc.core.model.vo.DcVertexEdgeVo;

import java.util.List;
import java.util.Set;

/**
 * Created by chengangxiong on 2019/01/03
 */
public interface DcVertexEdgeService {

    PageResponse findPage(DcVertexEdgeQo dcVertexEdgeQo);

    Response<List<DcVertexEdgeVo>> find(DcVertexEdgeQo dcVertexEdgeQo);

    Response<Set<String>> findCollections(DcVertexEdgeQo dcVertexEdgeQo);

    Response saveOrUpdate(DcVertexEdgeSuo dcVertexEdgeSuo);

    Response delete(DcVertexEdgeSuo dcVertexEdgeSuo);

    List<DcVertexEdgePo> findByKeyIn(List<String> keys);

    List<DcVertexEdgePo> save(List<DcVertexEdgePo> edgePos);

    DcVertexEdgePo save(DcVertexEdgePo edgePo);
}
