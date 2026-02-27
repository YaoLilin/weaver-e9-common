package com.customization.yll.common.web.util;

import cn.hutool.core.collection.CollUtil;
import com.weaverboot.frame.ioc.anno.classAnno.WeaIocComponent;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolilin
 * @desc 接口请求工具。注意：OA版本太老可能缺少 okhttp3 依赖，会导致报错
 * @date 2024/12/2
 **/
@WeaIocComponent
public class ApiCallManager {
    private static final Logger log = LoggerFactory.getLogger(ApiCallManager.class);
    private static final int TIME_OUT = 30;
    private final OkHttpClient okHttpClient;

    public ApiCallManager() {
        okHttpClient = new OkHttpClient.Builder()
                // 连接超时
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                // 读取超时
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                // 写入超时
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    /**
     * @param timeOut 接口超时时间,单位秒
     */
    public ApiCallManager(int timeOut) {
        okHttpClient = new OkHttpClient.Builder()
                // 连接超时
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                // 读取超时
                .readTimeout(timeOut, TimeUnit.SECONDS)
                // 写入超时
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .build();
    }

    /**
     * post 接口请求
     *
     * @param apiUrl 接口地址
     * @param body   请求体
     * @return 请求结果
     * @throws IOException io异常
     */
    public Response post(String apiUrl, String body) throws IOException {
        return post(apiUrl, body, null);
    }

    /**
     * post 接口请求
     *
     * @param apiUrl 接口地址
     * @param body   请求体
     * @param header 请求头,注意，如果 Content-Type 没有包含 charset，将会默认添加 charset=utf-8
     * @return 请求结果
     * @throws IOException io异常
     */
    public Response post(String apiUrl, String body, @Nullable Map<String, String> header) throws IOException {
        return post(apiUrl, body, header, null);
    }

    /**
     * post 接口请求
     *
     * @param apiUrl    接口地址
     * @param body      请求体
     * @param header    请求头,注意，如果 Content-Type 没有包含 charset，将会默认添加 charset=utf-8
     * @param mediaType 载体类型，如果为空则获取请求头中的 Content-Type 参数作为载体类型，如果请求头中没有 Content-Type
     *                  则默认为 application/json;charset=utf-8
     * @return 请求结果
     * @throws IOException io异常
     */
    public Response post(String apiUrl, String body, @Nullable Map<String, String> header,
                         @Nullable MediaType mediaType) throws IOException {
        if (mediaType == null) {
            if (header != null && header.get("Content-Type") != null) {
                mediaType = MediaType.parse(header.get("Content-Type"));
            } else {
                mediaType = MediaType.parse("application/json;charset=utf-8");
            }
        }

        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request.Builder builder = new Request.Builder()
                .url(apiUrl)
                .post(requestBody);
        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * post 请求，返回body，如果请求失败会返回空字符串
     *
     * @param apiUrl 接口地址
     * @param body   请求体
     * @return body
     */
    public String postResult(String apiUrl, String body) {
        return postResult(apiUrl, body, null);
    }

    /**
     * post 请求，返回body，如果请求失败会返回空字符串
     *
     * @param apiUrl 接口地址
     * @param body   请求体
     * @param header 请求头
     * @return body
     */
    public String postResult(String apiUrl, String body, Map<String, String> header) {
        try (Response response = post(apiUrl, body, header)) {
            return getBodyContent(response);
        } catch (IOException e) {
            log.error("接口请求发生异常", e);
            return "";
        }
    }

    /**
     * post 请求，返回body，如果请求失败会返回空字符串
     *
     * @param apiUrl    接口地址
     * @param body      请求体
     * @param header    请求头
     * @param mediaType 载体类型，如果为空则获取请求头中的 Content-Type 参数作为载体类型，如果请求头中没有 Content-Type
     *                  则默认为 application/json;charset=utf-8
     * @return body
     */
    public String postResult(String apiUrl, String body, Map<String, String> header, @Nullable MediaType mediaType) {
        try (Response response = post(apiUrl, body, header, mediaType)) {

            return getBodyContent(response);
        } catch (IOException e) {
            log.error("接口请求发生异常", e);
            return "";
        }
    }

    /**
     * 获取响应体的内容。
     *
     * @param response 服务器的响应对象
     * @return 响应体的内容，如果请求失败或响应体为空，则返回空字符串
     * @throws IOException 如果读取响应体时发生IO异常
     */
    @NotNull
    public static String getBodyContent(Response response) throws IOException {
        if (!response.isSuccessful()) {
            log.error("接口请求失败：" + response);
        }
        if (response.body() == null) {
            return "";
        }
        return response.body().string();
    }

    /**
     * GET 接口请求
     *
     * @param apiUrl 接口地址
     * @return 请求结果
     * @throws IOException io异常
     */
    public Response get(String apiUrl) throws IOException {
        return get(apiUrl, null);
    }

    /**
     * GET 接口请求
     *
     * @param apiUrl 接口地址
     * @param params 参数
     * @return 请求结果
     * @throws IOException io异常
     */
    public Response get(String apiUrl, Map<String, String> params) throws IOException {
        return get(apiUrl, params, null);
    }

    /**
     * GET 接口请求
     *
     * @param apiUrl 接口地址
     * @param params 参数
     * @param header 请求头
     * @return 请求结果
     * @throws IOException io异常
     */
    public Response get(String apiUrl, @Nullable Map<String, String> params, @Nullable Map<String, String> header)
            throws IOException {
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        if (CollUtil.isNotEmpty(params)) {
            if (apiUrl.contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name())).append("&");
            }
            urlBuilder.delete(urlBuilder.length() - 1, urlBuilder.length());
        }

        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.toString())
                .get();
        if (CollUtil.isNotEmpty(header)) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * GET 请求，返回body，如果请求失败会返回空字符串
     *
     * @param apiUrl 接口地址
     * @return body
     */
    public String getResult(String apiUrl) {
        return getResult(apiUrl, null);
    }


