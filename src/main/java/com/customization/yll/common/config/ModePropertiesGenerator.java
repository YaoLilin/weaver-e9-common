package com.customization.yll.common.config;

import com.customization.yll.common.exception.ConfigGenerateException;
import com.customization.yll.common.exception.PropNotConfigureException;
import com.customization.yll.common.mode.util.ModeConfigUtil;

import java.lang.reflect.Field;

/**
 * 建模配置属性类生成器。
 * <p>
 * 通过反射扫描目标类中所有带有 {@link ModeConfigProperty} 注解的字段，
 * 自动调用 {@link ModeConfigUtil#getPropValue} 从建模配置中心读取对应的属性值并注入，
 * 最终返回一个已填充属性的配置对象实例。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>
 * TestModeConfigProperties config = ModePropertiesGenerator.generate(TestModeConfigProperties.class);
 * </pre>
 *
 * @author 姚礼林
 * @date 2026/3/25
 */
public class ModePropertiesGenerator {

    private ModePropertiesGenerator() {
    }

    /**
     * 根据目标类字段上的 {@link ModeConfigProperty} 注解生成配置属性对象。
     * <p>
     * 遍历目标类的所有字段，对标注了 {@link ModeConfigProperty} 的字段，通过
     * {@link ModeConfigUtil#getPropValue} 从建模配置中心读取配置值并注入到对应字段中。
     * 注解中的 {@code cache}、{@code expireSeconds}、{@code required} 会透传给 ModeConfigUtil。
     * </p>
     *
     * @param <T>   配置属性类泛型
     * @param clazz 配置属性类的 Class 对象
     * @return 已注入配置值的配置属性对象
     * @throws ConfigGenerateException   实例化或反射赋值失败时抛出此异常
     * @throws PropNotConfigureException 如果某个字段标注了 required=true 且建模中未配置该属性，则抛出此异常
     */
    public static <T> T generate(Class<T> clazz) throws ConfigGenerateException, PropNotConfigureException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                injectFieldIfAnnotated(instance, field);
            }
            return instance;
        } catch (PropNotConfigureException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigGenerateException("生成建模配置对象失败，类：" + clazz.getName(), e);
        }
    }

    /**
     * 如果字段上有 {@link ModeConfigProperty} 注解，则从建模配置中心读取配置值并注入到该字段。
     *
     * @param instance 配置对象实例
     * @param field    待注入的字段
     * @throws PropNotConfigureException 如果 required=true 且配置值为空时抛出
     * @throws IllegalAccessException    反射赋值失败时抛出
     */
    private static void injectFieldIfAnnotated(Object instance, Field field)
            throws PropNotConfigureException, IllegalAccessException {
        ModeConfigProperty annotation = field.getAnnotation(ModeConfigProperty.class);
        if (annotation == null) {
            return;
        }
        String value = fetchValue(annotation, field.getName());
        field.setAccessible(true);
        field.set(instance, value);
    }

    /**
     * 根据 {@link ModeConfigProperty} 注解的参数调用对应的 ModeConfigUtil 重载方法获取配置值。
     * <p>如果注解中的 {@code name} 为空，则使用字段名作为配置项名称。</p>
     *
     * @param annotation 字段上的 ModeConfigProperty 注解
     * @param fieldName  字段名，当注解 name 为空时作为配置项名称的回退值
     * @return 配置属性值
     * @throws PropNotConfigureException 如果 required=true 且配置值为空时抛出
     */
    private static String fetchValue(ModeConfigProperty annotation, String fieldName) throws PropNotConfigureException {
        String configId = annotation.configId();
        String name = annotation.name().isEmpty() ? fieldName : annotation.name();
        boolean required = annotation.required();
        boolean cache = annotation.cache();
        int expireSeconds = annotation.expireSeconds();

        if (cache) {
            return ModeConfigUtil.getPropValue(configId, name, required, true, expireSeconds);
        }
        return ModeConfigUtil.getPropValue(configId, name, required);
    }
}
