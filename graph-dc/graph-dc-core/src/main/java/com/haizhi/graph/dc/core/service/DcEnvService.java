package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcEnvPo;
import com.haizhi.graph.dc.core.model.suo.DcEnvSuo;
import com.haizhi.graph.dc.core.model.vo.DcEnvDetailVo;
import com.haizhi.graph.dc.core.model.vo.DcEnvVo;
import com.haizhi.graph.sys.file.model.po.SysDictPo;

import java.util.List;

/**
 * Created by chengangxiong on 2019/03/22
 */
public interface DcEnvService {

    Response<DcEnvVo> findAll();

    Response saveOrUpdate(DcEnvSuo dcEnvSuo);

    Response delete(Long id);

    DcEnvPo findOne(Long id);

    Response<DcEnvDetailVo> findById(Long id);

    Response<List<SysDictPo>> findEnvVersion();
}
