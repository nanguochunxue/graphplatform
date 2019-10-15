package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengmo on 2018/8/16.
 */
@Transactional
@Repository
public interface DcVertexEdgeDao extends JpaRepo<DcVertexEdgePo> {

    List<DcVertexEdgePo> findByGraph(String graph);

    List<DcVertexEdgePo> findByGraphLike(String graph);

    List<DcVertexEdgePo> findByKeyIn(List<String> keys);
}
