package com.customization.yll.common.doc;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.customization.yll.common.doc.util.HMacUtils;
import com.customization.yll.common.exception.DocConvertException;
import com.customization.yll.common.util.DocUtil;
import com.customization.yll.common.util.Md5Util;
import com.customization.yll.common.web.util.ApiCallManager;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 使用WPS Api（文档中台）进行文档转换
 *       使用此转换必需将 /api/open/second-dev/doc/download 添加到接口白名单
 * @date 2025/6/16
 **/
public class DocConvertorByWpsApi {
    private static final String DOWNLOAD_SECRET = "dd8a36f2-f0bb-4021-b829-083b1fd8489d";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ApiCallManager apiCallManager = new ApiCallManager(120);
    private final String oaHost;
    private final String host;
    private final String secretKey;
    private final String accessKey;

    /**
     * @param oaHost    oa 系统地址
     * @param host      wps 文档中台服务器地址
     * @param secretKey wps 文档中台 secretKey
     * @param accessKey wps 文档中台 accessKey
     */
    public DocConvertorByWpsApi(String oaHost, String host, String secretKey, String accessKey) {
        this.oaHost = oaHost;
        this.host = host;
        this.secretKey = secretKey;
        this.accessKey = accessKey;
    }

    /**
     * 调用 wps 接口进行文档转换
     *
     * @param fileId         系统上的文件id，为 imagefile 表的 imagefileid
     * @param savePath       转换后文件保存路径
     * @param convertFormat  转换格式
     * @param otherApiParams wps接口额外参数
     * @return 转换后的文件
     * @throws DocConvertException 转换异常
     */
    public File convert(int fileId, String savePath, String convertFormat, Map<String, Object> otherApiParams)
            throws DocConvertException {
        JSONObject resultJson = callConvertApiSync(fileId, convertFormat, otherApiParams);
        JSONObject data = resultJson.getJSONObject("data");
        String downloadId = data.getString("download_id");
        String routeKey = data.getString("route_key");

        Response response = downloadConvertedFile(downloadId, routeKey);
        return saveFile(response, savePath);
    }

    @NotNull
    private JSONObject callConvertApiSync(int fileId, String convertFormat, Map<String, Object> otherApiParams)
            throws DocConvertException {
        String uri = "/open/api/cps/sync/v1/convert";
        String apiUrl = host + uri;
        JSONObject body = getBody(fileId, convertFormat, otherApiParams);
        Map<String, String> header;
        try {
            header = getSignatureHeader(uri, "POST", "application/json;charset=utf-8",
                    body.toJSONString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new DocConvertException("生成签名失败", e);
        }
        log.info("请求体：" + body.toJSONString());
        String result = apiCallManager.postResult(apiUrl, body.toJSONString(), header);
        log.info("接口返回数据：" + result);
        if (StrUtil.isEmpty(result)) {
            throw new DocConvertException("文件转换接口请求错误，接口返回为空");
        }
        JSONObject resultJson = JSON.parseObject(result);
        if (resultJson.getIntValue("code") != 200) {
            throw new DocConvertException("文件转换接口请求错误，返回结果状态为失败，信息：" + resultJson.getString("msg"));
        }
        return resultJson;
    }

    private Response downloadConvertedFile(String downloadId, String routeKey) throws DocConvertException {
        log.info("下载文件，downloadId：" + downloadId);
        String uri = "/api/cps/v1/download/" + downloadId;
        String apiUrl = host + "/open" + uri;
        Map<String, String> header;
        try {
            header = getSignatureHeader(uri, "GET",
                    "application/json;charset=utf-8", null);
        } catch (Exception e) {
            throw new DocConvertException("生成签名失败", e);
        }
        header.put("Route-Key", routeKey);
        try {
            return apiCallManager.get(apiUrl,null ,header);
        } catch (IOException e) {
            throw new DocConvertException("转换文件下载接口请求失败", e);
        }
    }

    @NotNull
    private JSONObject getBody(int fileId, String convertFormat, Map<String, Object> otherParams) {
        String token = Md5Util.createMd5(fileId + DOWNLOAD_SECRET);
        String downloadUrl = oaHost + "/api/open/second-dev/doc/download?fileId=" + fileId + "&token=" + token;
        String fileName = DocUtil.getFileNameByFileId(fileId, new RecordSet());
        JSONObject body = new JSONObject();
        body.put("task_id", fileId + "-" + System.currentTimeMillis());
        body.put("doc_url", downloadUrl);
        body.put("doc_filename", fileName);
        body.put("target_file_format", convertFormat);
        if (otherParams != null) {
            body.putAll(otherParams);
        }
        return body;
    }

    /**
     * 生成WPS-4签名
     *
     * @param uri         请求接口地址，不包含域名部分
     * @param httpMethod  请求方法，如GET，POST
     * @param contentType 请求类型，如application/json
     * @param httpBody    请求体
     * @return WPS-4签名
     * @throws Exception 生成签名异常
     */
    private Map<String, String> getSignatureHeader(String uri, String httpMethod,
                                                   String contentType, byte[] httpBody)
            throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        ZonedDateTime gmtTime = ZonedDateTime.now(ZoneId.of("GMT"));
        String date = formatter.format(gmtTime);

        //open不参与签名，做替换处理
        if (uri.startsWith("/open")) {
            uri = uri.replace("/open", "");
        }

        String sha256body;
        //body为空则为空，否则返回sha256(body)
        if (httpBody != null && httpBody.length > 0) {
            sha256body = HMacUtils.getSHA256StrJava(httpBody);
        } else {
            sha256body = "";
        }

        //hmac-sha256(secret_key, Ver + HttpMethod + URI + Content-Type + Wps-Date + sha256(HttpBody))
        String data = "WPS-4" + httpMethod + uri + contentType + date + sha256body;
        log.info("data:" + data);
        String signature = HMacUtils.HMACSHA256(data, secretKey);
        Map<String, String> header = new HashMap<>(3);
        header.put("Content-Type", contentType);
        header.put("Wps-Docs-Date", date);
        header.put("Wps-Docs-Authorization", String.format("WPS-4 %s:%s", accessKey, signature));
        return header;
    }

    private File saveFile(Response response, String savePath) throws DocConvertException {
        log.info("保存文件，保存路径：" + savePath);
        if (!response.isSuccessful()) {
            throw new DocConvertException("文件下载失败：" + response.message()+",http状态："+ response.code() );
        }
        try {
            Path path = Paths.get(savePath);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (response.body() == null) {
                throw new DocConvertException("文件保存失败，文件下载接口无响应体");
            }
            try (InputStream inputStream = response.body().byteStream()) {
                Files.copy(inputStream, path);
            }
            return new File(savePath);
        } catch (IOException e) {
            throw new DocConvertException("文件保存失败", e);
        }
    }

}
