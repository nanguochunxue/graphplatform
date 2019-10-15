package com.haizhi.graph.tag.analytics.service.builder;

import com.haizhi.graph.engine.base.rule.script.ScriptContext;
import com.haizhi.graph.tag.core.domain.TagSchema;
import com.haizhi.graph.tag.core.domain.TagSchemaType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by chengmo on 2018/5/4.
 */
public class TagSchemaBuilder {

    public static List<TagSchema> create(ScriptContext ctx, String graph, long tagId){
        if (Objects.isNull(ctx.getMetadataMap())){
            return Collections.emptyList();
        }
        List<TagSchema> tagSchemas = new ArrayList<>();
        for (ScriptContext.Metadata md : ctx.getMetadataMap().values()) {
            TagSchema ts = new TagSchema();
            ts.setGraph(graph);
            ts.setTagId(tagId);
            ts.setType(getTagSchemaType(md.getType()));
            ts.setSchema(md.getName());
            if (md.getFields().isEmpty()){
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (String field : md.getFields()) {
                sb.append(field).append(",");
            }
            sb = sb.delete(sb.length() - 1, sb.length());
            ts.setFields(sb.toString());
            tagSchemas.add(ts);
        }
        return tagSchemas;
    }

    public static TagSchemaType getTagSchemaType(ScriptContext.Type type){
        TagSchemaType schemaType = TagSchemaType.VERTEX;
        switch (type){
            case TAG:
                schemaType = TagSchemaType.TAG;
                break;
            case SQL:
                schemaType = TagSchemaType.SQL;
                break;
            case EDGE:
                schemaType = TagSchemaType.EDGE;
                break;
        }
        return schemaType;
    }

}
