package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengmo on 2018/8/16.
 */
@Transactional
@Repository
public interface DcStoreDao extends JpaRepo<DcStorePo> {

    List<DcStorePo> findByType(String type);

    List<DcStorePo> findByNameLike(String name);

    List<DcStorePo> findByType(StoreType storeType);

    List<DcStorePo> findByEnvId(Long envId);

    DcStorePo findByName(String name);

    DcStorePo findByNameAndIdIsNot(String name, Long id);

    DcStorePo findByNameAndType(String name, StoreType storeType);
}
