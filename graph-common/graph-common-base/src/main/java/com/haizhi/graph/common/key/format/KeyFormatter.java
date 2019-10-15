package com.haizhi.graph.common.key.format;

/**
 * Created by chengmo on 2017/12/12.
 */
public interface KeyFormatter {

    Long toLong(long graphId, long schemaId, String md5);

    String format19Digit(long key);

    Long parseGraphId(long key);

    Long parseSchemaId(long key);
}
