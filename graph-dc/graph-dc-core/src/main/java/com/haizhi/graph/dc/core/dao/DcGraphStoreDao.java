package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcGraphStorePo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Created by chengmo on 2018/8/16.
 */
@Transactional
@Repository
public interface DcGraphStoreDao extends JpaRepo<DcGraphStorePo> {

    List<DcGraphStorePo> findByGraph(String graph);

    List<DcGraphStorePo> findByGraphLike(String graph);

    void deleteByGraph(String graph);

    List<DcGraphStorePo> findByStoreId(Long storeId);

    List<DcGraphStorePo> findByStoreIdIn(Collection<Long> storeIds);

    DcGraphStorePo findByGraphAndStoreIdAndStoreType(String graph, Long storeId, StoreType storeType);

    DcGraphStorePo findByIdIsNotAndGraphAndStoreIdAndStoreType(Long id, String graph, Long storeId, StoreType storeType);

    DcGraphStorePo findByGraphAndStoreType(String graph, StoreType storeType);
}
