package com.haizhi.graph.server.tiger.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by tanghaiyang on 2019/3/6.
 */
public class HttpClientTest {
    public static void main(String[] args) throws Exception{
//        String cmd = "ls";
        StringBuilder cmd = new StringBuilder();
        cmd.append("ls");
        String str = new ObjectMapper().writeValueAsString(cmd);
        System.out.println(str);

        JSONObject ab = new JSONObject();
        ab.put("a","sssss");
        ab.put("b","ffff");

        System.out.println(new ObjectMapper().writeValueAsString(ab));
    }
}
