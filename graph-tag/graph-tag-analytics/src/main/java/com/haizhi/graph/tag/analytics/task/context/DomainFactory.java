package com.haizhi.graph.tag.analytics.task.context;

import com.haizhi.graph.common.constant.FieldType;
import com.haizhi.graph.common.constant.SchemaType;
import com.haizhi.graph.common.context.Resource;
import com.haizhi.graph.common.context.SpringContext;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.dc.core.bean.Domain;
import com.haizhi.graph.dc.core.bean.Schema;
import com.haizhi.graph.dc.core.bean.SchemaField;
import com.haizhi.graph.engine.flow.tools.hive.HiveHelper;
import com.haizhi.graph.engine.flow.tools.hive.HiveTable;
import com.haizhi.graph.tag.analytics.util.Constants;
import com.haizhi.graph.tag.analytics.util.FieldTypeUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by chengmo on 2018/4/17.
 */
public class DomainFactory {

    private static final GLog LOG = LogFactory.getLogger(DomainFactory.class);

    public static Domain createOnHive(String graphName){
        Domain domain = new Domain(1, graphName);
        try {
            HiveHelper hiveHelper = new HiveHelper(Resource.getActiveProfile());
            List<HiveTable> tables = hiveHelper.describeTables(graphName);

            String mainSchema = SpringContext.getProperty(Constants.SCHEMA_MAIN);
            if (StringUtils.isBlank(mainSchema)){
                String message = MessageFormat.format("Main schema[{0}] Not Configured", mainSchema);
                throw new IllegalArgumentException(message);
            }
            mainSchema = mainSchema.toLowerCase();
            domain.setMainSchema(mainSchema);
            boolean flag = false;
            for (HiveTable table : tables) {
                String tableName = table.getTableName();
                Schema sch = new Schema();
                sch.setType(SchemaType.VERTEX);
                if (tableName.equals(mainSchema)){
                    sch.setType(SchemaType.VERTEX_MAIN);
                    flag = true;
                }
                sch.setGraphName(graphName);
                sch.setSchemaName(tableName);
                for (HiveTable.Field field : table.getFields()) {
                    SchemaField sf = new SchemaField();
                    sf.setField(field.getFieldName());
                    String hiveDataType = field.getDataType();
                    FieldType fieldType = FieldTypeUtils.getFieldTypeByHive(hiveDataType);
                    sf.setType(fieldType);
                    sch.addField(sf);
                }
                domain.addSchema(sch);
            }
            if (!flag){
                LOG.warn("Main schema[{0}] does not exists", mainSchema);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return domain;
    }
}
