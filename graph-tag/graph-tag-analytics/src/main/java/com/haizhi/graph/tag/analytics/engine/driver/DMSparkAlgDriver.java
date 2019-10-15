package com.haizhi.graph.tag.analytics.engine.driver;

import com.alibaba.fastjson.JSON;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import com.haizhi.graph.engine.flow.action.LauncherDriver;
import org.apache.spark.sql.SparkSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chengmo on 2018/4/4.
 */
public class DMSparkAlgDriver extends LauncherDriver {

    private static final GLog LOG = LogFactory.getLogger(DMSparkAlgDriver.class);

    public static void main(String[] args) throws Exception {
        run(DMSparkAlgDriver.class, args);
    }

    @Override
    protected void run(String[] args) throws Exception {
        SparkSession spark = this.getOrCreate(true);
        Map<String, String> map = this.getAlgFlowTask(args);
        runAlgTask(map, spark);
        spark.stop();
    }


    private Map<String, String> getAlgFlowTask(String[] args) {
        Map map = JSON.parseObject(args[0]);
        Map<String, String> algMap = new HashMap<>();
        map.keySet().forEach(e -> algMap.put(e.toString(), map.get(e).toString()));
        return algMap;
    }

    private void runAlgTask(Map<String, String> map, SparkSession spark) {
        String algName = map.get("alg");

    }

}
