package com.haizhi.graph.dc.core.service.impl;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.CoreStatus;
import com.haizhi.graph.dc.core.dao.DcGraphStoreDao;
import com.haizhi.graph.dc.core.model.po.DcGraphStorePo;
import com.haizhi.graph.dc.core.model.suo.DcGraphStoreSuo;
import com.haizhi.graph.dc.core.service.DcGraphStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengangxiong on 2019/01/03
 */
@Service
public class DcGraphStoreServiceImpl implements DcGraphStoreService {

    @Autowired
    private DcGraphStoreDao dcGraphStoreDao;

    @Override
    public Map<StoreType, DcGraphStorePo> findByGraph(String graph) {
        Map<StoreType, DcGraphStorePo> storePoMap = new HashMap<>();
        List<DcGraphStorePo> list = dcGraphStoreDao.findByGraph(graph);
        list.stream().forEach(dcGraphStorePo -> storePoMap.put(dcGraphStorePo.getStoreType(), dcGraphStorePo));
        return storePoMap;
    }

    @Override
    public Response saveOrUpdate(DcGraphStoreSuo suo) {
        checkGraphAndStoreTypeUnique(suo.getGraph(), suo.getStoreId(), suo.getStoreType(), suo.getId());
        DcGraphStorePo po = new DcGraphStorePo(suo);
        try {
            dcGraphStoreDao.save(po);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.STORE_SAVE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public DcGraphStorePo findByGraphAndStoreIdAndStoreType(String graph, Long storeId, StoreType storeType) {
        return dcGraphStoreDao.findByGraphAndStoreIdAndStoreType(graph, storeId, storeType);
    }

    @Override
    public DcGraphStorePo findByGraphAndStoreType(String graph, StoreType storeType) {
        return dcGraphStoreDao.findByGraphAndStoreType(graph, storeType);
    }

    private void checkGraphAndStoreTypeUnique(String graph, Long storeId, StoreType storeType, Long id) {
        DcGraphStorePo dcGraphStorePo;
        if (Objects.isNull(id)) {
            dcGraphStorePo = dcGraphStoreDao.findByGraphAndStoreIdAndStoreType(graph, storeId, storeType);
        } else {
            dcGraphStorePo = dcGraphStoreDao.findByIdIsNotAndGraphAndStoreIdAndStoreType(id, graph, storeId, storeType);
        }
        if (Objects.nonNull(dcGraphStorePo)) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_NAME_EXISTS);
        }
    }
}
