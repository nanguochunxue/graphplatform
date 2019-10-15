package com.haizhi.graph.common.key;

/**
 * Created by chengmo on 2018/1/2.
 */
public interface KeyHasher {

    long toLong(String md5);

    String getPartitionHash(String md5);
}
