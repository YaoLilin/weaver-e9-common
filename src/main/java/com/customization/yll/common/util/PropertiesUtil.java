package com.customization.yll.common.util;


import com.customization.yll.common.exception.PropNotConfigureException;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import weaver.file.Prop;
import weaver.general.GCONST;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author 姚礼林
 * @desc 获取配置文件工具类
 * @date 2024/5/22
 */
public class PropertiesUtil {
    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
    private static final String PREFIX_CACHE_KEY = "config:";
    public static final int DEFAULT_EXPIRE_THREE_MINUTES = 60 * 3;

    private PropertiesUtil() {

    }

    /**
     * 获取配置文件，解决了配置文件中文乱码问题
     *
     * @param propFieldName 配置文件名称，不需要带扩展名后缀
     * @return Properties对象
     */
    public static Properties fetchProperties(String propFieldName) {
        Properties prop = new Properties();
        String path = GCONST.getPropertyPath() + propFieldName + ".properties";
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            prop.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("获取配置文件失败", e);
        }
        return prop;
    }

    /**
     * 将配置文件中的配置项转换成Map，例如可将这些字符串转换为map：a:1,b:2,c:3 ，转换后的map为：{a:1,b:2,c:3}
     *
     * @param propValue           配置项值
     * @param itemSplitPatten     项目分隔符，分隔每个map的item，例如 a:1,b:2,c:3 的分隔符就是","
     * @param keyValueSplitPatten key与value分隔符，分隔每个map的key与value，例如 a:1 的分隔符就是":"
     * @return 根据字符串生成的map
     */
    public static Map<String, String> valueConvertToMap(String propValue, @NotNull String itemSplitPatten,
                                                        @NotNull String keyValueSplitPatten) {
        if (propValue.isEmpty()) {
            return Collections.emptyMap();
        }
        String[] pairs = propValue.split(itemSplitPatten);
        return Arrays.stream(pairs).collect(HashMap::new, (map, i) -> {
            String[] item = i.split(keyValueSplitPatten);
            if (item.length == 2) {
                String key = item[0].trim();
                String value = item[1].trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    map.put(key, value);
                }
            }
        }, HashMap::putAll);
    }

    /**
     * 获取配置文件属性值
     * @param fileName 配置文件名称
     * @param propName 属性名称
     * @param required 是否必需，如果是必需，则属性值为空时将抛出异常
     * @exception PropNotConfigureException 属性值为空异常
     * @return 属性值
     */
    public static String getPropValue(String fileName, String propName, boolean required)
            throws PropNotConfigureException {
        String value = Prop.getPropValue(fileName, propName);
        if (required && StringUtils.isEmpty(value)) {
            throw new PropNotConfigureException("配置文件 " + fileName + " [" + propName + "] 属性未配置");
        }
        return value;
    }

    /**
     * 获取配置文件属性值，处理配置文件中的中文
     * @param fileName 配置文件名称
     * @param propName 属性名称
     * @param required 是否必需，如果是必需，则属性值为空时将抛出异常
     * @exception PropNotConfigureException 属性值为空异常
     * @return 属性值
     */
    public static String getPropValueWithChineseHandle(String fileName, String propName, boolean required)
            throws PropNotConfigureException {
        Properties properties = fetchProperties(fileName);
        String value = properties.getProperty(propName);
        if (required && StringUtils.isEmpty(value)) {
            throw new PropNotConfigureException("配置文件 " + fileName + " [" + propName + "] 属性未配置");
        }
        return value;
    }

    /**
     * 获取配置文件属性值
     * @param fileName 配置文件名称
     * @param propName 属性名称
     * @param required 是否必需，如果是必需，则属性值为空时将抛出异常
     * @param cache 是否缓存，默认缓存时间 {@link #DEFAULT_EXPIRE_THREE_MINUTES}
     * @exception PropNotConfigureException 属性值为空异常
     * @return 属性值
     */
    public static String getPropValue(String fileName, String propName, boolean required,
                                      boolean cache)
            throws PropNotConfigureException {
        if (cache) {
            String cacheKey = getCacheKey(fileName, propName);
            Object  cacheValue = CacheUtil.getCache(cacheKey);
            if (cacheValue != null) {
                return (String) cacheValue;
            }
            String value = getPropValue(fileName, propName, required);
            CacheUtil.putCache(cacheKey, value, DEFAULT_EXPIRE_THREE_MINUTES);
            return value;
        }
        return getPropValue(fileName, propName, required);
    }

    /**
     * 获取配置文件属性值
     * @param fileName 配置文件名称
     * @param propName 属性名称
     * @param required 是否必需，如果是必需，则属性值为空时将抛出异常
     * @param cache 是否缓存
     * @param expireSeconds 缓存过期时间,单位秒
     * @exception PropNotConfigureException 属性值为空异常
     * @return 属性值
     */
    public static String getPropValue(String fileName, String propName, boolean required,
                                      boolean cache,int expireSeconds)
            throws PropNotConfigureException {
        if (cache) {
            String cacheKey = getCacheKey(fileName, propName);
            Object  cacheValue = CacheUtil.getCache(cacheKey);
            if (cacheValue != null) {
                return (String) cacheValue;
            }
            String value = getPropValue(fileName, propName, required);
            CacheUtil.putCache(cacheKey, value, expireSeconds);
            return value;
        }
        return getPropValue(fileName, propName, required);
    }

    /**
     * 获取配置文件属性值，处理配置文件中的中文
     * @param fileName 配置文件名称
     * @param propName 属性名称
     * @param required 是否必需，如果是必需，则属性值为空时将抛出异常
     * @param cache 是否缓存
     * @param expireSeconds 缓存过期时间,单位秒
     * @exception PropNotConfigureException 属性值为空异常
     * @return 属性值
     */
    public static String getPropValueWithChineseHandle(String fileName, String propName, boolean required,
                                      boolean cache,int expireSeconds)
            throws PropNotConfigureException {
        if (cache) {
            String cacheKey = getCacheKey(fileName, propName);
            Object cacheValue = CacheUtil.getCache(cacheKey);
            if (cacheValue != null) {
                return (String) cacheValue;
            }
            String value = getPropValueWithChineseHandle(fileName, propName, required);
            CacheUtil.putCache(cacheKey, value, expireSeconds);
            return value;
        }
        return getPropValueWithChineseHandle(fileName, propName, required);
    }

    /**
     * 获取配置文件属性值
     * @param fileName 配置文件名称
     * @param propName 属性名称
     * @return 属性值
     */
    public static String getPropValue(String fileName, String propName) {
        return getPropValue(fileName, propName, false);
    }

    /**
     * 判断指定的流程id是否属于配置文件中配置的流程id的当前版本id或者后面流程版本的id，配置文件中的流程id格式为单个流程id或者多个流程id，
     * 多个流程id的格式如：324,256,111 ，如果是单个流程id，则判断指定的流程id是否等于配置文件中的流程id，
     * 或者是配置的流程id的后面的流程版本，当配置的流程id为多个流程id时，则判断指定的流程id是否属于配置的流程id
     * @param workflowId 要判断的流程id
     * @param workflowIdsOnConf 配置的流程id
     * @return 流程id是否属于配置文件中配置的流程id的当前版本id或者后面流程版本的id
     */
    public static boolean isCurrentOrAfterVersionWorkflow(int workflowId, String workflowIdsOnConf) {
        if (workflowIdsOnConf.contains(",")) {
            String[] split = workflowIdsOnConf.split(",");
            for (String s : split) {
                if (s.equals(String.valueOf(workflowId))) {
                    return true;
                }
            }
        } else {
            return WorkflowUtil.isCurrentOrAfterWorkflowVersion(Integer.parseInt(workflowIdsOnConf), workflowId);
        }
        return false;
    }

    @NotNull
    private static String getCacheKey(String fileName, String propName) {
        return PREFIX_CACHE_KEY + fileName + ":" + propName;
    }

}
