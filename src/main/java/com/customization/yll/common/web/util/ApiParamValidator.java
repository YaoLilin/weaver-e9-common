package com.customization.yll.common.web.util;

import com.customization.yll.common.exception.FieldValueEmptyException;
import com.customization.yll.common.web.ApiModel;
import com.customization.yll.common.web.ApiParam;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author 姚礼林
 * @desc ApiParam 注解字段校验工具类，用于校验使用 @ApiParam 注解的字段是否满足required要求
 * 支持嵌套对象的递归校验：
 * 1. 对于直接作为字段的嵌套对象，只有当该对象的类使用了 @ApiModel 注解时，才会对该对象进行校验
 * 2. 对于List、Map、数组等集合类型中的元素对象，会继续递归校验（不受 @ApiModel 限制）
 * @date 2025/12/17
 **/
public class ApiParamValidator {

    /**
     * 校验对象中使用@ApiParam注解的字段
     * 如果字段的required=true且字段值为空，则抛出FieldValueEmptyException异常
     * 支持嵌套对象的递归校验
     *
     * @param obj 待校验的对象
     * @throws FieldValueEmptyException 如果必填字段为空，则抛出此异常
     */
    public static void validate(Object obj) throws FieldValueEmptyException {
        // 根对象总是被校验，不需要检查@ApiModel注解
        validate(obj, new HashSet<>(), true);
    }

    /**
     * 递归校验对象中使用@ApiParam注解的字段
     *
     * @param obj            待校验的对象
     * @param visitedObjects 已访问的对象集合，用于防止循环引用
     * @throws FieldValueEmptyException 如果必填字段为空，则抛出此异常
     */
    private static void validate(Object obj, Set<Object> visitedObjects) throws FieldValueEmptyException {
        validate(obj, visitedObjects, false);
    }

    /**
     * 递归校验对象中使用@ApiParam注解的字段
     *
     * @param obj            待校验的对象
     * @param visitedObjects 已访问的对象集合，用于防止循环引用
     * @param forceValidate  是否强制校验（true：不检查@ApiModel注解，直接校验；false：只有类有@ApiModel注解时才校验）
     * @throws FieldValueEmptyException 如果必填字段为空，则抛出此异常
     */
    private static void validate(Object obj, Set<Object> visitedObjects, boolean forceValidate) throws FieldValueEmptyException {
        if (obj == null) {
            return;
        }

        // 防止循环引用
        if (visitedObjects.contains(obj)) {
            return;
        }
        visitedObjects.add(obj);

        // 如果是基本类型或包装类型，不需要递归校验
        if (isPrimitiveOrWrapper(obj.getClass())) {
            return;
        }

        Class<?> clazz = obj.getClass();

        // 如果不是强制校验，则检查类是否有@ApiModel注解
        if (!forceValidate && !clazz.isAnnotationPresent(ApiModel.class)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            ApiParam apiParam = field.getAnnotation(ApiParam.class);
            if (apiParam == null) {
                continue;
            }

            // 获取字段值
            Object fieldValue = getFieldValue(obj, field);

            // 只校验required=true的字段是否为空
            if (apiParam.required()) {
                if (isEmpty(fieldValue)) {
                    String fieldName = field.getName();
                    String description = apiParam.description();
                    throw new FieldValueEmptyException(
                            String.format("字段 [%s] 为必填项，不能为空，字段说明：%s", fieldName,
                                    description == null ? "" : description),
                            fieldName
                    );
                }
            }

            // 无论字段是否必填，只要字段值不为空，都递归校验嵌套对象
            if (fieldValue != null) {
                validateNestedObject(fieldValue, visitedObjects, field.getName(), forceValidate);
            }
        }
    }

