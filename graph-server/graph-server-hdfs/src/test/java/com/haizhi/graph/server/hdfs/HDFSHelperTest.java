package com.haizhi.graph.server.hdfs;

import org.junit.Test;

/**
 * Created by chengmo on 2018/10/22.
 */
public class HDFSHelperTest {

    @Test
    public void example(){
        doExample("application-haizhi.properties");
    }

    @Test
    public void exampleFI(){
        // replace *-site.xml with fic80/*-site.xml
        doExample("application-haizhi-fic80.properties");
    }

    private void doExample(String activeProfile){
        HDFSHelper helper = new HDFSHelper(activeProfile);
        String text = "test";
        String path = "/user/graph/test/test.txt";
        helper.upsertLine(path, text);
        System.out.println(helper.exsits(path));
        helper.delete(path);
    }
}
