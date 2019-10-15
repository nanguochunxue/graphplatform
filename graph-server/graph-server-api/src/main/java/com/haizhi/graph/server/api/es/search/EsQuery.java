package com.haizhi.graph.server.api.es.search;

import lombok.Data;

import java.util.*;

/**
 * Created by chengmo on 2019/4/30.
 */
@Data
public class EsQuery {
    private String graph;
    private String keyword;
    private int pageNo = 0;
    private int pageSize = 10;
    private String queryDSL;
    private Set<String> schemas;
    private Map<String, Set<String>> schemaKeys;
    private Map<String, Object> option;
    private List<Map<String, Object>> query;
    private Map<String, Object> filter;
    private List<Map<String, Object>> sort;
    private List<Map<String, Object>> aggregation;
    private Set<String> fields;
    private int timeout = 30;

    /* log */
    private boolean debugEnabled;
    /* highlight */
    private boolean highlight = false;
    public static String HIGHLIGHTER_PRE_TAGS = "<em>";
    public static String HIGHLIGHTER_POST_TAGS = "</em>";
    private String highlighterPreTags = HIGHLIGHTER_PRE_TAGS;
    private String highlighterPostTags = HIGHLIGHTER_POST_TAGS;
    private Set<String> excludedHLFields = new HashSet<>(Arrays.asList(""));
}