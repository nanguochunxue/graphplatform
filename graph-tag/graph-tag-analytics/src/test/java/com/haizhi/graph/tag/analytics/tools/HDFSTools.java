package com.haizhi.graph.tag.analytics.tools;

import com.haizhi.graph.engine.flow.tools.hdfs.HDFSHelper;
import org.junit.Test;

/**
 * Created by chengmo on 2018/7/28.
 */
public class HDFSTools {

    @Test
    public void upload(){
        System.setProperty("HADOOP_USER_NAME", "admin");
        HDFSHelper helper = new HDFSHelper();
        String localPath = "/Users/haizhi/MyData/Opensource/Hadoop/Spark/spark-2.1.0-bin-hadoop2.7/jars";
        //String localPath = "/Users/haizhi/temp/hdp-2.6.1.0-129/spark2/jars";
        String dstPath = "/user/graph/lib/spark2x.jars/";
        helper.upload(localPath, dstPath);
        helper.close();
    }

    @Test
    public void uploadFile(){
        System.setProperty("HADOOP_USER_NAME", "root");
        HDFSHelper helper = new HDFSHelper();
        String localPath = "/Users/haizhi/1.Work/2.设计/Module/Tag/tag-analytics/0script/tag-demo-script/data/4/to_balance.csv";
        String dstPath = "/user/graph/tag/data/to_balance/";
        helper.upload(localPath, dstPath);
        helper.close();
    }

    @Test
    public void deletePath(){
        System.setProperty("HADOOP_USER_NAME", "admin");
        HDFSHelper helper = new HDFSHelper();
        //String dstPath = "/user/graph/lib/spark2x.jars/";
        String dstPath = "/user/graph/tag/data/to_change_record/";
        helper.delete(dstPath);
        helper.close();
    }

    @Test
    public void listAndPrint(){
        System.setProperty("HADOOP_USER_NAME", "root");
        HDFSHelper helper = new HDFSHelper();
        //String dstPath = "/user/graph/lib/spark2x.jars/";
        String dstPath = "/user/chengmo/tag";
        helper.listAndPrint(dstPath);
        helper.close();
    }

    @Test
    public void upsertLine(){
        System.setProperty("HADOOP_USER_NAME", "root");
        HDFSHelper helper = new HDFSHelper();
        String text = "test";
        helper.upsertLine("/user/chengmo/tag/TAG.DAG=>logic.2_21_1.tags[3]", text);
    }
}
