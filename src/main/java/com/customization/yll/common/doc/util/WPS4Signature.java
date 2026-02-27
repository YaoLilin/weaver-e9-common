package com.customization.yll.common.doc.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * WPS-3签名，需要引入依赖包
 * <pre>
 * org.apache.commons.commons-lang3
 * commons-codec.commons-codec
 * </pre>
 */
public class WPS4Signature {

    public final static String HTTP_HEADER_AUTHORIZATION = "Wps-Docs-Authorization";
    public final static String HTTP_HEADER_DATE = "Wps-Docs-Date";
    public final static String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    private String appId; // 应用id
    private String secretKey; // 应用秘钥

    public WPS4Signature(String appId, String secretKey) {
        this.appId = appId;
        this.secretKey = secretKey;
    }

    /**
     * 获取请求body MD5
     *
     * @param content 请求body
     * @return
     */
    public String getSHA256(String content) {
        if (StringUtils.isBlank(content)) {
            return HMacUtils.getSHA256StrJava("".getBytes());
        } else {
            return HMacUtils.getSHA256StrJava(content.getBytes());
        }
    }

    /**
     * 获取日期字符串
     *
     * @param date
     * @return
     */
    public static String getGMTDateString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date) + " GMT";
    }

    /**
     * 签名
     *
     * @param uriWithQuerystring
     * @param contentMD5         签名参数MD5
     * @param dateString
     * @return
     */
    public String getSignature(String method, String uriWithQuerystring, String contentMD5, String dateString, String contentType) throws Exception{
        return HMacUtils.HMACSHA256("WPS-4"+method+ uriWithQuerystring + contentType + dateString +contentMD5,this.secretKey);
    }

    /**
     * 获取X-Auth
     *
     * @param uriWithQuerystring 请求url，带querystring
     * @param contentMD5         请求body MD5
     * @param dateString         日期字符串，例如：Mon, 15 Nov 2021 02:34:04 GMT
     * @param contentType        application/json
     * @return
     */
    public String getAuthorization(String method ,String uriWithQuerystring, String contentMD5, String dateString, String contentType)throws Exception {
        String authorization = String.format(Locale.US, "WPS-4 %s:%s",
                this.appId,
                getSignature(method, uriWithQuerystring, contentMD5, dateString, contentType));
        return authorization;
    }

    /**
     * 获取签名请求头
     *
     * @param uriWithQuerystring 请求url，带querystring
     * @param content            请求body
     * @param date               日期，默认为 new Date()
     * @param contentType        默认为 application/json
     * @return
     */
    public Map<String, String> getSignatureHeaders(String method,String uriWithQuerystring, String content, Date date, String contentType) {
        if (uriWithQuerystring == null) {
            uriWithQuerystring = "";
        }
        if (content == null || StringUtils.isBlank(content)) {
            content = "";
        }else {
            content = getSHA256(content);
        }
        if (date == null) {
            date = new Date();
        }
        if (contentType == null) {
            contentType = "application/json";
        }

        String dateString = getGMTDateString(date);
        String authorization = "";
        try {
            authorization = getAuthorization(method, uriWithQuerystring, content, dateString, contentType);
        }catch (Exception e){

        }

        Map<String, String> headers = new HashMap<>();
        headers.put(WPS4Signature.HTTP_HEADER_AUTHORIZATION, authorization);
        headers.put(WPS4Signature.HTTP_HEADER_CONTENT_TYPE, contentType);
        headers.put(WPS4Signature.HTTP_HEADER_DATE, dateString);
        return headers;
    }

    public static void main(String[] args) {
        Date date = new Date();
        String contentType = "application/json";
        String uriWithQuerystring = "/auth/v1/app/inscope/token?app_id=AK20211119GMGKQV&scope=file_edit,file_preview,file_format_control,app_files_synerg_mgr";
        String body = "";

        // 获取签名请求头
        String appId = "AKKWXQJqTfBjMeAI";
        String secretKey = "TMqDUEcokBRXVzCpuPlyJKAxrHiLWneY";
        WPS4Signature signature = new WPS4Signature(appId, secretKey);
        Map<String, String> headers = signature.getSignatureHeaders("POST",uriWithQuerystring, body, date, contentType);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }


    }
}
