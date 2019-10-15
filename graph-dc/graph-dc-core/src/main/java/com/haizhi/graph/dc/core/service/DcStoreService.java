package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcStorePo;
import com.haizhi.graph.dc.core.model.qo.DcNameCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcStoreQo;
import com.haizhi.graph.dc.core.model.suo.DcStoreSuo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.model.vo.DcStorePageVo;
import com.haizhi.graph.dc.core.model.vo.DcStoreSelectorVo;
import com.haizhi.graph.dc.core.model.vo.DcStoreVo;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/03
 */
public interface DcStoreService {

    PageResponse findPage(DcStoreQo dcStoreQo);

    Response<List<String>> findStoreTypeList();

    List<DcStorePo> findByGraph(String graph);

    List<DcStoreVo> findVoByGraph(String graph);

    Response saveOrUpdate(DcStoreSuo dcStoreSuo);

    Response<DcNameCheckVo> check(DcNameCheckQO dcNameCheckQO);

    Response delete(Long id);

    Response<List<DcStorePageVo>> find(DcStoreQo dcStoreQo);

    Response<List<DcStoreSelectorVo>> findAll(StoreType storeType);

    Response<DcStoreVo> findById(Long id);

    DcStorePo findByNameAndType(String name, StoreType storeType);
}
