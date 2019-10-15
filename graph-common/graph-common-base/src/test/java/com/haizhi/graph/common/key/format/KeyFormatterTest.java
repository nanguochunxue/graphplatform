package com.haizhi.graph.common.key.format;

import com.haizhi.graph.common.key.KeyFactory;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by chengmo on 2017/12/15.
 */
public class KeyFormatterTest {

    @Test
    public void toLong(){
        KeyFormatter keyFormatter = KeyFactory.createFormatter();
        String md5 = "79639cb8b90edfe0b429502697d98bd8";
        long key = keyFormatter.toLong(1,3, md5);
        System.out.println(key);

        String s = "2016-11-152017-11-17";
        System.out.println(s.contains("|"));
        System.out.println(Arrays.asList("2016-11-15|2017-11-17".split("\\|")));
    }
}
