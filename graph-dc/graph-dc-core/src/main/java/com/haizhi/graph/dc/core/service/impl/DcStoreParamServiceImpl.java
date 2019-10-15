package com.haizhi.graph.dc.core.service.impl;

import com.haizhi.graph.dc.core.constant.CoreConstant;
import com.haizhi.graph.dc.core.dao.DcStoreParamDao;
import com.haizhi.graph.dc.core.model.po.DcStoreParamPo;
import com.haizhi.graph.dc.core.service.DcStoreParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chengangxiong on 2019/05/05
 */
@Slf4j
@Service
public class DcStoreParamServiceImpl implements DcStoreParamService {

    @Autowired
    private DcStoreParamDao dcStoreParamDao;

    @Override
    public String findGPExportUrl(Long storeId) {
        List<DcStoreParamPo> dcStoreParamList = dcStoreParamDao.findByStoreId(storeId);
        for (DcStoreParamPo paramPo : dcStoreParamList){
            if (paramPo.getKey().equals(CoreConstant.GP_EXPORT_URL)){
                return paramPo.getValue();
            }
        }
        return null;
    }
}
