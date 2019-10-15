package com.haizhi.graph.dc.core.service;

import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaApiQo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcSchemaQo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaSuo;
import com.haizhi.graph.dc.core.model.vo.DcGraphStoreVo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.model.vo.DcSchemaVo;

import java.util.List;
import java.util.Map;

/**
 * Created by chengangxiong on 2019/01/03
 */
public interface DcSchemaService {

    List<DcSchemaPo> findByGraph(String graph);

    PageResponse findPage(DcSchemaQo dcSchemaQo);

    Response<DcNameCheckVo> check(DcSchemaCheckQO checkQO);

    Response saveOrUpdate(DcSchemaSuo dcSchemaSuo);

    Response delete(Long id);

    Response<List<SchemaType>> findSchemaTypeList();

    Response<List<DcSchemaVo>> findAll(DcSchemaApiQo dcSchemaApiQo);

    Response<List<DcSchemaVo>> findAll(Long graphId);

    void deleteByGraph(String graph);

    DcSchemaPo findByGraphAndSchema(String graph, String schema);

    Response findAvailableStore(Long graphId);

    DcGraphStoreVo countByGraph(String graph);

    Long findGraphIdBySchemaId(Long schemaId);
}
