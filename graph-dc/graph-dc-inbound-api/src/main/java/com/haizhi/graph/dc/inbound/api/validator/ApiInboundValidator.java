package com.haizhi.graph.dc.inbound.api.validator;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.key.Keys;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.common.util.DateUtils;
import com.haizhi.graph.common.util.Getter;
import com.haizhi.graph.dc.core.model.suo.DcInboundDataSuo;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.bean.SchemaField;
import com.haizhi.graph.dc.core.model.po.DcVertexEdgePo;
import com.haizhi.graph.dc.core.service.DcMetadataCache;
import com.haizhi.graph.dc.inbound.api.service.VertexEdgeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2018/8/27.
 */
@Component
public class ApiInboundValidator extends AbstractValidator<DcInboundDataSuo> {

    private static final GLog LOG = LogFactory.getLogger(ApiInboundValidator.class);

    @Autowired
    private DcMetadataCache dcMetadataCache;

    @Autowired
    private VertexEdgeService vertexEdgeService;

    @Override
    protected boolean doValidate(DcInboundDataSuo cuo, ValidatorResult result) {
        String graph = cuo.getGraph();
        String msg;
        if (StringUtils.isBlank(graph)) {
            msg = "graph cannot be blank";
            LOG.audit(msg);
            result.addErrorMsg(msg);
            result.setErrorRows(cuo.getRows());
            return false;
        }
        String schema = cuo.getSchema();
        if (StringUtils.isBlank(schema)) {
            msg = "schema cannot be blank";
            LOG.audit(msg);
            result.addErrorMsg(msg);
            result.setErrorRows(cuo.getRows());
            return false;
        }
        Domain domain = dcMetadataCache.getDomain(graph);
        if (domain.invalid()) {
            msg = "graph not found, " + graph;
            LOG.audit(msg);
            result.addErrorMsg(msg);
            result.setErrorRows(cuo.getRows());
            return false;
        }
        Schema sch = domain.getSchema(schema);
        if (Objects.isNull(sch)) {
            msg = "schema not found, " + schema;
            LOG.audit(msg);
            result.addErrorMsg(msg);
            result.setErrorRows(cuo.getRows());
            return false;
        }
        return checkRows(sch, cuo, result);
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private boolean checkRows(Schema sch, DcInboundDataSuo cuo, ValidatorResult result) {
        List<Map<String, Object>> rows = cuo.getRows();
        boolean isEdge = sch.getType() == SchemaType.EDGE;
        if (CollectionUtils.isEmpty(rows)) {
            result.addErrorMsg("data rows is empty");
            return false;
        }
        String graph = cuo.getGraph();
        String schema = cuo.getSchema();
        for (Iterator<Map<String, Object>> iterator = rows.iterator(); iterator.hasNext(); ) {
            Map<String, Object> row = iterator.next();
            if (!checkExternalKeys(row, isEdge, result)) {
                result.addErrorRow(row);
                iterator.remove();
                continue;
            }
            Map<String, Object> rowCopy = Maps.newHashMap(row);
            if (!checkRow(row, sch, isEdge, result)) {
                result.addErrorRow(rowCopy);
                iterator.remove();
            }
            rowCopy.clear();
            if (isEdge) {
                addVertexEdge(graph, schema, row);
            }
        }
        // all rows validate failed
        if (rows.isEmpty()) {
            LOG.audit("rows is empty");
            return false;
        }
        return true;
    }

    private boolean checkRow(Map<String, Object> row, Schema sch, boolean isEdge, ValidatorResult result) {
        boolean success = true;
        String objectKey = Getter.get(Keys.OBJECT_KEY, row);
        for (Iterator<Map.Entry<String, Object>> iterator = row.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            String field = entry.getKey();
            Object value = entry.getValue();
            if (Keys._ID.equals(field.toLowerCase())) {
                //result.addErrorMsg("ignore the contained field _id");
                iterator.remove();
                continue;
            }
            SchemaField schemaField = sch.getField(field);
            if (Objects.isNull(schemaField)) {
                result.addErrorMsg(MessageFormat.format("ignore the missing schema filed[{0}]", field));
                iterator.remove();
                continue;
            }
            if (!checkDataType(row, schemaField, field, value, result)) {
                result.addErrorMsg(MessageFormat.format("invalid data type[{0}=>{1}] objectKey=[{2}] field=[{3}]",
                        schemaField.getType(), value, objectKey, field));
                success = false;
            }
        }
        int size = row.size();
        // if vertex: key = object_key
        if (!isEdge) {
            if (size <= 1) {
                result.addErrorMsg("vertex must not only object_key");
                return false;
            }
            return success;
        }
        // if edge: key = object_key,from_key,to_key
        if (size < 3) {
            result.addErrorMsg("edge must contain object_key,from_key,to_key");
            return false;
        }
        return success;
    }

    private boolean checkExternalKeys(Map<String, Object> row, boolean isEdge, ValidatorResult result) {
        if (!row.containsKey(Keys.OBJECT_KEY)) {
            result.addErrorMsg("missing field object_key");
            return false;
        }
        Object objectKey = row.get(Keys.OBJECT_KEY);
        if (objectKey instanceof String) {
            String keyString = (String) objectKey;
            if (keyString.contains("/")) {
                result.addErrorMsg("objectKey[" + keyString + "] must not contain a slash[/]");
                return false;
            }
        }
        if (isEdge) {
            Object fromKey = row.get(Keys.FROM_KEY);
            if (!row.containsKey(Keys.FROM_KEY)) {
                result.addErrorMsg("schema is edge; missing field from_key");
                return false;
            }
            if (fromKey instanceof String) {
                String keyString = (String) fromKey;
                if (!keyString.contains("/")) {
                    result.addErrorMsg("fromKey[" + keyString + "] must contain a slash[/]");
                    return false;
                }
            }
            Object toKey = row.get(Keys.TO_KEY);
            if (!row.containsKey(Keys.TO_KEY)) {
                result.addErrorMsg("schema is edge; missing field to_key");
                return false;
            }
            if (toKey instanceof String) {
                String keyString = (String) toKey;
                if (!keyString.contains("/")) {
                    result.addErrorMsg("toKey[" + keyString + "] must contain a slash[/]");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkDataType(Map<String, Object> row, SchemaField schemaField, String key, Object value,
                                  ValidatorResult result) {
        try {
            String fieldValue = Objects.toString(value, "");
            Object parsedValue = null;
            switch (schemaField.getType()) {
                case STRING:
                    break;
                case LONG:
                    try {
                        parsedValue = Long.parseLong(fieldValue);
                    } catch (final NumberFormatException nfe) {
                        return false;
                    }
                    break;
                case DOUBLE:
                    try {
                        parsedValue = Double.parseDouble(fieldValue);
                    } catch (final NumberFormatException nfe) {
                        return false;
                    }
                    break;
                case DATETIME:
                    parsedValue = DateUtils.parseDate(fieldValue);
                    if (Objects.isNull(parsedValue)) {
                        return false;
                    }
            }
            row.put(key, parsedValue == null ? fieldValue : parsedValue);
            return true;
        } catch (Exception e) {
            LOG.error("checkDataType and row: \n{0}", JSON.toJSONString(row, true));
            LOG.error(e);
        }
        return false;
    }

    private void addVertexEdge(String graph, String schema, Map<String, Object> row) {
        try {
            String fromKey = Getter.get(Keys.FROM_KEY, row);
            String toKey = Getter.get(Keys.TO_KEY, row);
            String fromVertex = StringUtils.substringBefore(fromKey, "/");
            String toVertex = StringUtils.substringBefore(toKey, "/");
            vertexEdgeService.addVertexEdge(new DcVertexEdgePo(graph, fromVertex, toVertex, schema));
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
