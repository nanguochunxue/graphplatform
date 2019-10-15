package com.haizhi.graph.server.arango.search.builder;

import com.haizhi.graph.server.api.gdb.search.GdbQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by tanghaiyang on 2019/4/4.
 */
@Service
public class IdsBuilder {

    private static final String AQL_PATTERN_FIND_BY_KEY = "for document in %s filter document._key in ['%s'] return document";
    private static final String STRING_CONNECTOR_COMMA_QUOTATION = "','";

    /*
    * @see  conditions/findByIdsComplex.json"
    * for c in vertex filter c._id in ["vertex/2664027","vertex/2664021" ] limit 100 sort c.key DESC return c
    * */
    @SuppressWarnings("unchecked")
    public static  Map<String,String> get(GdbQuery gdbQuery){
        Map<String, Set<String>> schemas = gdbQuery.getSchemas();
        Map<String,String> aqlList = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : schemas.entrySet()) {
            String schema = entry.getKey();
            String listStr = StringUtils.join(entry.getValue(), STRING_CONNECTOR_COMMA_QUOTATION);
            String aql = String.format(AQL_PATTERN_FIND_BY_KEY, schema, listStr);
            aqlList.put(schema,aql);
        }
        return aqlList;
    }

}
