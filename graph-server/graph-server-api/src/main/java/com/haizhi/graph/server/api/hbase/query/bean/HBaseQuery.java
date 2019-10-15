package com.haizhi.graph.server.api.hbase.query.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by chengmo on 2018/2/5.
 */
@Data
@NoArgsConstructor
public class HBaseQuery {

    private String database;
    private String url;
    private Map<String, Set<String>> schemas = new LinkedHashMap<>();

    public HBaseQuery(String database) {
        this(database, null);
    }

    public HBaseQuery(String database, String url) {
        if (StringUtils.isBlank(database)) {
            throw new IllegalArgumentException("namespace is null or empty");
        }
        this.database = database;
        this.url = url;
    }

    /**
     * Added rowKeys to the query.
     *
     * @param table
     * @param rowKeys
     * @return
     */
    public HBaseQuery addRowKeys(String table, String... rowKeys) {
        Set<String> set = (Set<String>)schemas.get(table);
        if (set == null) {
            set = new HashSet<>();
            schemas.put(table, set);
        }
        set.addAll(Arrays.asList(rowKeys));
        return this;
    }

    /**
     * Added rowKeys to the query.
     *
     * @param rowKeys
     * @return
     */
    public HBaseQuery addRowKeys(String table, Collection<String> rowKeys) {
        Set<String> set = (Set<String>)schemas.get(table);
        if (set == null) {
            set = new HashSet<>();
            schemas.put(table, set);
        }
        set.addAll(rowKeys);
        return this;
    }

}
