package com.haizhi.graph.server.api.es.index.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengmo on 2018/1/11.
 */
@Data
@NoArgsConstructor
public class ScriptSource {

    private String id;
    private List<Field> normalFields = new ArrayList<>();

    public ScriptSource(String id) {
        this.id = id;
    }

    public ScriptSource updateNormalField(Field field) {
        if (field != null) {
            normalFields.add(field);
        }
        return this;
    }

    public ScriptSource updateNormalFields(List<Field> fields) {
        if (fields != null) {
            normalFields.addAll(fields);
        }
        return this;
    }

    public ScriptSource updateNormalField(String name, Object value) {
        return updateNormalField(name, value, UpdateMode.REPLACE);
    }

    public ScriptSource updateNormalField(String name, Object value, UpdateMode updateMode) {
        if (StringUtils.isNotBlank(name)) {
            normalFields.add(new Field(name, value, updateMode));
        }
        return this;
    }

    public String getUpsertScript() {
        StringBuilder script = new StringBuilder();
        String normalUpdateScript = getNormalUpdateScript();
        if (StringUtils.isNotEmpty(normalUpdateScript)) {
            script.append(normalUpdateScript);
        }
        return script.toString();
    }

    public Map<String, Object> getParams() {
        Map<String, Object> params = new HashMap<>();
        for (Field field : normalFields) {
            params.put(field.getName(), field.getValue());
        }
        return params;
    }

    private String getNormalUpdateScript() {
        if (normalFields == null || normalFields.isEmpty()) {
            return null;
        }

        StringBuilder script = new StringBuilder();
        for (Field field : normalFields) {
            script.append("ctx._source.")
                    .append(field.getName())
                    .append(field.getUpdateMode())
                    .append("params.")
                    .append(field.getName())
                    .append(";");
        }
        return script.toString();
    }

    @Override
    public String toString() {
        return "ScriptSource{" +
                "id='" + id + '\'' +
                ", normalFields=" + normalFields +
                '}';
    }
}
