package com.haizhi.graph.dc.core.bean;

import com.haizhi.graph.common.constant.StoreType;
import com.haizhi.graph.dc.core.model.vo.DcStoreVo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by chengmo on 2018/1/17.
 */
@Data
@NoArgsConstructor
public class Domain implements Serializable {
    private long graphId;
    private String graphName;
    private String mainSchema;
    private Map<String, Schema> schemaMap = new LinkedHashMap<>();
    private Set<String> allEdgeNames = new LinkedHashSet<>();
    private Set<String> allRelatedVertexNames = new LinkedHashSet<>();
    // storeType.name, store.url
    private Map<String, String> stores = new HashMap<>();
    // storeType, DcStorePo
    private Map<StoreType, DcStoreVo> storeMap = new HashMap<>();

    public Domain(long graphId, String graphName) {
        this.graphId = graphId;
        this.graphName = graphName;
    }

    public String getStoreUrl(String storeType) {
        return this.stores.get(storeType);
    }

    public Schema getSchema(String schemaName) {
        return schemaMap.get(schemaName);
    }

    public Set<String> getRelatedVertexNames(Set<String> edgeNames) {
        Set<String> results = new HashSet<>();
        for (String edgeName : edgeNames) {
            Schema sch = schemaMap.get(edgeName);
            if (sch == null) {
                continue;
            }
            results.addAll(sch.getVertexSchemas());
        }
        return results;
    }

    public List<Schema> getSchemas(Collection<String> schemaNames) {
        List<Schema> resultList = new ArrayList<>();
        for (String schemaName : schemaNames) {
            Schema sch = schemaMap.get(schemaName);
            if (sch != null) {
                resultList.add(sch);
            }
        }
        return resultList;
    }

    public boolean isMainSchema(String schema) {
        return mainSchema == null ? false : mainSchema.equals(schema);
    }

    public boolean invalid() {
        return graphId == 0 || StringUtils.isBlank(graphName);
    }

    public void addSchema(Schema sch) {
        if (sch == null) {
            return;
        }
        schemaMap.put(sch.getSchemaName(), sch);
    }

    public static Domain empty(){
        return new Domain();
    }
}
