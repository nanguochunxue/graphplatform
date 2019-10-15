package com.haizhi.graph.tag.analytics;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chengmo on 2018/3/15.
 */
public class Test {

    public static void main(String[] args) {
        String str = "count(get_json_object(value, $.count(reg_mount1)))";
        Matcher matcher = Pattern.compile("\\$\\.\\w+\\(\\w+\\)").matcher(str);
        while (matcher.find()){
            System.out.println(matcher.group());
            String s = matcher.group();
            s = StringUtils.substringAfter(s, ".");
            System.out.println(s);
        }
        str = "count(get_json_object(t.value,'$.count(1)'))";
        System.out.println(str.replaceAll("\\w+\\.", ""));
        str = "date_format";
        System.out.println(StringUtils.substringBefore(str, "("));

        String sql = "select t.name AS name," +
                "date_format(t.reg_date,'yyyy-MM-dd') AS statTime," +
                "count(1)," +
                "min(t.reg_amount) " +
                "from Company t " +
                "where t.reg_amount > 500000000 " +
                "and t.reg_date > date_sub(current_date(), 365*10) " +
                "group by t.name,date_format(t.reg_date,'yyyy-MM-dd')";
        System.out.println(sql);

        String json = "{\"count(1)\":3,\"min(reg_amount)\":1.458E9}";
        json = json.replaceAll("\\(|\\)", "\\$");
        System.out.println(json);
        Map<String, Object> valueMap = JSON.parseObject(json, Map.class);
        System.out.println(valueMap);

        json = "{\"tagIds\": [3,5,7]}";
        valueMap = JSON.parseObject(json, Map.class);
        System.out.println(valueMap);

        String rule = "@field(1) > 10000 and @field(2) in (央企，国企) " +
                "and @yearAgo(@field(3)) < 10 and @tag(3.value) and @tag(4.value.count$1$)";
        matcher = Pattern.compile("@((field)|(tag))\\([A-Za-z0-9_.$]+\\)").matcher(rule);
        while (matcher.find()){
            System.out.println(matcher.group());
        }
        System.out.println();
    }
}
