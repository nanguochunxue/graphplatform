package com.haizhi.graph.common.util;

import org.junit.Test;

import java.util.Date;

/**
 * Created by chengmo on 2017/12/15.
 */
public class DateUtilsTest {

    @Test
    public void example(){
        //转化为本地时间戳
        System.out.println(DateUtils.toLocalMillis("2017-12-14"));
        System.out.println(DateUtils.toLocalMillis("2017-12-14 10:52:10"));
        System.out.println(DateUtils.toLocalMillis("2017-12-14T10:52:10+0800"));
        System.out.println(DateUtils.toLocalMillis("2017-12-14T10:52:10+0100"));

        //时间转UTC（ISO-8601）
        System.out.println(DateUtils.toUTC("2017-12-14"));
        System.out.println(DateUtils.toUTC("2017-12-14 10:52:10"));

        //UTC转本地时间
        System.out.println(DateUtils.utc2Local("2017-12-14T10:52:10+0800"));
        System.out.println(DateUtils.utc2Local("2017-12-14T10:52:10+0100"));

        //格式化本地时间
        System.out.println(DateUtils.formatLocal(new Date()));
        System.out.println(DateUtils.formatLocal(System.currentTimeMillis()));
    }
}
