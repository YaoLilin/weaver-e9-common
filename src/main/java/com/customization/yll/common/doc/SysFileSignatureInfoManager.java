package com.customization.yll.common.doc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.customization.yll.common.doc.bean.SignatureResult;
import com.customization.yll.common.web.util.QysSignatureUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.File;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 获取契约锁文件签名信息
 * @date 2025/8/3
 **/
public class SysFileSignatureInfoManager {
    private static final Logger log = LoggerFactory.getLogger(SysFileSignatureInfoManager.class);
    private final String serverHost;
    private final String appToken;
    private final String appSecret;
    private final OkHttpClient httpClient;

    public SysFileSignatureInfoManager(String serverHost, String appToken, String appSecret) {
        this.serverHost = serverHost;
        this.appToken = appToken;
        this.appSecret = appSecret;
        this.httpClient = new OkHttpClient();
    }

    /**
     * 获取文件签名信息
     * @param filePath 文件路径
     * @return 签名信息
     */
    public Optional<SignatureResult> getFileSignatureInfo(String filePath) {
        try {
            if (!verifyFile(filePath)) {
                return Optional.empty();
            }
            log.info("获取文件签名：" + filePath);

            // 根据文件扩展名动态设置文件名和 MIME 类型
            String fileExtension = getFileExtension(filePath);
            String safeFileName = "document." + fileExtension;
            String mimeType = getMimeType(fileExtension);

            RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", safeFileName,
                    RequestBody.create(MediaType.parse(mimeType), new File(filePath)))
                .build();

            // 构建请求头
            Request request = getRequest(requestBody);

            //  发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("契约锁文件验签接口调用失败，状态码: " + response.code());
                    return Optional.empty();
                }
                if (response.body() == null) {
                    log.error("response.body == null");
                    return Optional.empty();
                }
                String responseBody = response.body().string();

                // 6. 解析响应
                JSONObject responseJson = JSON.parseObject(responseBody);

                if (responseJson.getInteger("code") != 0) {
                    log.error("契约锁文件验签失败: " + responseJson.getString("message"));
                    return Optional.empty();
                }
                log.info("接口返回成功");
                // 7. 构建签名信息对象
                SignatureResult signatureResult = JSON.toJavaObject(responseJson, SignatureResult.class);
                log.info("签名信息：" + JSON.toJSONString(signatureResult));
                return Optional.of(signatureResult);

            }

        } catch (Exception e) {
            log.error("获取文件签名信息异常", e);
            return Optional.empty();
        }
    }

    @NotNull
    private Request getRequest(RequestBody requestBody) {
        long timestamp = System.currentTimeMillis();
        String signature = QysSignatureUtil.generateSignature(timestamp, appToken, appSecret);

        return new Request.Builder()
            .url(serverHost + "/file/verify")
            .addHeader("Accept", "text/plain,application/json")
            .addHeader("User-Agent", "privateapp-java-api-client")
            .addHeader("x-qys-accesstoken", appToken)
            .addHeader("x-qys-timestamp", String.valueOf(timestamp))
            .addHeader("x-qys-signature", signature)
            .post(requestBody)
            .build();
    }

    private boolean verifyFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("文件不存在: " + filePath);
            return false;
        }

        // 检查文件格式（支持PDF和OFD格式）
        String fileExtension = getFileExtension(filePath);
        if (!isSupportedFormat(fileExtension)) {
            log.error("文件格式不支持，仅支持PDF和OFD格式: " + filePath);
            return false;
        }
        return true;
    }

    /**
     * 获取文件扩展名
     * @param filePath 文件路径
     * @return 文件扩展名（小写，不包含点号）
     */
    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * 检查是否为支持的文件格式
     * @param fileExtension 文件扩展名
     * @return 是否支持
     */
    private boolean isSupportedFormat(String fileExtension) {
        return "pdf".equals(fileExtension) || "ofd".equals(fileExtension);
    }

    /**
     * 根据文件扩展名获取 MIME 类型
     * @param fileExtension 文件扩展名
     * @return MIME 类型
     */
    private String getMimeType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "ofd":
                return "application/ofd";
            default:
                return "application/octet-stream";
        }
    }

}
