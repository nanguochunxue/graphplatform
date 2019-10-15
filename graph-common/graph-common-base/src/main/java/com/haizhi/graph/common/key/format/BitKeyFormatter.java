package com.haizhi.graph.common.key.format;

import com.haizhi.graph.common.key.KeyUtils;

import java.math.BigInteger;

/**
 * Created by chengmo on 2017/12/12.
 */
public class BitKeyFormatter implements KeyFormatter {

    @Override
    public Long toLong(long graphId, long schemaId, String md5) {
        if (graphId > 99 || schemaId > 999 || md5 == null || md5.length() != 32) {
            return -1L;
        }
        // md5(16)
        BigInteger value = new BigInteger(md5.substring(8, 24), 16);
        return graphId << 56 | schemaId << 46 | value.longValue() >> 17;
    }

    @Override
    public String format19Digit(long key) {
        return KeyUtils.formatPlaceHolder(key, 19);
    }

    @Override
    public Long parseGraphId(long key) {
        long value = key >> 56;
        if (value == 0){
            return -1L;
        }
        return value;
    }

    @Override
    public Long parseSchemaId(long key) {
        long value = (key >> 46) & 0x3ff;
        if (value == 0){
            return -1L;
        }
        return value;
    }
}
