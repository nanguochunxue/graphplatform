package com.haizhi.graph.plugins.etl.gp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by chengangxiong on 2019/04/18
 */
public class CallPerlScriptTest {

    @Test
    public void test() throws IOException, InterruptedException {
        String scriptPath = this.getClass().getClassLoader().getResource("hello.perl").getPath();
        Process process = Runtime.getRuntime().exec("perl " + scriptPath);

        byte[] res = new byte[5];
        process.getInputStream().read(res);
        process.waitFor();
        int exitValue = process.exitValue();

        System.out.println("exitValue: " + exitValue);
        System.out.println(new String(res));
    }

    public static final String SELECT = "select";
    public static final String FROM = "from";
    public static final String WHERE = "where";
    @Test
    public void test3(){
        String sql = "select a,b,c from aaaa where a='a'";
        int selectPos = StringUtils.indexOfIgnoreCase(sql, SELECT);
        int fromPos = StringUtils.indexOfIgnoreCase(sql, FROM);
        int wherePos = StringUtils.indexOfIgnoreCase(sql, WHERE);

        if (selectPos == -1 || fromPos == -1 || wherePos == -1){

        }
        String fields = sql.substring(selectPos + SELECT.length(), fromPos);
        String table = sql.substring(fromPos + FROM.length(), wherePos);
        String filter = sql.substring(wherePos + WHERE.length());

        System.out.println(fields.trim());
        System.out.println(table.trim());
        System.out.println(filter.trim());
    }

}
