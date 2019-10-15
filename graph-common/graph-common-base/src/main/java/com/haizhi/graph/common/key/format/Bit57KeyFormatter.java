package com.haizhi.graph.common.key.format;

import com.haizhi.graph.common.key.KeyUtils;

import java.math.BigInteger;

/**
 * Created by chengmo on 2017/12/12.
 */
public class Bit57KeyFormatter implements KeyFormatter {

    @Override
    public Long toLong(long graphId, long schemaId, String md5) {
        // 63=111111=(2<<5)-1, 255=11111111=(2<<7)-1
        if (graphId > 63 || schemaId > 255 || md5 == null || md5.length() != 32) {
            return -1L;
        }
        // md5(16)
        BigInteger value = new BigInteger(md5.substring(8, 24), 16);
        long longValue = Math.abs(value.longValue());
        return graphId << 57 | schemaId << 49 | longValue >> 14;
    }

    @Override
    public String format19Digit(long key) {
        return KeyUtils.formatPlaceHolder(key, 19);
    }

    @Override
    public Long parseGraphId(long key) {
        long value = key >> 57;
        if (value == 0) {
            return -1L;
        }
        return value;
    }

    @Override
    public Long parseSchemaId(long key) {
        long value = (key >> 49) & 0xff;
        if (value == 0) {
            return -1L;
        }
        return value;
    }
}
