package com.haizhi.graph.server.arango.search.parser;

import com.alibaba.fastjson.JSONObject;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.server.api.gdb.search.bean.Operator;
import com.haizhi.graph.server.api.gdb.search.bean.ParseFields;
import com.haizhi.graph.server.api.gdb.search.parser.AbstractQueryParser;
import com.haizhi.graph.server.api.gdb.search.parser.QueryParser;
import com.haizhi.graph.server.api.gdb.search.query.GraphQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.IsNullQBuilder;
import com.haizhi.graph.server.api.gdb.search.query.QBuilderType;
import com.haizhi.graph.server.api.gdb.search.query.TermsQBuilder;
import com.haizhi.graph.server.api.gdb.search.xcontent.XContentBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

/**
 * Created by thomas on 18/1/26.
 */
public abstract class ArangoQueryParser extends AbstractQueryParser implements QueryParser
{
    protected static final GLog LOGGER = LogFactory.getLogger(ArangoQueryParser.class);

    protected static final String DEFAULT_DIRECTION = "ANY";
    protected static final int DEFAULT_MAX_DEPTH = 1;

    /**
     * do some parameter check before query
     * @param object
     * @return
     */
    protected boolean preCheck(JSONObject object)
    {
        QBuilderType type = QBuilderType.fromName(XContentBuilder.getFirstKey(object));
        if(type != QBuilderType.GRAPH)
        {
            LOGGER.warn("QBuilderType: {0}, not graph type. check fail.", type.name());
            return false;
        }
        JSONObject graph = object.getJSONObject(GraphQBuilder.NAME);
        if(CollectionUtils.isEmpty(graph.getJSONArray(GraphQBuilder.START_VERTICES_FIELD)))
        {
            LOGGER.warn("empty start vertices. check fail");
            return false;
        }
        if(CollectionUtils.isEmpty(graph.getJSONArray(GraphQBuilder.VERTEX_TABLES_FIELD)))
        {
            LOGGER.warn("empty vertex tables. check fail");
            return false;
        }
        if(CollectionUtils.isEmpty(graph.getJSONArray(GraphQBuilder.EDGE_TABLES_FIELD)))
        {
            LOGGER.warn("empty edge tables. check fail");
            return false;
        }
        return true;
    }

    @Override
    protected String parseTerms(JSONObject object)
    {
        JSONObject field = object.getJSONObject(TermsQBuilder.NAME);
        String fieldName = XContentBuilder.getFirstKey(field);
        String values = "['" + StringUtils.join(field.getJSONArray(fieldName), "', '") + "']";
        return "$." + fieldName + BL + Operator.IN.get() + BL + values;
    }

    @Override
    protected String parseIsNull(JSONObject object)
    {
        JSONObject field = object.getJSONObject(IsNullQBuilder.NAME);
        String table = field.getString(IsNullQBuilder.TABLE_FIELD);
        String fieldName = field.getString(IsNullQBuilder.FIELD_FIELD);
        // $.fieldName == null
        return table + "." + fieldName + " == null";
    }

    @Override
    protected String parseGraphTable(JSONObject object, String builderName)
    {
        JSONObject table = object.getJSONObject(builderName);
        String tableName = new ArrayList<>(table.keySet()).get(1);
        String tableAlias = table.getString(ParseFields.ALIAS_FIELD);
        String expression = parseObjectTree(table.getJSONObject(tableName));
        expression = expression.replaceAll("\\$", tableAlias);
        return String.format("(%s != null AND %s)", tableAlias, expression);
    }
}

