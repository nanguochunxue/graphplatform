package com.haizhi.graph.dc.core.service.impl;

import com.alibaba.fastjson.JSON;
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
import com.haizhi.graph.dc.core.dao.DcGraphDao;
import com.haizhi.graph.dc.core.dao.DcGraphStoreDao;
import com.haizhi.graph.dc.core.model.po.*;
import com.haizhi.graph.dc.core.model.qo.DcGraphCheckQO;
import com.haizhi.graph.dc.core.model.qo.DcGraphIdsQo;
import com.haizhi.graph.dc.core.model.qo.DcGraphQo;
import com.haizhi.graph.dc.core.model.suo.DcGraphSuo;
import com.haizhi.graph.dc.core.model.vo.*;
import com.haizhi.graph.dc.core.redis.DcStorePubService;
import com.haizhi.graph.dc.core.service.DcGraphService;
import com.haizhi.graph.dc.core.service.DcGraphStoreService;
import com.haizhi.graph.dc.core.service.DcSchemaService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhouduqing on 2018/12/24.
 */
@Slf4j
@Service
public class DcGraphServiceImpl extends JpaBase implements DcGraphService {

    private static final GLog LOG = LogFactory.getLogger(DcGraphServiceImpl.class);

    @Autowired
    private DcGraphDao dcGraphDao;

    @Autowired
    private DcGraphStoreDao dcGraphStoreDao;

    @Autowired
    private DcGraphStoreService dcGraphStoreService;

    @Autowired
    private DcSchemaService dcSchemaService;

    @Autowired
    private DcStorePubService dcStorePubService;

