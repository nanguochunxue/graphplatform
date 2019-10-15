package com.haizhi.graph.common.key;

import com.haizhi.graph.common.key.format.Bit57KeyFormatter;
import com.haizhi.graph.common.key.format.KeyFormatter;
import com.haizhi.graph.common.key.generator.HashKeyGenerator;
import com.haizhi.graph.common.key.generator.KeyGenerator;
import com.haizhi.graph.common.key.getter.HashKeyGetter;
import com.haizhi.graph.common.key.getter.HashKeyScanGetter;
import com.haizhi.graph.common.key.hash.DefaultHasher;

/**
 * Created by chengmo on 2017/12/7.
 */
public class KeyFactory {

    public static KeyGenerator create(long graphId, long schemaSeq) {
        return new HashKeyGenerator(graphId, schemaSeq);
    }

    public static KeyFormatter createFormatter() {
        return new Bit57KeyFormatter();
    }

    public static KeyHasher createKeyHasher() {
        return new DefaultHasher();
    }

    public static KeyGetter createKeyGetter() {
        return new HashKeyGetter();
    }

    public static KeyScanGetter createKeyScanGetter() {
        return new HashKeyScanGetter();
    }
}
