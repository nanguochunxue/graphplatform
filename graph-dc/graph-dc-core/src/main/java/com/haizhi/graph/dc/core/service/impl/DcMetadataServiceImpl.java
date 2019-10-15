package com.haizhi.graph.dc.core.service.impl;

import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.bean.SchemaField;
import com.haizhi.graph.dc.core.dao.*;
import com.haizhi.graph.dc.core.model.po.DcGraphPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaFieldPo;
import com.haizhi.graph.dc.core.model.po.DcSchemaPo;
import com.haizhi.graph.dc.core.model.vo.DcStoreVo;
import com.haizhi.graph.dc.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chengmo on 2018/10/23.
 */
@Service
public class DcMetadataServiceImpl implements DcMetadataService {

    private static final GLog LOG = LogFactory.getLogger(DcMetadataServiceImpl.class);

    @Autowired
    private DcGraphDao graphDao;

    @Autowired
    private DcGraphStoreDao graphStoreDao;

    @Autowired
    private DcStoreDao storeDao;

    @Autowired
    private DcSchemaDao schemaDao;

    @Autowired
    private DcSchemaFieldDao schemaFieldDao;

    @Autowired
    private DcVertexEdgeDao vertexEdgeDao;

    @Autowired
    private DcSchemaService dcSchemaService;

    @Autowired
    private DcGraphStoreService graphStoreService;

    @Autowired
    private DcStoreService storeService;

    @Autowired
    private DcSchemaFieldService schemaFieldService;

    @Override
    public Domain getDomain(String graph) {
        try {
            // graph
            DcGraphPo dcGraphPo = graphDao.findByGraph(graph);
            if (graph == null || dcGraphPo == null) {
                LOG.error("graph[{0}] not found", graph);
                return Domain.empty();
            }
            long graphId = dcGraphPo.getId();

            // domain
            Domain domain = new Domain(graphId, graph);

            // schemas
            List<DcSchemaPo> schemas = dcSchemaService.findByGraph(graph);

            // schemaId, fields
            Map<String, List<DcSchemaFieldPo>> fieldMap = getFieldMap(graph);

            // vertex_edge
            Set<String> vertexEdgeSet = getVertexEdgeSet(graph);

            // graph_stores
//        domain.getStores().putAll(getStores(graph));
            domain.getStoreMap().putAll(getStores(graph));

            Set<String> allEdgeNames = new LinkedHashSet<>();
            Set<String> allRelatedVertexNames = new LinkedHashSet<>();
            // join schemas and fields
            for (DcSchemaPo schema : schemas) {
                long schemaId = schema.getId();
                String schemaName = schema.getSchema();
                SchemaType type = schema.getType();

                Schema sch = new Schema();
                sch.setGraphId(graphId);
                sch.setGraphName(graph);
                sch.setSchemaId(schemaId);
                sch.setSchemaName(schemaName);
                sch.setSchemaNameCn(schema.getSchemaNameCn());
                sch.setType(type);
                sch.setSequence(schema.getSequence());
                sch.setUseSearch(schema.isUseSearch());
                sch.setUseGraphDb(schema.isUseGdb());
                sch.setUseHBase(schema.isUseHBase());
                sch.setSearchWeight(schema.getSearchWeight());

                boolean isVertex = SchemaType.isVertex(type);
                boolean isEdge = SchemaType.isEdge(type);
                if (SchemaType.isMainVertex(type)) {
                    domain.setMainSchema(schemaName);
                } else if (isEdge) {
                    allEdgeNames.add(sch.getSchemaName());
                }

                for (String str : vertexEdgeSet) {
                    if (str.contains(schemaName)) {
                        String[] arr = str.split("\\.");
                        String fromVertex = arr[0];
                        String toVertex = arr[1];
                        String edge = arr[2];
                        sch.getVertexEdgeList().add(new Schema.VertexEdge(fromVertex, toVertex, edge));
                        if (isVertex) {
                            sch.getEdgeSchemas().add(edge);
                        } else if (isEdge) {
                            sch.getVertexSchemas().add(fromVertex);
                            sch.getVertexSchemas().add(toVertex);
                            allRelatedVertexNames.add(fromVertex);
                            allRelatedVertexNames.add(toVertex);
                        }
                    }
                }

                List<DcSchemaFieldPo> fieldList = fieldMap.get(schemaName);
                if (Objects.nonNull(fieldList)){
                    for (DcSchemaFieldPo sf : fieldList) {
                        String fieldName = sf.getField();
                        SchemaField field = new SchemaField();
                        field.setGraphId(graphId);
                        field.setGraphName(graph);
                        field.setSchemaId(schemaId);
                        field.setSchemaName(schemaName);
                        field.setField(fieldName);
                        field.setType(sf.getType());
                        field.setUseSearch(schema.isUseSearch());
                        field.setUseGraphDb(schema.isUseGdb());
                        field.setSearchWeight(sf.getSearchWeight());
                        field.setFieldCnName(sf.getFieldNameCn());

                        Boolean isMain = sf.isMain();
                        if (Objects.isNull(isMain)){
                            isMain = Boolean.FALSE;
                        }
                        field.setMain(isMain);
                        if (isMain) {
                            sch.setMainField(fieldName);
                        }
                        // fieldCnName
    //                SchemaField uiField = uiFieldMap.get(schemaName + fieldName);
    //                field.setFieldCnName(uiField != null ? uiField.getFieldCnName() : "");
                        sch.addField(field);
                    }
                }
                domain.addSchema(sch);
                domain.setAllEdgeNames(allEdgeNames);
                domain.setAllRelatedVertexNames(allRelatedVertexNames);
            }
            return domain;
        } catch (Exception e) {
            LOG.error(e);
        }
        return Domain.empty();
    }

    private Map<StoreType, DcStoreVo> getStores(String graph) {
        List<DcStoreVo> dcStorePos = storeService.findVoByGraph(graph);
        return dcStorePos.stream().collect(Collectors.toMap(e -> e.getType(), e -> e));
    }

    private Set<String> getVertexEdgeSet(String graph) {
        return vertexEdgeDao.findByGraph(graph).stream().map(f ->
                f.getFromVertex() + "." + f.getToVertex() + "." + f.getEdge()).collect(Collectors.toSet());
    }

    private Map<String, List<DcSchemaFieldPo>> getFieldMap(String graph) {
        List<DcSchemaFieldPo> schemaFieldPos = schemaFieldService.findByGraph(graph);
        return schemaFieldPos.stream()
                .collect(Collectors.groupingBy(DcSchemaFieldPo::getSchema));
    }
}