    @Override
    public PageResponse findPage(DcGraphQo qo) {
        try {
            PageQo pageQo = qo.getPage();
            PageRequest pageRequest = new PageRequest(pageQo.getPageNo() - 1, pageQo.getPageSize(), JQL.SORT.UPDATED_DT_DESC);
            Page<DcGraphPo> page = dcGraphDao.findAll(pageRequest);
            return PageResponse.success(page.getContent(), page.getTotalElements(), pageQo);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.GRAPH_PAGE_ERROR, e);
        }
    }

    @Override
    public Response<List<DcGraphFrameVo>> findGraphFrame() {
        QDcGraphPo graph = QDcGraphPo.dcGraphPo;
        QDcSchemaPo schema = QDcSchemaPo.dcSchemaPo;
        JPAQuery<Tuple> query = jpa
                .select(graph, schema.id).from(graph)
                .leftJoin(schema).on(graph.graph.eq(schema.graph))
                .groupBy(graph.graph).select(graph, schema.id.count());
        List<DcGraphFrameVo> voList = query.fetch().stream().map(tuple -> {
            DcGraphFrameVo vo = new DcGraphFrameVo(tuple.get(0, DcGraphPo.class), tuple.get(1, Long.class));
            return vo;
        }).collect(Collectors.toList());
        return Response.success(voList);
    }

    @Override
    public Response findByIds(DcGraphIdsQo ids) {
        QDcGraphPo graphTable = QDcGraphPo.dcGraphPo;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(graphTable.id.in(ids.getIds()));
        Iterable<DcGraphPo> dcGraphPoList = dcGraphDao.findAll(booleanBuilder);
        List<DcGraphVo> dcGraphVos = new ArrayList<>();
        dcGraphPoList.forEach(dcGraphPo -> {
            dcGraphVos.add(new DcGraphVo(dcGraphPo));
        });
        return Response.success(dcGraphVos);
    }

    @Override
    public Response findAll() {
        try {
            Iterable<DcGraphPo> results = dcGraphDao.findAll(JQL.SORT.UPDATED_DT_DESC);
            List<DcGraphVo> rows = new ArrayList<>();
            results.forEach((po) -> {
                rows.add(new DcGraphVo(po));
            });
            return Response.success(rows);
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.GRAPH_FIND_ERROR, e);
        }
    }

    @Override
    @Transactional
    public Response saveOrUpdate(DcGraphSuo suo) {
        try {
            Long id = suo.getId();
            if ( StringUtils.isEmpty(suo.getGraph())) {
                LOG.error("graph name is empty: {0}", JSON.toJSONString(suo,true));
                return Response.error("graph name is empty, check the error log!");
            }
            String graphNewName = suo.getGraph().toLowerCase();    // because es index must be lower case
            if(Objects.isNull(id)) {     // id is null and then insert
                if(checkGraphExist(graphNewName)){
                    return Response.error("graph must lower case and graph name exist: " + graphNewName);
                }
                suo.setGraph(graphNewName);
                DcGraphPo dcGraphPo = new DcGraphPo(suo);
                dcGraphDao.save(dcGraphPo);
                List<DcGraphStorePo> graphStorePos = assemblyGraphStore(suo);
                dcGraphStoreDao.save(graphStorePos);
                return Response.success();
            }
            // id is not null and then update
            DcGraphPo po = dcGraphDao.findOne(id);
            String graphOldName = po.getGraph();

            dcGraphStoreDao.deleteByGraph(graphOldName);
            List<DcGraphStorePo> graphStorePos = assemblyGraphStore(suo);
            dcGraphStoreDao.save(graphStorePos);

            if(!checkGraphExist(graphNewName)){
                renameGraph(graphOldName, graphNewName, suo);
            }

            dcStorePubService.publishByGraph(graphNewName);       // refresh new graph cache
            dcStorePubService.publishByGraph(graphOldName);       // clean   old graph cache
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.GRAPH_SAVE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public Response delete(@NonNull Long id) {
        DcGraphPo dcGraphPo = dcGraphDao.findOne(id);
        if (Objects.isNull(dcGraphPo)) {
            throw new UnexpectedStatusException(CoreStatus.GRAPH_NOT_EXISTS);
        }
        try {
            dcGraphDao.delete(id);
            dcGraphStoreDao.deleteByGraph(dcGraphPo.getGraph());
            dcSchemaService.deleteByGraph(dcGraphPo.getGraph());
            dcStorePubService.publishByGraph(dcGraphPo.getGraph());
        } catch (Exception e) {
            throw new UnexpectedStatusException(CoreStatus.GRAPH_DELETE_ERROR, e);
        }
        return Response.success();
    }

    @Override
    public Response<DcNameCheckVo> check(DcGraphCheckQO graphNameCheckQO) {
        DcNameCheckVo checkVo = new DcNameCheckVo();
        if(checkGraphExist(graphNameCheckQO.getName())) {
            checkVo.setCheckResult(false);
            checkVo.setCheckMsg("graph name is exist!");
        }
        checkVo.setCheckMsg("graph name is valid!");
        return Response.success(checkVo);
    }

    @Override
    public Response<DcGraphDetailVo> findDetailById(@NonNull Long id) {
        DcGraphPo graphPo = findById(id);
        if (Objects.isNull(graphPo)) {
            return Response.success();
        }
        Map<StoreType, DcGraphStorePo> storeMap = dcGraphStoreService.findByGraph(graphPo.getGraph());
        DcGraphDetailVo detailVo = new DcGraphDetailVo(graphPo, getId(storeMap.get(StoreType.Hbase)),
                getId(storeMap.get(StoreType.ES)), getId(storeMap.get(StoreType.GDB)));
        DcGraphStoreVo graphStoreVo = getGraphStoreDetail(detailVo.getGraph());
        detailVo.setGraphStoreVo(graphStoreVo);
        return Response.success(detailVo);
    }

    @Override
    public DcGraphPo findById(Long id) {
        return dcGraphDao.findOne(id);
    }

    @Override
    public DcGraphPo findByByGraph(String graph) {
        return dcGraphDao.findByGraph(graph);
    }

    private Long getId(DcGraphStorePo dcGraphStorePo) {
        return dcGraphStorePo == null ? null : dcGraphStorePo.getStoreId();
    }


    private void doSaveOrUpdate(DcGraphSuo suo) {
        if (Objects.isNull(suo.getId())) {
            DcGraphPo dcGraphPo = new DcGraphPo(suo);
            dcGraphDao.save(dcGraphPo);
        } else {
            QDcGraphPo dcGraphPo = QDcGraphPo.dcGraphPo;
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(builder.and(dcGraphPo.id.eq(suo.getId())));
//            refresh update time , because just change store , this time will not update
            jpa.update(dcGraphPo)
                    .set(dcGraphPo.graph, suo.getGraph())
                    .set(dcGraphPo.graphNameCn, suo.getGraphNameCn())
                    .set(dcGraphPo.remark, suo.getRemark())
                    .set(dcGraphPo.updatedDt, new Date())
                    .where(builder)
                    .execute();
        }
    }

    private List<DcGraphStorePo> assemblyGraphStore(DcGraphSuo suo) {
        List<DcGraphStorePo> graphStorePos = new ArrayList<>();
        if (Objects.nonNull(suo.getHbase())) {
            addGraphStore(graphStorePos, suo.getGraph(), StoreType.Hbase, suo.getHbase());
        }
        if (Objects.nonNull(suo.getEs())) {
            addGraphStore(graphStorePos, suo.getGraph(), StoreType.ES, suo.getEs());
        }
        if (Objects.nonNull(suo.getGdb())) {
            addGraphStore(graphStorePos, suo.getGraph(), StoreType.GDB, suo.getGdb());
        }
        return graphStorePos;
    }

    private void addGraphStore(List<DcGraphStorePo> graphStorePos, String graph, StoreType storeType, Long storeId) {
        DcGraphStorePo graphStorePo = new DcGraphStorePo();
        graphStorePo.setGraph(graph);
        graphStorePo.setStoreType(storeType);
        graphStorePo.setStoreId(storeId);
        graphStorePos.add(graphStorePo);
    }


    private DcGraphStoreVo getGraphStoreDetail(String graph) {
        return dcSchemaService.countByGraph(graph);
    }

//    private void checkGraphNameUnique(String graph, Long id) {
//        DcGraphPo sameGraph;
//        if (Objects.isNull(id)) {
//            sameGraph = dcGraphDao.findByGraph(graph);
//        } else {
//            sameGraph = dcGraphDao.findByGraphAndIdIsNot(graph, id);
//        }
//        if (Objects.nonNull(sameGraph)) {
//            throw new UnexpectedStatusException(CoreStatus.GRAPH_NAME_EXISTS);
//        }
//    }

    private boolean checkGraphExist(String graph) {
        if(StringUtils.isAnyEmpty(graph)){
            LOG.audit("graph is null");
            throw new UnexpectedStatusException(CoreStatus.GRAPH_IS_NULL);
        }
        QDcGraphPo dcGraphPo = QDcGraphPo.dcGraphPo;
        List<String> graphList = jpa.select(dcGraphPo.graph).from(dcGraphPo).fetch();
        if(Objects.nonNull(graphList)) {
            for (String existGraph : graphList) {
                if (StringUtils.equals(existGraph.toLowerCase(), graph.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void renameGraph(String graphOldName, String graphNewName, DcGraphSuo suo){
        Date date = new Date();
        // update dc_graph
        QDcGraphPo dcGraphPo = QDcGraphPo.dcGraphPo;
        BooleanBuilder builderDcGraph = new BooleanBuilder().and(dcGraphPo.graph.eq(graphOldName));
        jpa.update(dcGraphPo)
                .set(dcGraphPo.graph, graphNewName)
                .set(dcGraphPo.graphNameCn, suo.getGraphNameCn())
                .set(dcGraphPo.remark, suo.getRemark())
                .set(dcGraphPo.updatedDt, date)
                .where(builderDcGraph)
                .execute();
        // update dc_schema
        QDcSchemaPo dcSchemaPo = QDcSchemaPo.dcSchemaPo;
        BooleanBuilder builderDcSchemaPo = new BooleanBuilder().and(dcSchemaPo.graph.eq(graphOldName));
        jpa.update(dcSchemaPo)
                .set(dcSchemaPo.graph, graphNewName)
                .set(dcSchemaPo.updatedDt, date)
                .where(builderDcSchemaPo)
                .execute();
        // update dc_schema_field
        QDcSchemaFieldPo dcSchemaFieldPo = QDcSchemaFieldPo.dcSchemaFieldPo;
        BooleanBuilder builderDcSchemaField = new BooleanBuilder().and(dcSchemaFieldPo.graph.eq(graphOldName));
        jpa.update(dcSchemaFieldPo)
                .set(dcSchemaFieldPo.graph, graphNewName)
                .set(dcSchemaFieldPo.updatedDt, date)
                .where(builderDcSchemaField)
                .execute();
        // update dc_task
        QDcTaskPo qDcTaskPo = QDcTaskPo.dcTaskPo;
        BooleanBuilder builderDcTask = new BooleanBuilder().and(qDcTaskPo.graph.eq(graphOldName));
        jpa.update(qDcTaskPo)
                .set(qDcTaskPo.graph, graphNewName)
                .set(qDcTaskPo.updatedDt, date)
                .where(builderDcTask)
                .execute();
        // update dc_verte_edge
        QDcVertexEdgePo qDcVertexEdgePo = QDcVertexEdgePo.dcVertexEdgePo;
        BooleanBuilder builderDcVertexEdge = new BooleanBuilder().and(qDcVertexEdgePo.graph.eq(graphOldName));
        jpa.update(qDcVertexEdgePo)
                .set(qDcVertexEdgePo.graph, graphNewName)
                .where(builderDcVertexEdge)
                .execute();
    }
}
