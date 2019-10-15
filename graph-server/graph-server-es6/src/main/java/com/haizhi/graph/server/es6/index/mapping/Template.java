package com.haizhi.graph.server.es6.index.mapping;

import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;

/**
 * Created by chengmo on 2017/12/01.
 */
public class Template {

    private static final GLog Log = LogFactory.getLogger(Template.class);

    public static XContentBuilder getSettings() {
        XContentBuilder xb = null;
        try {
            xb = XContentFactory.jsonBuilder()
            .startObject()
                .field("index.number_of_shards", 5)      //default 5
                .field("index.number_of_replicas", 1)    //default 1
                /*.startObject("analysis")
                    .startObject("analyzer")
                        .startObject(Analyzers.IK.code())
                            .field("type", "custom")
                            .field("tokenizer", "standard_tokenizer")
                        .endObject()
                    .endObject()
                    .startObject("tokenizer")
                        .startObject("standard_tokenizer")
                            .field("type", "standard")
                            .field("max_token_length", 5)
                        .endObject()
                    .endObject()
                .endObject()*/
                .startObject("index")
                    .startObject("blocks")
                        .field("read_only_allow_delete", false)
                    .endObject()
                .endObject()
            .endObject();
        } catch (IOException e) {
            Log.error(e);
        }
        return xb;
    }

    public static XContentBuilder getTypeMapping() {
        XContentBuilder xb = null;
        try {
            xb = XContentFactory.jsonBuilder()
            .startObject()
                .startObject("_all")
                    .field("enabled", false)
                .endObject()
                .field("date_detection", false)
                .startArray("dynamic_templates")
                    .startObject()
                        .startObject("strings")
                            .field("match_mapping_type", "string")
                            .startObject("mapping")
                                .field("type", "text")
                                 //comment smart cn because use smart cn plugin then can not search anything in fi es env
                                //.field("analyzer", Analyzers.SMART_CN.code())
                                .startObject("fields")
                                    .startObject("keyword")
                                        .field("type", "keyword")
                                    .endObject()
                                .endObject()
                            .endObject()
                        .endObject()
                    .endObject()
                .endArray()
            .endObject();
        } catch (IOException e) {
            Log.error(e);
        }
        return xb;
    }

}
