package com.haizhi.graph.dc.core.dao;

import com.haizhi.graph.common.core.jpa.JpaRepo;
import com.haizhi.graph.dc.core.model.po.DcStoreParamPo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chengangxiong on 2019/05/05
 */
@Transactional
@Repository
public interface DcStoreParamDao extends JpaRepo<DcStoreParamPo> {

    List<DcStoreParamPo> findByStoreId(Long storeId);

    void deleteByStoreId(Long storeId);
}
