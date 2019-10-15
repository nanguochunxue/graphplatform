package com.haizhi.graph.server.tiger.util;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghaiyang on 2019/3/14.
 */
public class MapToJsonTest {

    @Test
    public void transform(){
        Map<String,Object> map = new HashMap<>();

        map.put("aa", 11);
        map.put("name", "dfffff");

        Map<String,Object> map1 = new HashMap<>();
        map.put("map", map1);
        map1.put("age", 14);
        map1.put("city", "shenzhen");

//        System.out.println(map.toString());

        System.out.println(JSON.toJSONString(map));

//        JSONObject jsonObject = (JSONObject)map;
//        System.out.println(JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat));

    }


}
