package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.common.redis.key.RKeys;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengmo on 2018/8/16.
 */
@Transactional
@Repository
public interface DcGraphDao extends JpaRepo<DcGraphPo> {

//    @Cacheable(value = RKeys.DC_GRAPH, key = "#a0")
    DcGraphPo findByGraph(String graph);

    List<DcGraphPo> findByGraphLike(String graph);

    List<DcGraphPo> findByGraphNameCnLike(String graphNameCn);

    List<DcGraphPo> findByGraphNameCn(String graphNameCn);

    List<DcGraphPo> findByGraphLikeOrGraphNameCnLike(String graph, String graphNameCn);

    List<DcGraphPo> findByGraphOrGraphNameCn(String graph, String graphNameCn);

    DcGraphPo findByGraphAndIdIsNot(String graph, Long id);

    DcGraphPo findOne(Long id);

//    @Cacheable(cacheNames = RKeys.DC_GRAPH, key = "#a0.graph")
    DcGraphPo save(DcGraphPo dcGraphPo);
}
