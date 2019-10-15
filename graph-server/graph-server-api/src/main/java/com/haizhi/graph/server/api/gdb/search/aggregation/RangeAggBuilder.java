package com.haizhi.graph.server.api.gdb.search.aggregation;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by chengmo on 2018/1/22.
 */
public class RangeAggBuilder extends AbstractAggBuilder {

    public static final String NAME = "range";
    public static final String RANGES_FIELD = "ranges";
    public static final String KEY_FIELD = "key";
    public static final String FROM_FIELD = "from";
    public static final String TO_FIELD = "to";
    private List<Range> ranges = new ArrayList<>();

    public RangeAggBuilder(String table, String field) {
        super(table, field);
    }

    public RangeAggBuilder(String name, String table, String field) {
        super(name, table, field);
    }

    @Override
    protected XContentBuilder doXContent() {
        XContentBuilder xb = XContentBuilder.builder(this.name);
        JSONObject rangeValue = XContentBuilder.jsonObject();
        xb.put(NAME, rangeValue);

        // range
        rangeValue.put(TABLE_FIELD, table);
        rangeValue.put(FIELD_FIELD, field);
        rangeValue.put(SIZE_FIELD, size);

        // ranges
        JSONArray rangesValue = XContentBuilder.jsonArray();
        rangeValue.put(RANGES_FIELD, rangesValue);
        for (Range range : ranges) {
            rangesValue.add(XContentBuilder.jsonObject()
                    .fluentPut(KEY_FIELD, range.getKey())
                    .fluentPut(FROM_FIELD, range.getFrom())
                    .fluentPut(TO_FIELD, range.getTo()));
        }
        return xb;
    }

    /**
     * Add a new range to this aggregation.
     *
     * @param key
     * @param from
     * @param to
     * @return
     */
    public RangeAggBuilder addRange(String key, double from, double to) {
        addRange(new Range(key, from, to));
        return this;
    }

    /**
     * Add a new range to this aggregation, but the key will be
     * automatically generated based on from and to.
     *
     * @param from
     * @param to
     * @return
     */
    public RangeAggBuilder addRange(double from, double to) {
        return addRange(null, from, to);
    }

    /**
     * Add a new range with no lower bound.
     *
     * @param key
     * @param to
     * @return
     */
    public RangeAggBuilder addUnboundedTo(String key, double to) {
        addRange(new Range(key, null, to));
        return this;
    }

    /**
     * Add a new range with no lower bound, but the key will be
     * computed automatically.
     *
     * @param to
     * @return
     */
    public RangeAggBuilder addUnboundedTo(double to) {
        return addUnboundedTo(null, to);
    }

    /**
     * Add a new range with no upper bound.
     *
     * @param key
     * @param from
     * @return
     */
    public RangeAggBuilder addUnboundedFrom(String key, double from) {
        addRange(new Range(key, from, null));
        return this;
    }


    /**
     * Add a new range with no upper bound, but the key will be
     * computed automatically.
     *
     * @param from
     * @return
     */
    public RangeAggBuilder addUnboundedFrom(double from) {
        return addUnboundedFrom(null, from);
    }

    public RangeAggBuilder addRange(Range range) {
        if (range == null) {
            throw new IllegalArgumentException("[range] must not be null: [" + name + "]");
        }
        ranges.add(range);
        return this;
    }

    public static class Range {
        private String key;
        private final double from;
        private final double to;

        public Range(String key, Double from, Double to) {
            this.key = key;
            if (StringUtils.isBlank(key)){
                this.key = Objects.toString(from, "") + "-" + Objects.toString(to, "");
            }
            this.from = from == null ? Double.NEGATIVE_INFINITY : from;
            this.to = to == null ? Double.POSITIVE_INFINITY : to;
        }

        public String getKey() {
            return key;
        }

        public double getFrom() {
            return from;
        }

        public double getTo() {
            return to;
        }
    }
}
