package com.haizhi.graph.search.api.gdb.query;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.constant.LogicOperator;
import com.haizhi.graph.common.exception.UnexpectedStatusException;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.search.api.gdb.constant.GdbSearchStatus;
import com.haizhi.graph.server.api.constant.GOperator;
import com.haizhi.graph.server.api.gdb.search.query.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2019/1/22.
 */
public class GdbFilterBuilder {

    private static final GLog LOG = LogFactory.getLogger(GdbFilterBuilder.class);

    private static final String LOGIC_OPERATOR = "logicOperator";
    private static final String RULES = "rules";
    private static final String SCHEMA = "schema";
    private static final String SCHEMA_TYPE = "schemaType";
    private static final String VERTEX = "VERTEX";
    private static final String EDGE = "EDGE";
    private static final String FIELD = "field";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String RANGES = "ranges";
    private static final String TERM = "term";
    private static final String RANGE = "range";
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String INCLUDE_LOWER = "includeLower";
    private static final String INCLUDE_UPPER = "includeUpper";

    private static final String FIELD_TYPE = "fieldType";
    private static final String OPERATOR = "operator";


    public static void build(Map<String, Object> filter, GraphQBuilder builder) {
        if (Objects.isNull(filter)) {
            return;
        }
        JSONObject filterTree = new JSONObject(filter);
        builder.setQuery(recursiveParseFilter(filterTree, true));
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private static AbstractQBuilder recursiveParseFilter(JSONObject current, boolean isRoot) {
        BoolQBuilder boolQBuilder = QBuilders.boolQBuilder();
        boolean containLogicOperator = current.containsKey(LOGIC_OPERATOR);
        boolean containRules = current.containsKey(RULES);
        boolean containSchema = current.containsKey(SCHEMA);
        boolean containSchemaType = current.containsKey(SCHEMA_TYPE);
        boolean containField = current.containsKey(FIELD);
        boolean containFieldType = current.containsKey(FIELD_TYPE);
        boolean containOperator = current.containsKey(OPERATOR);
        if( containRules){ // has logic_operators
            String logicOperatorStr;
            LogicOperator logicOperator;
            if(!containLogicOperator) {
                logicOperator = LogicOperator.OR;
            } else {
                logicOperatorStr = getString(LOGIC_OPERATOR, current);
                logicOperator = LogicOperator.valueOf(logicOperatorStr);
            }
            JSONArray rules = getJSONArray(RULES, current);
            for (int i = 0; i < rules.size(); i++) {
                JSONObject rule = rules.getJSONObject(i);
                AbstractQBuilder builder = recursiveParseFilter(rule, false);
                switch (logicOperator) {
                    case AND:
                        boolQBuilder.must(builder);
                        break;
                    case OR:
                        boolQBuilder.should(builder);
                        break;
                }
            }
        }else if(containSchema && containSchemaType && containField && containFieldType && containOperator){
            String schemaType = getString(SCHEMA_TYPE, current);
            String schema = getString(SCHEMA, current);
            BoolQBuilder bool = parseField(current);
            AbstractQBuilder qBuilder;
            switch (schemaType.toUpperCase()) {
                case VERTEX:
                    qBuilder = QBuilders.vertexQBuilder(schema, bool);
                    break;
                case EDGE:
                    qBuilder = QBuilders.edgeQBuilder(schema, bool);
                    break;
                default:
                    throw new IllegalArgumentException("schemaType must be in (vertex,edge)");
            }
            if (isRoot){
                boolQBuilder.must(qBuilder);
                return boolQBuilder;
            }
            return qBuilder;
        }else {
            LOG.error("filterTree has some problem:\n{0}", JSON.toJSONString(current,true));
            throw new UnexpectedStatusException(GdbSearchStatus.GDP_FILTER_ERROR,"GdbFilterBuilder.recursiveParseFilter","filterTree has some problem");
        }
        return boolQBuilder;
    }

    private static BoolQBuilder parseField(JSONObject jsonObject) {
        try {
            String field = getString(FIELD, jsonObject);
            String fieldType = getString(FIELD_TYPE, jsonObject);
            String operator = getString(OPERATOR, jsonObject);
            BoolQBuilder bool = QBuilders.boolQBuilder();
            GOperator gOperator = GOperator.byValue(operator.toUpperCase());
            if(Objects.isNull(gOperator)) {
                LOG.error("gOperator of operator[{0}] is null!", operator);
                return bool;
            }
            Object from;
            Object to;
            boolean includeLower;
            boolean includeUpper;
            Object value;
            JSONObject valueJSONObject;
            List<String> valueList;
            switch (gOperator) {
                case EQ:
                    value = wrapField(fieldType, getString(VALUE, jsonObject));
                    bool.must(QBuilders.termQBuilder(field, value));
                    break;
                case NOT_EQ:
                    value = wrapField(fieldType, getString(VALUE, jsonObject));
                    bool.mustNot(QBuilders.termQBuilder(field, value));
                    break;
                case GT:
                    value = getObject(VALUE, jsonObject);
                    bool.must(QBuilders.rangeQBuilder(field).from(value, false));
                    break;
                case GTE:
                    value = getObject(VALUE, jsonObject);
                    bool.must(QBuilders.rangeQBuilder(field).from(value, true));
                    break;
                case LT:
                    value = getObject(VALUE, jsonObject);
                    bool.must(QBuilders.rangeQBuilder(field).to(value, false));
                    break;
                case LTE:
                    value = getObject(VALUE, jsonObject);
                    bool.must(QBuilders.rangeQBuilder(field).to(value, true));
                    break;
                case IN:
                    valueList = getList(VALUE, jsonObject);
                    bool.must(QBuilders.termsQBuilder(field, valueList));
                    break;
                case NOT_IN:
                    valueList = getList(VALUE, jsonObject);
                    bool.mustNot(QBuilders.termsQBuilder(field, valueList));
                    break;
                case IS_NULL:
                    bool.must(QBuilders.isNullQBuilder(field));
                    break;
                case IS_NOT_NULL:
                    bool.mustNot(QBuilders.isNullQBuilder(field));
                    break;
                case RANGE:
                    valueJSONObject = getJSONObject(VALUE, jsonObject);
                    from = valueJSONObject.get(FROM);
                    to = valueJSONObject.get(TO);
                    includeLower = valueJSONObject.getBooleanValue(INCLUDE_LOWER);
                    includeUpper = valueJSONObject.getBooleanValue(INCLUDE_UPPER);
                    bool.must(QBuilders.rangeQBuilder(field).from(from, includeLower).to(to, includeUpper));
                    break;
                case NOT_RANGE:
                    valueJSONObject = getJSONObject(VALUE, jsonObject);
                    from = valueJSONObject.get(FROM);
                    to = valueJSONObject.get(TO);
                    includeLower = valueJSONObject.getBooleanValue(INCLUDE_LOWER);
                    includeUpper = valueJSONObject.getBooleanValue(INCLUDE_UPPER);
                    bool.mustNot(QBuilders.rangeQBuilder(field).from(from, includeLower).to(to, includeUpper));
                    break;
                case MATCH:
                    value = getString(VALUE, jsonObject);
                    bool.mustNot(QBuilders.likeQBuilder(field, value));
                    break;
                default:
                    String msg = "gOperator [" + gOperator + "]is not exist: ";
                    LOG.error(msg);
                    throw new IllegalArgumentException(msg);
            }
            return bool;
        }catch (Exception e){
            LOG.error("parseField:\n{0}\nexception:\n{0}", JSON.toJSONString(jsonObject,true),e.getMessage());
            throw new UnexpectedStatusException("GdbFilterBuilder.parseField exception",e,e.getMessage());
        }
    }

    private static String wrapField(String fieldType, String value){
        switch (fieldType.toUpperCase()){
            case "STRING":
            case "DATE":
                return "'" + value + "'";
            default:
                return value;
        }
    }

    private static JSONArray getJSONArray(String key, JSONObject jsonObject) {
        JSONArray value = jsonObject.getJSONArray(key);
        preCheck(key,value);
        return value;
    }

    private static String getString(int index, JSONArray jsonArray) {
        String value = jsonArray.getString(index);
        if (StringUtils.isBlank(value)) {
            String msg = "index[" + index + "] value cannot be blank";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return value;
    }

    private static String getString(String key, JSONObject jsonObject) {
        String value = jsonObject.getString(key);
        if (StringUtils.isBlank(value)) {
            String msg = key + " cannot be blank";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return value;
    }

    private static JSONObject getJSONObject(String key, JSONObject jsonObject){
        JSONObject value = (JSONObject)jsonObject.get(key);
        preCheck(key,value);
        return value;
    }

    private static Object getObject(String key, JSONObject jsonObject){
        Object value = jsonObject.get(key);
        preCheck(key,value);
        return value;
    }

    private static List<String> getList(String key, JSONObject jsonObject){
        List<String> value = (List<String>)jsonObject.get(key);
        preCheck(key,value);
        return value;
    }

    private static void preCheck(String key, Object value){
        if (Objects.isNull(value)) {
            String msg = key + " cannot be null";
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

}
