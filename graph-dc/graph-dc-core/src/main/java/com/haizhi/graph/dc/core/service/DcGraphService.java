package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import com.haizhi.graph.dc.core.model.qo.DcGraphCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcGraphIdsQo;
import com.haizhi.graph.dc.core.model.qo.DcGraphQo;
import com.haizhi.graph.dc.core.model.suo.DcGraphSuo;
import com.haizhi.graph.dc.core.model.vo.DcGraphDetailVo;
import com.haizhi.graph.dc.core.model.vo.DcGraphFrameVo;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/03
 */
public interface DcGraphService {

    PageResponse findPage(DcGraphQo dcGraphQo);

    Response<List<DcGraphFrameVo>> findGraphFrame();

    Response findByIds(DcGraphIdsQo ids);

    Response findAll();

    Response saveOrUpdate(DcGraphSuo dcGraphSuo);

    Response delete(Long id);

    Response check(DcGraphCheckQO graphNameCheckQO);

    Response<DcGraphDetailVo> findDetailById(Long id);

    DcGraphPo findById(Long id);

    DcGraphPo findByByGraph(String graph);
}
