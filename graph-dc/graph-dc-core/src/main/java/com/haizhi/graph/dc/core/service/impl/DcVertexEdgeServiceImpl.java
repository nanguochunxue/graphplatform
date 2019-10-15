package com.haizhi.graph.dc.core.service.impl;

import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.CoreStatus;
import com.haizhi.graph.dc.core.dao.DcVertexEdgeDao;
import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
import com.haizhi.graph.dc.core.model.po.QDcVertexEdgePo;
import com.haizhi.graph.dc.core.model.qo.DcVertexEdgeQo;
import com.haizhi.graph.dc.core.model.suo.DcVertexEdgeSuo;
import com.haizhi.graph.dc.core.model.vo.DcVertexEdgeVo;
import com.haizhi.graph.dc.core.service.DcVertexEdgeService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chengangxiong on 2019/01/08
 */
@Service
public class DcVertexEdgeServiceImpl extends JpaBase implements DcVertexEdgeService {

    @Autowired
    private DcVertexEdgeDao dcVertexEdgeDao;

    @Override
    public PageResponse findPage(DcVertexEdgeQo qo) {
        try {
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize(), JQL.SORT.UPDATED_DT_DESC);
            Page<DcVertexEdgePo> page = dcVertexEdgeDao.findAll(pageRequest);
            return PageResponse.success(page.getContent(), page.getTotalPages(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.VERTEX_EDGE_PAGE_ERROR, e);
        }
    }

    @Override
    public Response<List<DcVertexEdgeVo>> find(DcVertexEdgeQo qo) {
        try {
            QDcVertexEdgePo table = QDcVertexEdgePo.dcVertexEdgePo;
            BooleanBuilder builder = new BooleanBuilder();
            if (!StringUtils.isEmpty(qo.getEdge())) {
                builder = builder.and(table.edge.like(JQL.likeWrap(qo.getEdge())));
            }
            Iterable<DcVertexEdgePo> results = dcVertexEdgeDao.findAll(builder);
            List<DcVertexEdgeVo> rows = new ArrayList<>();
            results.forEach((po) -> {
                rows.add(new DcVertexEdgeVo(po));
            });
            return Response.success(rows);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.VERTEX_EDGE_FIND_ERROR, e);
        }
    }

    @Override
    public Response<Set<String>> findCollections(DcVertexEdgeQo qo) {
        try {
            String graph = qo.getGraph();
            Set<String> edges = qo.getEdges();
            QDcVertexEdgePo qDcVertexEdgePo = QDcVertexEdgePo.dcVertexEdgePo;
            JPAQuery<DcVertexEdgePo> jpaQuery = jpa.select(qDcVertexEdgePo)
                    .from(qDcVertexEdgePo)
                    .where(qDcVertexEdgePo.graph.eq(graph));
            if (Objects.nonNull(edges) && !edges.isEmpty()) {
                jpaQuery.where(qDcVertexEdgePo.edge.in(edges));
            }
            List<DcVertexEdgePo> jpaResultList = jpaQuery.fetch();
            Set<String> result = new HashSet<>();
            jpaResultList.forEach(po -> {
                String fromVertex = po.getFromVertex();
                String toVertex = po.getToVertex();
                if (Objects.nonNull(fromVertex) && !fromVertex.isEmpty()) {
                    result.add(po.getFromVertex());
                }
                if (Objects.nonNull(toVertex) && !toVertex.isEmpty()) {
                    result.add(toVertex);
                }
            });
            return Response.success(result);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.VERTEX_EDGE_FIND_COLLECTIONS_ERROR, e);
        }
    }

    @Override
    public Response saveOrUpdate(DcVertexEdgeSuo suo) {
        try {
            DcVertexEdgePo dcVertexEdgePo = new DcVertexEdgePo(suo);
            dcVertexEdgeDao.save(dcVertexEdgePo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.VERTEX_EDGE_SAVE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public Response delete(DcVertexEdgeSuo suo) {
        try {
            dcVertexEdgeDao.delete(suo.getId());
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.VERTEX_EDGE_DELETE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public List<DcVertexEdgePo> findByKeyIn(List<String> keys) {
        return dcVertexEdgeDao.findByKeyIn(keys);
    }

    @Override
    public List<DcVertexEdgePo> save(List<DcVertexEdgePo> edgePos) {
        return dcVertexEdgeDao.save(edgePos);
    }

    @Override
    public DcVertexEdgePo save(DcVertexEdgePo edgePo) {
        return dcVertexEdgeDao.save(edgePo);
    }
}
