package com.haizhi.graph.server.api.gdb.search.query;

import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;

/**
 * Created by chengmo on 2018/1/20.
 */
public class RangeQBuilder extends AbstractQBuilder {

    public static final String NAME = "range";
    public static final String FROM_FIELD = "from";
    public static final String TO_FIELD = "to";
    public static final String INCLUDE_LOWER_FIELD = "include_lower";
    public static final String INCLUDE_UPPER_FIELD = "include_upper";
    private final String fieldName;
    private Object from;
    private Object to;
    private boolean includeLower = true;
    private boolean includeUpper = true;

    public RangeQBuilder(String fieldName) {
        this.fieldName = requireValue(fieldName, "field name is null or empty");
    }

    /**
     * The from part of the range query. Null indicates unbounded.
     *
     * @param from
     * @return
     */
    public RangeQBuilder from(Object from) {
        return from(from, this.includeLower);
    }

    /**
     * The from part of the range query. Null indicates unbounded.
     *
     * @param from
     * @param includeLower
     * @return
     */
    public RangeQBuilder from(Object from, boolean includeLower) {
        this.from = from;
        this.includeLower = includeLower;
        return this;
    }

    /**
     * The to part of the range query. Null indicates unbounded.
     *
     * @param to
     * @return
     */
    public RangeQBuilder to(Object to) {
        return to(to, this.includeUpper);
    }

    /**
     * The to part of the range query. Null indicates unbounded.
     *
     * @param to
     * @param includeUpper
     * @return
     */
    public RangeQBuilder to(Object to, boolean includeUpper) {
        this.to = to;
        this.includeUpper = includeUpper;
        return this;
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(NAME);
        xb.put(fieldName, XContentBuilder.jsonObject()
                .fluentPut(FROM_FIELD, from)
                .fluentPut(TO_FIELD, to)
                .fluentPut(INCLUDE_LOWER_FIELD, includeLower)
                .fluentPut(INCLUDE_UPPER_FIELD, includeUpper));
        return xb;
    }

    public String fieldName() {
        return this.fieldName;
    }

    public Object from() {
        return this.from;
    }

    public Object to() {
        return this.to;
    }

    public boolean includeLower() {
        return this.includeLower;
    }

    public boolean includeUpper() {
        return this.includeUpper;
    }
}
