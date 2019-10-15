package com.haizhi.graph.dc.core.service.impl;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.core.jpa.JQL;
import com.haizhi.graph.common.core.jpa.JpaBase;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.model.PageQo;
import com.haizhi.graph.common.model.PageResponse;
import com.haizhi.graph.common.model.Response;
import com.haizhi.graph.dc.core.constant.CoreStatus;
import com.haizhi.graph.dc.core.constant.QueryType;
import com.haizhi.graph.dc.core.dao.DcGraphDao;
import com.haizhi.graph.dc.core.dao.DcSchemaDao;
import com.haizhi.graph.dc.core.dao.DcSchemaFieldDao;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.model.po.QDcSchemaFieldPo;
import com.haizhi.graph.dc.core.model.qo.*;
import com.haizhi.graph.dc.core.model.suo.DcSchemaFieldSuo;
import com.haizhi.graph.dc.core.model.vo.DcNameCheckVo;
import com.haizhi.graph.dc.core.model.vo.DcSchemaFieldVo;
import com.haizhi.graph.dc.core.redis.DcPubService;
import com.haizhi.graph.dc.core.service.DcSchemaFieldService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by chengangxiong on 2019/01/04
 */
@Service
public class DcSchemaFieldServiceImpl extends JpaBase implements DcSchemaFieldService {

    @Autowired
    private DcGraphDao dcGraphDao;

    @Autowired
    private DcSchemaDao dcSchemaDao;

    @Autowired
    private DcSchemaFieldDao dcSchemaFieldDao;

    @Autowired
    private DcPubService dcPubService;

