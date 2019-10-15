package com.haizhi.graph.engine.flow.action;

import com.haizhi.graph.engine.flow.conf.BaseFlowTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Created by chengmo on 2018/3/14.
 */
public abstract class LauncherDriver implements Serializable {

    protected boolean debug;
    protected boolean security;

    protected static void run(Class<? extends LauncherDriver> klass, String[] args) throws Exception {
        LauncherDriver driver = klass.newInstance();
        driver.run(args);
    }

    protected abstract void run(String[] args) throws Exception;

    protected SparkSession getOrCreate() {
        return this.getOrCreate(false);
    }

    protected SparkSession getOrCreate(boolean enableHiveSupport) {
        return this.getOrCreate((Configuration) null, enableHiveSupport);
    }

    /**
     * @param task
     * @param enableHiveSupport
     * @return
     */
    protected SparkSession getOrCreate(BaseFlowTask task , boolean enableHiveSupport) {
        String hadoopConfDir = task.getHadoopConfDir();
        if (StringUtils.isNotBlank(hadoopConfDir)){
            File dir = new File(hadoopConfDir);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith("-site.xml");
                    }
                });
                Configuration conf = new Configuration();
                if (Objects.nonNull(files)) {
                    for (File file : files) {
                        conf.addResource(new Path(file.getPath()));
                    }
                }
                return this.getOrCreate(conf, enableHiveSupport);
            }
        }
        return this.getOrCreate(enableHiveSupport);
    }

    /**
     * @param conf
     * @param enableHiveSupport
     * @return
     */
    protected SparkSession getOrCreate(Map<String, String> conf, boolean enableHiveSupport) {
        return this.getOrCreate(conf.entrySet(), enableHiveSupport);
    }

    /**
     * @param conf
     * @param enableHiveSupport
     * @return
     */
    protected SparkSession getOrCreate(Iterable<Map.Entry<String, String>> conf, boolean enableHiveSupport) {
        SparkSession.Builder builder = SparkSession.builder()
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .config("spark.rdd.compress", "true");
        if (Objects.nonNull(conf)) {
            for (Map.Entry<String, String> entry : conf) {
                builder.config(entry.getKey(), entry.getValue());
            }
        }
        if (enableHiveSupport) {
            builder.enableHiveSupport();
        }
        if (debug) {
            builder.master("local[*]");
        }
        return builder.getOrCreate();
    }
}
