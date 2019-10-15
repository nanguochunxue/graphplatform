package com.haizhi.graph.dc.inbound.service.impl;

import com.haizhi.graph.dc.inbound.dao.DcTaskMetaDao;
import com.haizhi.graph.dc.core.model.po.DcTaskMetaPo;
import com.haizhi.graph.dc.core.model.suo.DcTaskMetaSuo;
import com.haizhi.graph.dc.core.model.vo.DcTaskMetaVo;
import com.haizhi.graph.dc.inbound.service.DcTaskMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by chengangxiong on 2019/04/23
 */
@Service
public class DcTaskMetaServiceImpl implements DcTaskMetaService {

    @Autowired
    private DcTaskMetaDao dcTaskMetaDao;

    @Override
    public void save(List<DcTaskMetaSuo> dcTaskMetaSuos, Long taskId) {
        List<DcTaskMetaPo> poList = dcTaskMetaSuos.stream().map(dcTaskMetaSuo -> {
            DcTaskMetaPo metaPo = new DcTaskMetaPo(dcTaskMetaSuo);
            metaPo.setTaskId(taskId);
            return metaPo;
        }).collect(Collectors.toList());
        dcTaskMetaDao.deleteByTaskId(taskId);
        dcTaskMetaDao.save(poList);
    }

    @Override
    public List<DcTaskMetaVo> findByTaskId(Long taskId) {
        List<DcTaskMetaPo> poList = dcTaskMetaDao.findAllByTaskId(taskId);
        List<DcTaskMetaVo> voList = new ArrayList<>();
        if (Objects.nonNull(poList) && !poList.isEmpty()){
            voList = poList.stream().map(dcTaskMetaPo -> new DcTaskMetaVo(dcTaskMetaPo)).collect(Collectors.toList());
        }
        return voList;
    }

    @Override
    public List<DcTaskMetaPo> findAllByTaskId(Long taskId) {
        return dcTaskMetaDao.findAllByTaskId(taskId);
    }
}
