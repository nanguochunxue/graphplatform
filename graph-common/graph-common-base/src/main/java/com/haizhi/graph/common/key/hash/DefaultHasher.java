package com.haizhi.graph.common.key.hash;

import com.haizhi.graph.common.key.KeyHasher;
import com.haizhi.graph.common.key.KeyUtils;

/**
 * Created by chengmo on 2018/1/2.
 */
public class DefaultHasher implements KeyHasher {

    @Override
    public long toLong(String md5) {
        long h = 0;
        for (int i = 0; i < md5.length(); i++) {
            h = 127 * h + md5.charAt(i);
        }
        return h;
    }

    @Override
    public String getPartitionHash(String md5) {
        long remainder = Long.remainderUnsigned(toLong(md5), KeyUtils.LOGIC_PARTITIONS);
        return KeyUtils.formatPlaceHolder(remainder, KeyUtils.HASH_DIGITS);
    }
}
