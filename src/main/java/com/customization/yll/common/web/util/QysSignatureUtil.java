package com.customization.yll.common.web.util;

import com.customization.yll.common.IntegrationLog;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 获取契约锁api签名
 * @date 2025/8/11
 **/
@UtilityClass
public class QysSignatureUtil {
    private static final IntegrationLog log = new IntegrationLog(QysSignatureUtil.class);

    public static Map<String, String> getSignatureHead(String appToken, String appSecret) {
        long timestamp = System.currentTimeMillis();
        String signature = generateSignature(timestamp, appToken, appSecret);

        Map<String, String> head = new HashMap<>(3);
        head.put("x-qys-accesstoken", appToken);
        head.put("x-qys-timestamp", String.valueOf(timestamp));
        head.put("x-qys-signature", signature);
        return head;
    }

    /**
     * 生成签名
     *
     * @param timestamp 时间戳
     * @return 签名
     */
    public static String generateSignature(long timestamp, String appToken, String appSecret) {
        String content = appToken + appSecret + timestamp;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("生成签名失败", e);
            return "";
        }
    }
}