    @Override
    public List<DcSchemaFieldPo> findByGraph(String graph) {
        try {
            QDcSchemaFieldPo schemaFieldTable = QDcSchemaFieldPo.dcSchemaFieldPo;
            JPAQuery<DcSchemaFieldPo> jpaQuery = jpa
                    .select(schemaFieldTable)
                    .from(schemaFieldTable)
                    .where(schemaFieldTable.graph.eq(graph));
            return jpaQuery.fetch();
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_FIELD_FIND_ERROR, e);
        }
    }

    @Override
    public PageResponse findPage(DcSchemaFieldQo qo) {
        try {
            QDcSchemaFieldPo table = QDcSchemaFieldPo.dcSchemaFieldPo;
            BooleanBuilder builder = new BooleanBuilder();
            if (StringUtils.isNotEmpty(qo.getSchema())) {
                builder.and(table.schema.eq(qo.getSchema()));
            }
            if (StringUtils.isNotEmpty(qo.getGraph())) {
                builder.and(table.graph.eq(qo.getGraph()));
            }
            if (StringUtils.isNotEmpty(qo.getSearch())) {
                if (qo.getQueryType() == QueryType.ALL) {
                    builder.and(table.fieldNameCn.like(JQL.likeWrap(qo.getSearch())).or(table.field.like(JQL.likeWrap(qo.getSearch()))));
                } else if (qo.getQueryType() == QueryType.CH) {
                    builder.and(table.fieldNameCn.like(JQL.likeWrap(qo.getSearch())));
                } else if (qo.getQueryType() == QueryType.EN) {
                    builder.and(table.field.like(JQL.likeWrap(qo.getSearch())));
                }
            }
            if (Objects.nonNull(qo.getType())) {
                builder.and(table.type.eq(qo.getType()));
            }
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize(), JQL.SORT.UPDATED_DT_DESC);
            Page<DcSchemaFieldPo> page = dcSchemaFieldDao.findAll(builder, pageRequest);
            List<DcSchemaFieldVo> voList = page.getContent().stream().map(DcSchemaFieldVo::new).collect(Collectors.toList());
            return PageResponse.success(voList, page.getTotalElements(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_FIELD_PAGE_ERROR, e);
        }
    }

    @Override
    public PageResponse<DcSchemaFieldVo> findAuthorizedFieldPage(DcAuthorizedFieldQo qo) {
        try {
            Long graphId = qo.getGraphId();
            DcGraphPo dcGraph = dcGraphDao.findOne(graphId);
            Long schemaId = qo.getSchemaId();
            DcSchemaPo dcSchema = dcSchemaDao.findOne(schemaId);
            Long fieldId = qo.getId();
            if (dcGraph == null || dcSchema == null) {
                return PageResponse.success();
            }
            QDcSchemaFieldPo table = QDcSchemaFieldPo.dcSchemaFieldPo;
            BooleanBuilder builder = new BooleanBuilder();
            if (Objects.nonNull(fieldId)) {
                builder.and(table.id.eq(fieldId));
            }
            builder.and(table.graph.eq(dcGraph.getGraph()));
            builder.and(table.schema.eq(dcSchema.getSchema()));
            if (Objects.nonNull(qo.getSearch())) {
                builder.and(table.fieldNameCn.like(JQL.likeWrap(qo.getSearch())).or(table.field.like(JQL.likeWrap(qo.getSearch()))));
            }
            if (Objects.nonNull(qo.getType())) {
                builder.and(table.type.eq(qo.getType()));
            }
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize(), JQL.SORT.UPDATED_DT_DESC);

            Page<DcSchemaFieldPo> page = dcSchemaFieldDao.findAll(builder, pageRequest);
            List<DcSchemaFieldVo> voList = page.getContent().stream().map(DcSchemaFieldVo::new).collect(Collectors.toList());
            return PageResponse.success(voList, page.getTotalElements(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.AUTHORIZED_SCHEMA_FIELD_PAGE_ERROR, e);
        }
    }

    @Override
    public Response<List<String>> findDistinctByFieldByLike(String fieldName) {
        try {
            if (StringUtils.isEmpty(fieldName)) {
                return Response.success(Collections.EMPTY_LIST);
            }
            QDcSchemaFieldPo table = QDcSchemaFieldPo.dcSchemaFieldPo;
            List<String> fieldNameList = jpa.selectDistinct(table.field)
                    .from(table)
                    .where(table.field.like(JQL.likeWrap(fieldName))).fetch();
            return Response.success(fieldNameList);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.DISTINCT_FIELD_SCHEMA_FIELD_ERROR, e);
        }
    }

    @Override
    public Response<DcNameCheckVo> check(DcSchemaFieldCheckQO checkQO) {
        DcNameCheckVo checkVo = new DcNameCheckVo();
        try {
            checkFieldNameUnique(checkQO.getId(), checkQO.getGraph(), checkQO.getSchema(), checkQO.getField());
        } catch (Exception e) {
            checkVo.setCheckResult(false);
            checkVo.setCheckMsg(e.getMessage());
        }
        return Response.success(checkVo);
    }

    @Override
    public Response saveOrUpdate(DcSchemaFieldSuo suo) {
        checkFieldNameUnique(suo.getId(), suo.getGraph(), suo.getSchema(), suo.getField());
        DcSchemaFieldPo dcSchemaPo = new DcSchemaFieldPo(suo);
        boolean isCreate = dcSchemaPo.getId() == null;
        if (isCreate && dcSchemaPo.isMain()) {
            if (hasMainField(suo.getGraph(), suo.getSchema())) {
                throw new UnexpectedStatusException(CoreStatus.SCHEMA_IN_MAIN_FIELD_EXISTS, dcSchemaPo.getSchema());
            }
        }
        try {
            dcSchemaFieldDao.save(dcSchemaPo);
            dcPubService.publish(suo.getGraph());
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_FIELD_SAVE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public void createDefaultSysField(DcSchemaPo dcSchemaPo) {
        createStringField(dcSchemaPo.getGraph(), dcSchemaPo.getSchema(), Keys.OBJECT_KEY);
        if (dcSchemaPo.getType().isEdge()) {
            createStringField(dcSchemaPo.getGraph(), dcSchemaPo.getSchema(), Keys.FROM_KEY);
            createStringField(dcSchemaPo.getGraph(), dcSchemaPo.getSchema(), Keys.TO_KEY);
        }
    }

    @Override
    public Response delete(@NonNull Long id) {
        try {
            DcSchemaFieldPo schemaFieldPo = dcSchemaFieldDao.findOne(id);
            if (Objects.isNull(schemaFieldPo)) {
                throw new UnexpectedStatusException(CoreStatus.SCHEMA_FIELD_NOT_EXISTS);
            }
            dcSchemaFieldDao.delete(id);
            dcPubService.publish(schemaFieldPo.getGraph());
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_FIELD_DELETE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public void deleteField(@NonNull String graph, @NonNull String schema) {
        dcSchemaFieldDao.deleteByGraphAndSchema(graph, schema);
    }

    @Override
    public Response<Boolean> checkHasMainField(DcSchemaFieldMainCheckQO checkQO) {
        return Response.success(hasMainField(checkQO.getGraph(), checkQO.getSchema()));
    }

    @Override
    public void delete(String graph, String schema) {
        dcSchemaFieldDao.deleteByGraphAndSchema(graph, schema);
    }

    @Override
    public Response<List<DcSchemaFieldVo>> findAll(DcSchemaFieldApiQo dcSchemaFieldApiQo) {
        QDcSchemaFieldPo table = QDcSchemaFieldPo.dcSchemaFieldPo;
        Long schemaFieldId = dcSchemaFieldApiQo.getId();
        BooleanBuilder builder = new BooleanBuilder();
        if (schemaFieldId != null) {
            builder.and(table.id.eq(schemaFieldId));
        }
        if (Strings.isNotEmpty(dcSchemaFieldApiQo.getSearch())) {
            builder.and(table.fieldNameCn.like(JQL.likeWrap(dcSchemaFieldApiQo.getSearch()))
                    .or(table.field.like(JQL.likeWrap(dcSchemaFieldApiQo.getSearch()))));
        }
        if (Objects.nonNull(dcSchemaFieldApiQo.getType())) {
            builder.and(table.type.eq(dcSchemaFieldApiQo.getType()));
        }
        if (Objects.nonNull(dcSchemaFieldApiQo.getGraphId())) {
            DcGraphPo graphPo = dcGraphDao.findOne(dcSchemaFieldApiQo.getGraphId());
            if (Objects.nonNull(graphPo)) {
                builder.and(table.graph.eq(graphPo.getGraph()));
            } else Response.success(new ArrayList<>());
        }
        if (Objects.nonNull(dcSchemaFieldApiQo.getSchemaId())) {
            DcSchemaPo schemaPo = dcSchemaDao.findOne(dcSchemaFieldApiQo.getSchemaId());
            if (Objects.nonNull(schemaPo)) {
                builder.and(table.schema.eq(schemaPo.getSchema()));
            } else Response.success(new ArrayList<>());
        }
        List<DcSchemaFieldVo> voList = jpa.select(table).from(table).where(builder).fetch().stream().map(dcSchemaFieldPo -> new DcSchemaFieldVo(dcSchemaFieldPo)).collect(Collectors.toList());
        return Response.success(voList);
    }

    @Override
    public List<DcSchemaFieldPo> findByGraphAndSchema(@NonNull String graph, @NonNull String schema) {
        return dcSchemaFieldDao.findByGraphAndSchema(graph, schema);
    }

    public boolean hasMainField(String graph, String schema) {
        return !dcSchemaFieldDao.findByGraphAndSchemaAndIsMain(graph, schema, true).isEmpty();
    }

    private void createStringField(String graph, String schema, String fieldName) {
        DcSchemaFieldPo objectKeyField = new DcSchemaFieldPo();
        objectKeyField.setGraph(graph);
        objectKeyField.setSchema(schema);
        objectKeyField.setField(fieldName);
        objectKeyField.setType(FieldType.STRING);
        objectKeyField.setModifiable(false);
        objectKeyField.setSchema(schema);
        objectKeyField.setFieldNameCn(fieldName);
        dcSchemaFieldDao.save(objectKeyField);
    }

    private void checkFieldNameUnique(Long id, String graph, String schema, String field) {
        DcSchemaFieldPo sameField;
        if (Objects.isNull(id)) {
            sameField = dcSchemaFieldDao.findByGraphAndSchemaAndField(graph, schema, field);
        } else {
            sameField = dcSchemaFieldDao.findByIdIsNotAndGraphAndSchemaAndField(id, graph, schema, field);
        }
        if (Objects.nonNull(sameField)) {
            throw new UnexpectedStatusException(CoreStatus.SCHEMA_FIELD_NAME_EXISTS);
        }
    }
}
