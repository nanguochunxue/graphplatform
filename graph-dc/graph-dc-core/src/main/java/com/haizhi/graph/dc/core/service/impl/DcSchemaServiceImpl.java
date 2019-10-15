package com.haizhi.graph.dc.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.CoreStatus;
import com.haizhi.graph.dc.core.constant.QueryType;
import com.haizhi.graph.dc.core.dao.DcSchemaDao;
import com.haizhi.graph.dc.core.model.po.*;
import com.haizhi.graph.dc.core.model.qo.DcSchemaApiQo;
import com.haizhi.graph.dc.core.model.qo.DcSchemaCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcSchemaQo;
import com.haizhi.graph.dc.core.model.suo.DcSchemaSuo;
import com.haizhi.graph.dc.core.model.vo.DcGraphStoreVo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.model.vo.DcSchemaVo;
import com.haizhi.graph.dc.core.redis.DcPubService;
import com.haizhi.graph.dc.core.service.DcGraphService;
import com.haizhi.graph.dc.core.service.DcSchemaFieldService;
import com.haizhi.graph.dc.core.service.DcSchemaService;
import com.querydsl.core.BooleanBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/10/23.
 */
@Service
public class DcSchemaServiceImpl extends JpaBase implements DcSchemaService {

    private static final GLog LOG = LogFactory.getLogger(DcSchemaServiceImpl.class);

    @Autowired
    private DcSchemaDao dcSchemaDao;

    @Autowired
    private DcSchemaFieldService dcSchemaFieldService;

    @Autowired
    private DcGraphService dcGraphService;

    @Autowired
    private DcPubService dcPubService;

    @Override
    public List<DcSchemaPo> findByGraph(String graph) {
        QDcSchemaPo schemaTable = QDcSchemaPo.dcSchemaPo;
        return jpa.select(schemaTable)
                .from(schemaTable)
                .where(schemaTable.graph.eq(graph)).fetch();
    }

