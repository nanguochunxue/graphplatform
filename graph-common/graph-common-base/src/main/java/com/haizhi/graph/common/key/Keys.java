package com.haizhi.graph.common.key;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by chengmo on 2017/12/11
 */
public class Keys {
    /* Key separator */
    public static final String KS1 = "#";
    public static final String KS2 = "|";
    public static final String KS3 = "~";

    /* 用户导入的主键 */
    public static final String FROM_KEY = "from_key";
    public static final String TO_KEY = "to_key";
    public static final String OBJECT_KEY = "object_key";

    /* HBase默认配置*/

    // HBase默认Family
    public static final String OBJECTS = "objects";
    // HBase历史表后缀
    public static final String HISTORY = ".history";

    /* Summary集合需要的字段 */
    public static final String TYPE = "type";
    public static final String CTIME = "ctime";

    /* 系统内部字段 */

    // 通过MD5->LONG自动转换的
    public static final String _KEY = "_key";
    public static final String _FROM = "_from";
    public static final String _TO = "_to";
    public static final String _ID = "_id";

    // Row key for hbase
    public static String _ROW_KEY = "_row_key";

    // 存储融合后的时间区间，取值如：[12,2016-07-05|2017-11-17] 其中12毫无意义，该字段后续会改名
    public static String _TYPE = "type";

    /* 系统内部字段 */

    private static final Set<String> internalKeys = new HashSet<>(Arrays.asList(_KEY, _FROM, _TO));

    public static boolean internalKey(String key) {
        return internalKeys.contains(key);
    }

    private static final Set<String> externalKeys = new HashSet<>(Arrays.asList(FROM_KEY, TO_KEY, OBJECT_KEY, CTIME));

    public static boolean externalKey(String key) {
        return externalKeys.contains(key);
    }

    public static boolean validate(String objectKey){
        return objectKey != null && objectKey.length() >= 32;
    }

    public static boolean validate(String fromKey, String toKey){
        if (StringUtils.isAnyBlank(fromKey, toKey)){
            return false;
        }
        if (!fromKey.contains("/") || !toKey.contains("/")){
            return false;
        }
        return true;
    }
}
