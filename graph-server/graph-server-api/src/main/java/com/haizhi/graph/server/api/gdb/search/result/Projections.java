package com.haizhi.graph.server.api.gdb.search.result;

import com.haizhi.graph.server.api.gdb.search.bean.CollectionType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/1/24.
 */
public class Projections {

    private Map<String, List<Projection>> results = new LinkedHashMap<>();

    public Projections addProjection(CollectionType collectionType, String table, String field){
        if (StringUtils.isAnyBlank(table, field)){
            return this;
        }
        List<Projection> list = results.get(table);
        if (list == null){
            list = new ArrayList<>();
            results.put(table, list);
        }
        Projection rf = new Projection(collectionType, table, field);
        if (!list.contains(rf)){
            list.add(rf);
        }
        return this;
    }

    public Projections addProjection(String table, String field){
        return addProjection(CollectionType.DOCUMENT, table, field);
    }

    public Map<String, List<Projection>> getProjections(){
        return results;
    }
}
