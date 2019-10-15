package com.haizhi.graph.server.api.gdb.search.query;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

import java.util.Collection;

/**
 * Created by chengmo on 2018/1/19.
 */
public abstract class AbstractQBuilder implements QBuilder {

    @Override
    public final XContentBuilder toXContent() {
        return doXContent();
    }

    protected abstract XContentBuilder doXContent();

    @Override
    public final String toString() {
        try {
            XContentBuilder xb = toXContent();
            return JSON.toJSONString(xb.rootObject(), true);
        } catch (Exception e) {
            // So we have a stack trace logged somewhere
            return "{ \"error\" : \"" + e.getMessage() + "\"}";
        }
    }

    protected static <T> T requireValue(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        if (value instanceof String) {
            if (((String) value).trim().isEmpty()) {
                throw new IllegalArgumentException(message);
            }
        }
        if (value instanceof Collection<?>) {
            if (((Collection<?>) value).isEmpty()) {
                throw new IllegalArgumentException(message);
            }
        }
        return value;
    }
}
