package com.haizhi.graph.dc.inbound.service;

import com.haizhi.graph.dc.core.model.po.DcTaskMetaPo;
import com.haizhi.graph.dc.core.model.suo.DcTaskMetaSuo;
import com.haizhi.graph.dc.core.model.vo.DcTaskMetaVo;

import java.util.List;

/**
 * Created by chengangxiong on 2019/04/23
 */
public interface DcTaskMetaService {

    void save(List<DcTaskMetaSuo> dcTaskMetaSuos, Long taskId);

    List<DcTaskMetaVo> findByTaskId(Long taskId);

    List<DcTaskMetaPo> findAllByTaskId(Long taskId);
}
