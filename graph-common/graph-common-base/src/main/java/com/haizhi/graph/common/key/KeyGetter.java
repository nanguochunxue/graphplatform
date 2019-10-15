package com.haizhi.graph.common.key;

import java.util.Map;

/**
 * Created by chengmo on 2018/1/2.
 */
public interface KeyGetter {

    /**
     *  Get long key string.
     *
     * @param key   ->Example: 79639cb8b90edfe0b429502697d98bd8
     *                      or Company/79639cb8b90edfe0b429502697d98bd8
     * @return  ->Example: -2884508745827238140
     *                  or Company/3908240077063702273
     */
    String getLongKey(String key);

    /**
     * Get row key string for entity.
     *
     * @param objectKey     ->Example: eff1213f8a5d883a86f5d6b5fbe20e3c
     * @return  ->Example: 060#eff1213f8a5d883a86f5d6b5fbe20e3c
     */
    String getRowKey(String objectKey);

    /**
     * Get row key string for edge.
     *
     * @param objectKey     ->Example: eff1213f8a5d883a86f5d6b5fbe20e3c
     * @param fromKey       ->Example: Company/79639cb8b90edfe0b429502697d98bd8
     * @param toKey         ->Example: Person/6375c3b7421d28dd51647cd126c0747c
     * @return  ->Example: 555#Company/79639cb8b90edfe0b429502697d98bd8~Person/6375c3b7421d28dd51647cd126c0747c#eff1213f8a5d883a86f5d6b5fbe20e3c
     */
    String getRowKey(String objectKey, String fromKey, String toKey);

    /**
     * Get row key string for stats edge.
     *
     * @param objectKey     ->Example: eff1213f8a5d883a86f5d6b5fbe20e3c
     * @param fromKey       ->Example: Company/79639cb8b90edfe0b429502697d98bd8
     * @param toKey         ->Example: Person/6375c3b7421d28dd51647cd126c0747c
     * @param createTime    ->Example: 1510675200
     * @return ->Example: 555#Company/79639cb8b90edfe0b429502697d98bd8~Person/6375c3b7421d28dd51647cd126c0747c#1510675200#eff1213f8a5d883a86f5d6b5fbe20e3c
     */
    String getRowKey(String objectKey, String fromKey, String toKey, String createTime);

    /**
     * Get row key string for stats result edge.
     *
     * @param fromKey       ->Example: Company/79639cb8b90edfe0b429502697d98bd8
     * @param toKey         ->Example: Person/6375c3b7421d28dd51647cd126c0747c
     * @param dateRange     ->Example: 2017-11-15|2017-11-17
     * @return  ->Example: 555#Company/79639cb8b90edfe0b429502697d98bd8~Person/6375c3b7421d28dd51647cd126c0747c#1510675200~1510848000
     */
    String getSummaryRowKey(String fromKey, String toKey, String dateRange);

    /**
     * Get row key string for edge with operationFlag.
     *
     * @param data
     * @return
     */
    String getRowKey(Map<String, String> data);

    /**
     * Gets history table rowKey.
     *
     * @param objectKey     ->Example: EFF1213F8A5D883A86F5D6B5FBE20E3C
     * @return      ->Example: ${_hash(objectKey)} + ${currentDate} + objectKey
     *              476#2018-03-26T10#EFF1213F8A5D883A86F5D6B5FBE20E3C
     */
    String getHistoryRowKey(String objectKey);
}