    /**
     * GET 请求，返回body，如果请求失败会返回空字符串
     *
     * @param apiUrl 接口地址
     * @param params 参数
     * @return body
     */
    public String getResult(String apiUrl, Map<String, String> params) {
        return getResult(apiUrl, params, null);
    }

    /**
     * GET 请求，返回body，如果请求失败会返回空字符串
     *
     * @param apiUrl 接口地址
     * @param params 参数
     * @param header 请求头
     * @return body
     */
    public String getResult(String apiUrl, Map<String, String> params, Map<String, String> header) {
        try (Response response = get(apiUrl, params, header)) {
            return getBodyContent(response);
        } catch (IOException e) {
            log.error("接口请求发生异常", e);
            return "";
        }
    }

    /**
     * GET 请求，返回body，如果请求失败会返回空字符串。该方法已经弃用，请使用 getResult
     *
     * @param apiUrl 接口地址
     * @return body
     * @deprecated 请使用 getResult
     */
    @Deprecated
    public String getApiResult(String apiUrl) {
        return getResult(apiUrl, null);
    }

    /**
     * GET 请求，返回body，如果请求失败会返回空字符串。该方法已经弃用
     *
     * @param apiUrl 接口地址
     * @param params 参数
     * @return body
     * @deprecated 请使用 getResult
     */
    @Deprecated
    public String getApiResult(String apiUrl, Map<String, String> params) {
        return getResult(apiUrl, params, null);
    }

    /**
     * GET 请求，返回body，如果请求失败会返回空字符串。该方法已经弃用，请使用 getResult
     *
     * @param apiUrl 接口地址
     * @param params 参数
     * @param header 请求头
     * @return body
     * @deprecated 请使用 getResult
     */
    @Deprecated
    public String getApiResult(String apiUrl, Map<String, String> params, Map<String, String> header) {
        return getResult(apiUrl, params, header);
    }

