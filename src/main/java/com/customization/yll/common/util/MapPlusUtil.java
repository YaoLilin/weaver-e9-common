package com.customization.yll.common.util;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author 姚礼林
 * @desc Map 工具类，对 cn.hutool.core.map.MapUtil 的补充
 * @date 2025/7/11
 **/
public class MapPlusUtil extends cn.hutool.core.map.MapUtil {

    /**
     * 如果 map 中不存在指定的值，或值无法转换为目标类型，则抛出异常，否则获取键对应的值
     * @param map map
     * @param key key
     * @param type map的值转换到该类型
     * @param exceptionSupplier 获取需要抛出的异常
     * @return map 中的值
     * @param <T> map 值的类型
     * @param <X> 抛出异常的类型
     * @throws X 如果 map 中不存在指定的值，或值无法转换为目标类型，则抛出异常
     */
    public static <T, X extends Throwable> T getOrElseThrow(Map<?, ?> map, Object key, Class<T> type,
                                                            Supplier<? extends X> exceptionSupplier) throws X {
        T value = get(map, key, type);
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }
}
