package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.model.qo.DcAuthorizedFieldQo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldApiQo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldMainCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcSchemaFieldQo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaFieldSuo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.model.vo.DcSchemaFieldVo;

import java.util.List;

/**
 * Created by chengangxiong on 2019/01/03
 */
public interface DcSchemaFieldService {

    List<DcSchemaFieldPo> findByGraph(String graph);

    PageResponse findPage(DcSchemaFieldQo dcSchemaFieldQo);

    PageResponse<DcSchemaFieldVo> findAuthorizedFieldPage(DcAuthorizedFieldQo qo);

    Response<List<String>> findDistinctByFieldByLike(String fieldName);

    Response<DcNameCheckVo> check(DcSchemaFieldCheckQO checkQO);

    Response saveOrUpdate(DcSchemaFieldSuo dcSchemaFieldSuo);

    void createDefaultSysField(DcSchemaPo dcSchemaPo);

    Response delete(Long id);

    void deleteField(String graph, String schema);

    Response<Boolean> checkHasMainField(DcSchemaFieldMainCheckQO checkQO);

    void delete(String graph, String schema);

    Response<List<DcSchemaFieldVo>> findAll(DcSchemaFieldApiQo dcSchemaFieldApiQo);

    List<DcSchemaFieldPo> findByGraphAndSchema(String graph, String schema);
}
