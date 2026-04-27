package com.customization.yll.common.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.customization.yll.common.IntegrationLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import weaver.general.Util;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public static Map<String, Object> requestJson2Map(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            ServletInputStream inputStream = request.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            String jsonString = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            if (!Util.null2String(jsonString).isEmpty()) {
                resultMap = JSONObject.parseObject(jsonString, new TypeReference<Map<String, Object>>() {
                });
            }
        } catch (Exception e) {
            LOG.error("从 HttpServletRequest 中获取 JSON 参数发生异常", e);
            throw new JsonParseException("从 HttpServletRequest 中获取 JSON 参数发生异常: " + e.getMessage(), e);
        }

        return resultMap;
    }

    /**
     * 从 HttpServletRequest 中获取 JSON 参数
     *
     * @param request HttpServletRequest 对象
     * @return JSON 对象
     */
    public static JSONObject request2JsonObject(HttpServletRequest request) {
        Map<String, Object> map = requestJson2Map(request);
        return new JSONObject(map);
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
