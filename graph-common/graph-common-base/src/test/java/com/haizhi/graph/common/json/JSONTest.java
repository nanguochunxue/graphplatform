package com.haizhi.graph.common.json;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * Created by chengmo on 2018/1/3.
 */
public class JSONTest {

    @Test
    public void toJSONString(){
        User user = new User("user1", 18);
        System.out.println(JSON.toJSONString(user, true));

        // default ignore null
        user = new User(null, 18);
        System.out.println(JSON.toJSONString(user, true));
    }
}