    @Override
    public PageResponse findPage(DcSchemaQo qo) {
        try {
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize(), JQL.SORT.UPDATED_DT_DESC);
            Page<DcSchemaPo> page = dcSchemaDao.findAll(buildBuilder(qo), pageRequest);
            List<DcSchemaVo> voList = page.getContent().stream().map(new Function<DcSchemaPo, DcSchemaVo>() {
                @Override
                public DcSchemaVo apply(DcSchemaPo dcSchemaPo) {
                    return new DcSchemaVo(dcSchemaPo);
                }
            }).collect(Collectors.toList());
            return PageResponse.success(voList, page.getTotalElements(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_PAGE_ERROR, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<DcNameCheckVo> check(DcSchemaCheckQO checkQO) {
        DcNameCheckVo checkVo = new DcNameCheckVo();
        if (checkSchemaExist(checkQO.getGraph(), checkQO.getSchema())) {
            checkVo.setCheckResult(false);
            checkVo.setCheckMsg("schema name is exist!");
        }
        checkVo.setCheckMsg("schema name is valid");
        return Response.success(checkVo);
    }

    @Override
    @Transactional
    public Response saveOrUpdate(DcSchemaSuo suo) {
        try {
            if (StringUtils.isAnyEmpty(suo.getGraph(),suo.getSchema())) {
                LOG.error("graph or schema  is empty: {0}", JSON.toJSONString(suo,true));
                return Response.error("graph or schema name is empty, check the error log!");
            }
            String graph = suo.getGraph();

            // schema name use lower case cause es index name use schema in es6,
            // example: Can not create index which named test.Company in es
            String schemaNewName = suo.getSchema().toLowerCase();
            Long id = suo.getId();
            if(checkSchemaExist(graph, schemaNewName)){
                LOG.error("schema[{0}] of graph[{1}] is exist", schemaNewName, graph);
                return Response.error("schema is exist graph name exist: " + schemaNewName);
            }
            if(Objects.isNull(id)) {
                DcSchemaPo dcSchemaPo = new DcSchemaPo(suo);
                dcSchemaPo = dcSchemaDao.save(dcSchemaPo);
                dcSchemaFieldService.createDefaultSysField(dcSchemaPo);
                return Response.success();
            }
            Date date = new Date();
            DcSchemaPo po = dcSchemaDao.findOne(id);
            String schemaOldName = po.getSchema();
            // update dc_schema
            QDcSchemaPo qDcSchemaPo = QDcSchemaPo.dcSchemaPo;
            BooleanBuilder builderDcSchema = new BooleanBuilder()
                    .and(qDcSchemaPo.graph.eq(graph).and(qDcSchemaPo.schema.eq(schemaOldName)));
            jpa.update(qDcSchemaPo)
                    .set(qDcSchemaPo.schema, schemaNewName)
                    .set(qDcSchemaPo.schemaNameCn, suo.getSchemaNameCn())
                    .set(qDcSchemaPo.searchWeight, suo.getSearchWeight())
                    .set(qDcSchemaPo.useGdb, suo.isUseGdb())
                    .set(qDcSchemaPo.useHBase, suo.isUseHBase())
                    .set(qDcSchemaPo.useSearch, suo.isUseSearch())
                    .set(qDcSchemaPo.remark, suo.getRemark())
                    .set(qDcSchemaPo.updatedDt, date)
                    .where(builderDcSchema)
                    .execute();
            // update dc_schema_field
            QDcSchemaFieldPo qDcSchemaFieldPo = QDcSchemaFieldPo.dcSchemaFieldPo;
            BooleanBuilder builderDcSchemaField = new BooleanBuilder()
                    .and(qDcSchemaFieldPo.graph.eq(graph).and(qDcSchemaFieldPo.schema.eq(schemaOldName)));
            jpa.update(qDcSchemaFieldPo)
                    .set(qDcSchemaFieldPo.schema, schemaNewName)
                    .set(qDcSchemaFieldPo.updatedDt, date)
                    .where(builderDcSchemaField)
                    .execute();

            dcPubService.publish(graph);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_SAVE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        try {
            DcSchemaPo dcSchemaPo = dcSchemaDao.findOne(id);
            if (Objects.isNull(dcSchemaPo)) {
                throw new UnexpectedStatusException(CoreStatus.GRAPH_NOT_EXISTS);
            }
            dcSchemaDao.delete(id);
            dcSchemaFieldService.deleteField(dcSchemaPo.getGraph(), dcSchemaPo.getSchema());
            dcPubService.publish(dcSchemaPo.getGraph());
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_DELETE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<List<SchemaType>> findSchemaTypeList() {
        return Response.success(EnumUtils.getEnumList(SchemaType.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<List<DcSchemaVo>> findAll(DcSchemaApiQo dcSchemaApiQo) {
        QDcSchemaPo table = QDcSchemaPo.dcSchemaPo;
        BooleanBuilder builder = new BooleanBuilder();
        Long graphId = dcSchemaApiQo.getGraphId();
        DcGraphPo graphPo = dcGraphService.findById(graphId);
        if (Objects.isNull(graphPo)) {
            return Response.success();
        }
        if (StringUtils.isNotEmpty(dcSchemaApiQo.getSearch())) {
            builder.and(table.schema.like(JQL.likeWrap(dcSchemaApiQo.getSearch()))
                    .or(table.schemaNameCn.like(JQL.likeWrap(dcSchemaApiQo.getSearch()))));
        }
        if (Objects.nonNull(dcSchemaApiQo.getType())) {
            builder.and(table.type.eq(dcSchemaApiQo.getType()));
        }
        builder.and(table.graph.eq(graphPo.getGraph()));
        Iterable<DcSchemaPo> ite = dcSchemaDao.findAll(builder);
        if (Objects.isNull(ite)) {
            return Response.error("dcSchemaDao findAll failed");
        }
        List<DcSchemaVo> voList = new ArrayList<>();
        ite.forEach(dcSchemaPo -> {
            voList.add(new DcSchemaVo(dcSchemaPo));
        });
        return Response.success(voList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response<List<DcSchemaVo>> findAll(Long graphId) {
        DcGraphPo graphPo = dcGraphService.findById(graphId);
        if (Objects.isNull(graphPo)) {
            return Response.success();
        }
        List<DcSchemaPo> ite = dcSchemaDao.findByGraph(graphPo.getGraph());
        List<DcSchemaVo> voList = new ArrayList<>();
        if (Objects.isNull(ite)) {
            return Response.error("dcSchemaDao findByGraph failed");
        }
        ite.forEach(dcSchemaPo -> voList.add(new DcSchemaVo(dcSchemaPo)));
        return Response.success(voList);
    }

    @Override
    public void deleteByGraph(String graph) {
        List<DcSchemaPo> schemaList = findByGraph(graph);
        if (CollectionUtils.isEmpty(schemaList)) {
            LOG.audit("schemaList is empty");
        } else {
            schemaList.stream().forEach(dcSchemaPo -> {
                        delete(dcSchemaPo.getId());
                        dcSchemaFieldService.delete(dcSchemaPo.getGraph(), dcSchemaPo.getSchema());
                    }
            );
        }
    }

    @Override
    public DcSchemaPo findByGraphAndSchema(String graph, String schema) {
        return dcSchemaDao.findByGraphAndSchema(graph, schema);
    }

    @Override
    public Response findAvailableStore(Long graphId) {
        QDcGraphPo dcGraphPo = QDcGraphPo.dcGraphPo;
        QDcGraphStorePo dcGraphStorePo = QDcGraphStorePo.dcGraphStorePo;
        List<DcGraphStorePo> dcGraphStorePos = jpa.select(dcGraphStorePo)
                .from(dcGraphPo)
                .leftJoin(dcGraphStorePo).on(dcGraphPo.graph.eq(dcGraphStorePo.graph))
                .where(dcGraphPo.id.eq(graphId)).fetch();
        List<StoreType> availableStore = new ArrayList<>();
        if (Objects.isNull(dcGraphStorePos)) {
            return Response.error("dcGraphStorePos failed");
        }
        dcGraphStorePos.stream().forEach(graphStorePo -> {
            if (Objects.nonNull(graphStorePo)) {
                availableStore.add(graphStorePo.getStoreType());
            }
        });
        return Response.success(ImmutableMap.of(StoreType.ES, availableStore.contains(StoreType.ES),
                StoreType.GDB, availableStore.contains(StoreType.GDB),
                StoreType.Hbase, availableStore.contains(StoreType.Hbase)));
    }

    @Override
    public DcGraphStoreVo countByGraph(String graph) {
        List<DcSchemaPo> schemaPos = dcSchemaDao.findByGraph(graph);
        DcGraphStoreVo graphStoreVo = new DcGraphStoreVo();
        if (CollectionUtils.isEmpty(schemaPos)) {
            return graphStoreVo;
        }
        schemaPos.forEach(dcSchemaPo -> {
            graphStoreVo.setEsCanNull(graphStoreVo.isEsCanNull() && !dcSchemaPo.isUseSearch());
            graphStoreVo.setGdbCanNull(graphStoreVo.isGdbCanNull() && !dcSchemaPo.isUseGdb());
            graphStoreVo.setHbaseCanNull(graphStoreVo.isHbaseCanNull() && !dcSchemaPo.isUseHBase());
        });
        return graphStoreVo;
    }

    @Override
    public Long findGraphIdBySchemaId(Long schemaId) {
        QDcGraphPo dcGraphPo = QDcGraphPo.dcGraphPo;
        QDcSchemaPo dcSchemaPo = QDcSchemaPo.dcSchemaPo;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(dcSchemaPo.id.eq(schemaId));
        return jpa.select(dcGraphPo.id)
                .from(dcGraphPo)
                .leftJoin(dcSchemaPo).on(dcGraphPo.graph.eq(dcSchemaPo.graph))
                .where(builder)
                .fetchOne();
    }

    private BooleanBuilder buildBuilder(DcSchemaQo qo) {
        QDcSchemaPo table = QDcSchemaPo.dcSchemaPo;
        BooleanBuilder builder = new BooleanBuilder();
        // set query condition by query type
        if (StringUtils.isNotEmpty(qo.getSearch())) {
            if (qo.getQueryType() == null || qo.getQueryType() == QueryType.ALL) {
                builder.and(table.schema.like(JQL.likeWrap(qo.getSearch())).or(table.schemaNameCn.like(JQL.likeWrap(qo.getSearch()))));
            } else if (qo.getQueryType() == QueryType.CH) {
                builder.and(table.schemaNameCn.like(JQL.likeWrap(qo.getSearch())));
            } else if (qo.getQueryType() == QueryType.EN) {
                builder.and(table.schema.like(JQL.likeWrap(qo.getSearch())));
            }
        }
        if (Objects.nonNull(qo.getType())) {
            builder.and(table.type.eq(qo.getType()));
        }
        if (Objects.nonNull(qo.getGraph())) {
            builder.and(table.graph.eq(qo.getGraph()));
        }
        return builder;
    }

//    private void checkSchemaNameUnique(Long id, String graph, String schema) {
//        DcSchemaPo sameSchema;
//        if (Objects.isNull(id)) {
//            sameSchema = dcSchemaDao.findByGraphAndSchema(graph, schema);
//        } else {
//            sameSchema = dcSchemaDao.findByIdIsNotAndGraphAndSchema(id, graph, schema);
//        }
//        if (Objects.nonNull(sameSchema)) {
//            throw new UnexpectedStatusException(CoreStatus.SCHEMA_NAME_EXISTS);
//        }
//    }

    private boolean checkSchemaExist(String graph, String schema) {
        if (StringUtils.isAnyEmpty(graph, schema)) {
            LOG.audit("graph[{0}] or schema[{1}] is null", graph, schema);
            throw new UnexpectedStatusException(CoreStatus.GRAPH_SCHEMA_IS_NULL);
        }
        QDcSchemaPo dcSchemaPo = QDcSchemaPo.dcSchemaPo;
        List<String> schemaList = jpa.select(dcSchemaPo.schema)
                .from(dcSchemaPo)
                .where(dcSchemaPo.graph.eq(graph.toLowerCase()))
                .fetch();
        if (Objects.nonNull(schemaList)) {
            for (String existSchema : schemaList) {
                if (StringUtils.equals(existSchema.toLowerCase(), schema.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

}