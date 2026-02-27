package com.customization.yll.common.mode.util;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.exception.PropNotConfigureException;
import com.customization.yll.common.util.CacheUtil;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import weaver.conn.RecordSet;

/**
 * @author 姚礼林
 * @desc 建模统一配置中心配置属性值获取工具类
 * @date 2025/4/8
 **/
@UtilityClass
public class ModeConfigUtil {
    public static final String TABLE_NAME = "uf_config_center";
    public static final String PREFIX_CACHE_KEY = "config:";
    /**
     * 默认缓存过期时间
     */
    public static final int DEFAULT_EXPIRE_TEN_MINUTES = 60 * 3;

    /**
     * 获取配置中的属性值
     * @param configId 配置id
     * @param propName 属性名
     * @return 属性值
     */
    public static String getPropValue(String configId,String propName) {
        RecordSet recordSet = new RecordSet();
        recordSet.executeQuery("select m.value from uf_config_center z join uf_config_center_dt1 m " +
                "on z.id = m.mainid where z.config_id=? and m.name=?",configId,propName);
        recordSet.next();
        return recordSet.getString("value");
    }

    /**
     * 获取配置中的属性值
     * @param configId 配置id
     * @param propName 属性名
     * @param required 属性值是否必需，如果为true，则属性值必需存在，否则抛出异常
     * @return 属性值
     * @throws PropNotConfigureException 属性值不存在异常
     */
    public static String getPropValue(String configId,String propName,boolean required)
            throws PropNotConfigureException {
        String value = getPropValue(configId, propName);
        if (required && StrUtil.isBlank(value)) {
            throw new PropNotConfigureException(String.format("配置id：%s， 属性[%s]未配置值",configId,propName));
        }
        return value;
    }

    /**
     * 获取配置中的属性值，缓存属性值，默认缓存过期时间:{@link #DEFAULT_EXPIRE_TEN_MINUTES}
     * @param configId 配置id
     * @param propName 属性名
     * @param required 属性值是否必需，如果为true，则属性值必需存在，否则抛出异常
     * @param cache 是否缓存属性值
     * @return 属性值
     */
    public static String getPropValue(String configId,String propName,boolean required, boolean cache)
            throws PropNotConfigureException{
        return getPropValue(configId, propName, required,cache,DEFAULT_EXPIRE_TEN_MINUTES);
    }

    /**
     * 获取配置中的属性值
     * @param configId 配置id
     * @param propName 属性名
     * @param required 属性值是否必需，如果为true，则属性值必需存在，否则抛出异常
     * @param cache 是否缓存属性值
     * @param expireSeconds 缓存过期时间
     * @return 属性值
     */
    public static String getPropValue(String configId,String propName,boolean required,
                                      boolean cache,int expireSeconds) throws PropNotConfigureException {
        if (cache) {
            String cacheKey = getCacheKey(configId, propName);
            Object cacheValue = CacheUtil.getCache(cacheKey);
            if (cacheValue != null) {
                return (String) cacheValue;
            }
            String value = getPropValue(configId, propName, required);
            CacheUtil.putCache(cacheKey, value, expireSeconds);
            return value;
        }
        return getPropValue(configId, propName, required);
    }


    @NotNull
    public static String getCacheKey(String configId, String propName) {
        return PREFIX_CACHE_KEY + configId + ":" + propName;
    }
}
