package com.customization.yll.common.util;

import com.cloudstore.api.util.Util_Redis;
import com.cloudstore.dev.api.util.Util_DataCache;
import org.jetbrains.annotations.Nullable;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.IOException;

/**
 * @author yaolilin
 * @desc 缓存工具, 与标准的工具类相比，此缓存工具会优先使用redis进行缓存，如果没有则使用本地缓存
 *       本地缓存增加过期时间
 * @date 2024/11/26
 **/
public class CacheUtil {
    private static final Logger log = LoggerFactory.getLogger(CacheUtil.class);

    private CacheUtil() {

    }

    /**
     * 删除缓存，优先使用redis，如果没有redis则使用本地缓存
     *
     * @param key 缓存key
     */
    public static void deleteCache(String key) {
        if (isRedis()) {
            Util_DataCache.clearValWithRedis(key);
        } else {
            Util_DataCache.clearVal(key);
            Util_DataCache.clearVal(getCacheTimeKey(key));
        }
    }

    /**
     * 存入缓存，优先使用redis，如果没有redis则使用本地缓存。如果是 redis ,缓存对象类必须实现 Serializable 接口，集合类型可以直接存入
     * @param key   缓存key
     * @param value 缓存值
     */
    public static void putCache(String key, Object value) {
        putCache(key, value, null);
    }

    /**
     * 存入缓存，优先使用redis，如果没有redis则使用本地缓存。如果是 redis ,缓存对象类必须实现 Serializable 接口，集合类型可以直接存入
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param seconds 缓存时间，单位为秒
     */
    public static void putCache(String key, Object value, int seconds) {
        putCache(key, value, Integer.valueOf(seconds));
    }

    /**
     * 存入缓存，优先使用redis，如果没有redis则使用本地缓存。如果是 redis ,缓存对象类必须实现 Serializable 接口，集合类型可以直接存入
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param seconds 缓存时间，单位为秒
     */
    private static void putCache(String key, Object value, @Nullable Integer seconds) {
        if (isRedis()) {
            try {
                if (seconds == null) {
                    Util_DataCache.setObjValWithRedis(key, value);
                }else {
                    Util_DataCache.setObjValWithRedis(key, value, seconds);
                }
            } catch (IOException e) {
                log.error("存入缓存失败", e);
                putLocalCache(key, value, seconds);
            }
        } else {
            putLocalCache(key, value, seconds);
        }
    }

    /**
     * 获取缓存，优先使用redis，如果没有redis则使用本地缓存
     *
     * @param key 缓存key
     * @return 缓存值
     */
    @Nullable
    public static Object getCache(String key) {
        if (isRedis()) {
            return Util_DataCache.getObjValWithRedis(key);
        }
        Object cacheValue = Util_DataCache.getObjVal(key);
        Object cacheTime = Util_DataCache.getObjVal(getCacheTimeKey(key));
        if (cacheTime == null) {
            return cacheValue;
        }
        // 获取之前存储的缓存到期时间
        long cacheExpireTime = Long.parseLong(cacheTime.toString());
        if (System.currentTimeMillis() > cacheExpireTime) {
            return null;
        }
        return cacheValue;
    }

    /**
     * 获取缓存，如果缓存不存在则返回默认值，如果没有redis则使用本地缓存
     *
     * @param key           缓存key
     * @param notExistValue 缓存不存在时的默认值
     * @return 缓存值
     */
    public static Object getCache(String key, Object notExistValue) {
        Object value = getCache(key);
        return value == null ? notExistValue : value;
    }

    public static boolean exist(String key) {
        return isRedis() ? Util_DataCache.containsKeyWithRedis(key) : Util_DataCache.containsKey(key);
    }

    /**
     * 判断是否使用redis
     *
     * @return 是否使用redis
     */
    public static boolean isRedis() {
        return Util_Redis.getInstance() != null;
    }

    /**
     * 获取本地缓存的过期时间，如果没有则返回-1
     * @param key 缓存key
     * @return 缓存过期时间，单位秒，如果缓存不存在则返回-1
     */
    public static int getLocalCacheExpire(String key) {
        Object value = Util_DataCache.getObjVal(getCacheTimeKey(key));
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value.toString());
    }

    private static void putLocalCache(String key, Object value, @Nullable Integer seconds) {
        Util_DataCache.setObjVal(key, value);
        if (seconds != null) {
            long expireTime = System.currentTimeMillis() + seconds * 1000L;
            Util_DataCache.setObjVal(getCacheTimeKey(key), expireTime + "");
        }
    }

    private static String getCacheTimeKey(String key) {
        return key + "_cache_time";
    }
}
