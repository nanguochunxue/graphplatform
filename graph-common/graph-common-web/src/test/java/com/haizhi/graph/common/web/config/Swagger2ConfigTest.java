package com.haizhi.graph.common.web.config;

import com.haizhi.graph.common.context.Module;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by chengmo on 2018/9/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "")
public class Swagger2ConfigTest {

    @Test
    public void initialize(){
        System.out.println(Module.getName());
        System.out.println(Module.getFullName());
    }
}
