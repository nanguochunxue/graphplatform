package com.haizhi.graph.tag.analytics.engine.alg;

/**
 * Created by wangxy on 2018/5/30.
 */


public enum AlgNamesEnum {
    /**
     * 实际控制人
     */
    ActualController("ActualController"),

    /**
     * 控股股东
     */
    MajorityShareHolder("MajorityShareHolder");

    String algName;

    AlgNamesEnum(String algName) {
        this.algName = algName;
    }

    public String getAlgName() {
        return algName;
    }

}
