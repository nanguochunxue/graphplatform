package com.haizhi.graph.server.es.index.bean;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * Created by chengmo on 2018/1/11.
 */
public class ScriptSourceTest {

    @Test
    public void getUpsertScript() {
        ScriptSource source = new ScriptSource("1");

        // =
        source.updateNormalField("stringField", "stringFieldValue");
        // +=
        source.updateNormalField("integerField", 2, UpdateMode.INCREMENT);
        // -=
        source.updateNormalField("longField", 100, UpdateMode.DECREMENT);

        System.out.println(source.getUpsertScript());
        System.out.println(JSON.toJSONString(source.getParams(), true));
    }
}