    /**
     * 文件上传请求 <br>
     * <p>
     * 使用示例：
     * <pre>{@code
     * // 构建参数列表
     * List<MultipartBody.Part> parts = new ArrayList<>();
     * parts.add(MultipartBody.Part.createFormData("category", "123"));
     *
     * // 添加文件
     * File file = new File("/path/to/file.doc");
     * RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
     * parts.add(MultipartBody.Part.createFormData("file", "document.doc", fileBody));
     *
     * // 添加其他参数
     * parts.add(MultipartBody.Part.createFormData("name", "document.doc"));
     * parts.add(MultipartBody.Part.createFormData("description", "测试文件"));
     *
     * // 构建请求头
     * Map<String, String> headers = new HashMap<>();
     * headers.put("Authorization", "Bearer token");
     *
     * // 调用上传方法
     * Response response = apiCallManager.uploadFile("http://api.example.com/upload", parts, headers);
     * }</pre>
     *
     * @param apiUrl 接口地址
     * @param parts  多部分请求体参数列表
     * @param header 请求头
     * @return 请求结果
     * @throws IOException io异常
     */
    public Response uploadFile(String apiUrl, List<MultipartBody.Part> parts, Map<String, String> header) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        // 添加所有parts
        for (MultipartBody.Part part : parts) {
            builder.addPart(part);
        }

        RequestBody requestBody = builder.build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(apiUrl)
                .post(requestBody);

        if (header != null && !header.isEmpty()) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.build();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 文件上传请求，返回body，如果请求失败会返回空字符串
     * <p>
     * 使用示例：
     * <pre>{@code
     * // 构建参数列表
     * List<MultipartBody.Part> parts = new ArrayList<>();
     * parts.add(MultipartBody.Part.createFormData("category", "123"));
     *
     * // 添加文件
     * File file = new File("/path/to/file.doc");
     * RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
     * parts.add(MultipartBody.Part.createFormData("file", "document.doc", fileBody));
     * parts.add(MultipartBody.Part.createFormData("name", "document.doc"));
     *
     * // 构建请求头
     * Map<String, String> headers = new HashMap<>();
     * headers.put("Authorization", "Bearer token");
     *
     * // 调用上传方法并获取响应体
     * String responseBody = apiCallManager.uploadFileResult("http://api.example.com/upload", parts, headers);
     * if (!responseBody.isEmpty()) {
     *     System.out.println("上传成功：" + responseBody);
     * } else {
     *     System.out.println("上传失败");
     * }
     * }</pre>
     *
     * @param apiUrl 接口地址
     * @param parts  多部分请求体参数列表
     * @param header 请求头
     * @return body
     */
    public String uploadFileResult(String apiUrl, List<MultipartBody.Part> parts, Map<String, String> header) {
        try (Response response = uploadFile(apiUrl, parts, header)) {
            if (!response.isSuccessful()) {
                log.error("文件上传失败：" + response);
                return "";
            }
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        } catch (IOException e) {
            log.error("文件上传发生异常", e);
            return "";
        }
    }

    public static MultipartBody.Part buildFilePart(File file, String fileName) {
        // 添加文件参数
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Headers fileHeaders = new Headers.Builder().addUnsafeNonAscii(
                "Content-Disposition", "form-data; name=\"file\"; filename=\"" + fileName + "\"").build();
        return MultipartBody.Part.create(fileHeaders, fileBody);
    }

    /**
     * post 请求，返回body，如果请求失败会返回空字符串。该方法已经弃用
     *
     * @param apiUrl 接口地址
     * @param body   请求体
     * @return body
     * @deprecated 请使用 postResult
     */
    @Deprecated
    public String postGetBody(String apiUrl, String body) {
        return postResult(apiUrl, body, null);
    }

    /**
     * post 请求，返回body，如果请求失败会返回空字符串。该方法已经弃用
     *
     * @param apiUrl 接口地址
     * @param body   请求体
     * @param header 请求头
     * @return body
     * @deprecated 请使用 postResult
     */
    @Deprecated
    public String postGetBody(String apiUrl, String body, Map<String, String> header) {
        return postResult(apiUrl, body, header);
    }

    /**
     * 将存储参数的 Map 转为 FROM 参数的字符串
     * @param params 存储参数的 Map
     * @throws IllegalArgumentException 如果转换为 FORM 参数字符串失败则抛出此异常
     * @return FROM 参数字符串
     */
    public static String toFormParam(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach(builder::add);
        FormBody formBody = builder.build();

        try {
            Buffer buffer = new Buffer();
            formBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            throw new IllegalArgumentException("不能将 Map 转换为 FORM 参数字符串，请检查 Map 参数");
        }
    }

}
