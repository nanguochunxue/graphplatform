package com.haizhi.graph.tag.core.domain;

/**
 * Created by chengmo on 2018/3/2.
 */
public enum AnalyticsMode {
    /**
     * 逻辑分析，包括单表、一对一多表
     */
    LOGIC {
        @Override
        public String toString() {
            return "logic";
        }
    },

    /**
     * 统计分析
     */
    STAT {
        @Override
        public String toString() {
            return "stat";
        }
    },

    /**
     * 挖掘分析，包括图挖掘等
     */
    DM {
        @Override
        public String toString() {
            return "dm";
        }
    }
}
