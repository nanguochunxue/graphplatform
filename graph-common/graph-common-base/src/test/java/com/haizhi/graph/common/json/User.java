package com.haizhi.graph.common.json;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by chengmo on 2018/1/3.
 */
public class User {

    @JSONField(name = "NAME")
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
