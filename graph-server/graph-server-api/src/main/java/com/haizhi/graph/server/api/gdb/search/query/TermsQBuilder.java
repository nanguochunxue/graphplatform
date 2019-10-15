package com.haizhi.graph.server.api.gdb.search.query;

import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by chengmo on 2018/1/20.
 */
public class TermsQBuilder extends AbstractQBuilder {

    public static final String NAME = "terms";
    private final String fieldName;
    private final List<?> values;

    public TermsQBuilder(String fieldName, String... values) {
        this(fieldName, values != null ? Arrays.asList(values) : null);
    }

    public TermsQBuilder(String fieldName, int... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) :
                (Iterable<?>) null);
    }

    public TermsQBuilder(String fieldName, long... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) :
                (Iterable<?>) null);
    }

    public TermsQBuilder(String fieldName, float... values) {
        this(fieldName, values != null ? IntStream.range(0, values.length)
                .mapToObj(i -> values[i]).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    public TermsQBuilder(String fieldName, double... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) :
                (Iterable<?>) null);
    }

    public TermsQBuilder(String fieldName, Iterable<?> values) {
        this.fieldName = requireValue(fieldName, "field name is null or empty");
        if (values == null) {
            throw new IllegalArgumentException("No value specified for terms query");
        }
        this.values = convert(values);
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(fieldName, XContentBuilder.jsonArray().fluentAddAll(values));
        return xb;
    }

    public String fieldName() {
        return this.fieldName;
    }

    public List<?> values() {
        return this.values;
    }

    private static List<?> convert(Iterable<?> values) {
        List<Object> list = new ArrayList<>();
        for (Object o : values) {
            list.add(o);
        }
        return list;
    }
}
