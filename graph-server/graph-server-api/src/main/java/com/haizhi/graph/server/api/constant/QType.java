package com.haizhi.graph.server.api.constant;

/**
 * Created by chengmo on 2018/1/5.
 */
public enum QType {
    TERM {
        @Override
        public String toString() {
            return "term";
        }
    },
    RANGE {
        @Override
        public String toString() {
            return "range";
        }
    },
    MATCH {
        @Override
        public String toString() {
            return "match";
        }
    },
    EXISTS {
        @Override
        public String toString() {
            return "exists";
        }
    }
}
