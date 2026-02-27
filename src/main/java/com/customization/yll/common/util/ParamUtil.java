package com.customization.yll.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.customization.yll.common.IntegrationLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import weaver.general.BaseBean;
import weaver.general.Util;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口参数获取工具，可用于获取接口中的参数，比如请求参数、json参数。
 *
 * @see com.engine.common.util.ParamUtil 引用此标准类
 */
@UtilityClass
public class ParamUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final IntegrationLog LOG = new IntegrationLog(ParamUtil.class);

    public static Map<String, Object> request2Map(HttpServletRequest request) {
        return com.engine.common.util.ParamUtil.request2Map(request);
    }

    public static Map<String, Object> requestJson2Map(HttpServletRequest var0) {
        Object var1 = new HashMap();

        try {
            ServletInputStream var2 = var0.getInputStream();
            ByteArrayOutputStream var3 = new ByteArrayOutputStream();
            byte[] var4 = new byte[1024];
            boolean var5 = false;

            int var6;
            while ((var6 = var2.read(var4)) != -1) {
                var3.write(var4, 0, var6);
            }

            var3.close();
            var2.close();
            String var7 = new String(var3.toByteArray(), "UTF-8");
            if (Util.null2String(var7).length() > 0) {
                var1 = (Map) JSONObject.parseObject(var7, new TypeReference<Map<String, Object>>() {
                }, new Feature[0]);
            }
        } catch (Exception var8) {
            (new BaseBean()).writeLog(var8);
        }

        return (Map) var1;
    }

    /**
     * 使用 jackJson 将输入流转换为指定对象
     *
     * @param jsonStream json 字符串输入流
     * @param type       转换为指定对象的类型
     * @param <T>        指定对象的类型
     * @return 转换结果，如果转换
     */
    @Nullable
    public static <T> T parseJsonToObject(InputStream jsonStream, Class<T> type) {
        try (InputStream is = jsonStream) {
            return MAPPER.readValue(is, type);
        } catch (IOException e) {
            LOG.error("将输入流转换为指定对象发生异常", e);
            return null;
        }
    }

    /**
     * 使用 jackJson 将 request 对象中的输入流转换为指定对象
     *
     * @param request 携带 json 的 request 对象
     * @param type    转换为指定对象的类型
     * @param <T>     指定对象的类型
     * @return 转换结果，如果转换
     */
    @Nullable
    public static <T> T parseJsonToObject(HttpServletRequest request, Class<T> type) {
        ServletInputStream inputStream;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            LOG.error("获取 request 的输入流发生异常", e);
            return null;
        }
        if (inputStream == null) {
            LOG.error("request 中无输入流");
            return null;
        }
        return parseJsonToObject(inputStream, type);
    }

    /**
     * 使用 jackJson 将指定对象转换为 json
     *
     * @param object 需要转换的对象
     * @return json 对象，如果转换失败则返回 null
     */
    public static JSONObject parseObjectToJson(Object object) {
        try {
            String json = MAPPER.writeValueAsString(object);
            return JSON.parseObject(json);
        } catch (JsonProcessingException e) {
            LOG.error("将指定对象转换为 json 发生异常", e);
            return null;
        }
    }
}