    /**
     * 校验嵌套对象（Collection、Map、数组、普通对象）
     *
     * @param value          字段值
     * @param visitedObjects 已访问的对象集合
     * @param fieldName      字段名称，用于错误信息
     * @param forceValidate  是否强制校验（用于Collection/Map/Array中的元素，不受@ApiModel限制）
     * @throws FieldValueEmptyException 如果必填字段为空，则抛出此异常
     */
    private static void validateNestedObject(Object value, Set<Object> visitedObjects, String fieldName, boolean forceValidate)
            throws FieldValueEmptyException {
        // 处理 Collection 类型（List、Set等）
        // Collection中的元素对象，继续递归校验（不受@ApiModel限制）
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            int index = 0;
            for (Object item : collection) {
                if (item != null && !isPrimitiveOrWrapper(item.getClass())) {
                    try {
                        // 强制校验，不检查@ApiModel注解
                        validate(item, visitedObjects, true);
                    } catch (FieldValueEmptyException e) {
                        // 增强错误信息，包含集合索引
                        throw new FieldValueEmptyException(
                                String.format("字段 [%s] 的第 %d 个元素校验失败：%s", fieldName, index, e.getMessage()),
                                e.getFieldName()
                        );
                    }
                }
                index++;
            }
        }
        // 处理 Map 类型
        // Map中的value对象，继续递归校验（不受@ApiModel限制）
        else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object mapValue = entry.getValue();
                if (mapValue != null && !isPrimitiveOrWrapper(mapValue.getClass())) {
                    try {
                        // 强制校验，不检查@ApiModel注解
                        validate(mapValue, visitedObjects, true);
                    } catch (FieldValueEmptyException e) {
                        // 增强错误信息，包含Map的key
                        throw new FieldValueEmptyException(
                                String.format("字段 [%s] 的键 [%s] 对应的值校验失败：%s", fieldName, entry.getKey(), e.getMessage()),
                                e.getFieldName()
                        );
                    }
                }
            }
        }
        // 处理数组类型
        // 数组中的元素对象，继续递归校验（不受@ApiModel限制）
        else if (value.getClass().isArray()) {
            int length = java.lang.reflect.Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object arrayItem = java.lang.reflect.Array.get(value, i);
                if (arrayItem != null && !isPrimitiveOrWrapper(arrayItem.getClass())) {
                    try {
                        // 强制校验，不检查@ApiModel注解
                        validate(arrayItem, visitedObjects, true);
                    } catch (FieldValueEmptyException e) {
                        // 增强错误信息，包含数组索引
                        throw new FieldValueEmptyException(
                                String.format("字段 [%s] 的第 %d 个元素校验失败：%s", fieldName, i, e.getMessage()),
                                e.getFieldName()
                        );
                    }
                }
            }
        }
        // 处理普通对象类型（直接作为字段的对象）
        // 只有当对象的类使用了@ApiModel注解时，才对该对象进行校验
        else if (!isPrimitiveOrWrapper(value.getClass())) {
            Class<?> valueClass = value.getClass();
            // 检查类是否有@ApiModel注解
            if (valueClass.isAnnotationPresent(ApiModel.class)) {
                // 使用forceValidate参数，保持原有的校验逻辑
                validate(value, visitedObjects, forceValidate);
            }
            // 如果没有@ApiModel注解，则不进行递归校验
        }
    }

    /**
     * 判断是否为基本类型或包装类型
     *
     * @param clazz 类型
     * @return true表示为基本类型或包装类型
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Boolean.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == Character.class
                || clazz == java.math.BigDecimal.class
                || clazz == java.math.BigInteger.class
                || clazz == java.util.Date.class
                || clazz == java.sql.Date.class
                || clazz == java.sql.Timestamp.class
                || java.util.Date.class.isAssignableFrom(clazz);
    }

    /**
     * 获取字段值
     *
     * @param obj   对象实例
     * @param field 字段
     * @return 字段值
     */
    private static Object getFieldValue(Object obj, Field field) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问字段: " + field.getName(), e);
        }
    }

    /**
     * 判断值是否为空
     *
     * @param value 待判断的值
     * @return true表示为空，false表示不为空
     */
    private static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }

        // 字符串类型：null或空字符串都视为空
        if (value instanceof String) {
            return StringUtils.isBlank((String) value);
        }

        // 集合类型：null或空集合都视为空
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }

        // 数组类型：null或空数组都视为空
        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value) == 0;
        }

        return false;
    }
}

