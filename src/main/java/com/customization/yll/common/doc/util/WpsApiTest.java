package com.customization.yll.common.doc.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WpsApiTest {

    public static String WPS_DOCS_DATE = "Wps-Docs-Date";
    public static String WPS_DOCS_AUTHORIZATION = "Wps-Docs-Authorization";
    public static String CONTENT_TYPE = "Content-Type";
    public static String WPS_SIGNATURE_VERSION = "WPS-4";
    public final static String[] WORD_EXT = {"doc", "dot", "wps", "wpt", "docx", "dotx", "docm", "dotm"};
    public final static String[] EXCEL_EXT = {"xls", "xlt", "et", "xlsx", "xltx", "xlsm", "xltm"};
    public final static String[] PPT_EXT = {"ppt", "pptx", "pptm", "ppsx", "ppsm", "pps", "potx", "potm", "dpt", "dps"};
    public final static String[] PDF_EXT = {"pdf","PDF"};

    private static String ACCESS_KEY;
    private static String SECRET_KEY;

    public static void initAppInfo(String accessKey, String secretKey) {
        ACCESS_KEY = accessKey;
        SECRET_KEY = secretKey;
    }


    /**
     * 根据文件名，获取文件类型
     *
     * @param filename 文件名
     * @return 文件类型 w p s f
     */
    public static String getFileType(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return null;
        }
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return null;
        }
        String ext = filename.substring(index + 1);
        for (String element : WORD_EXT) {
            if (StringUtils.equalsIgnoreCase(ext, element)) {
                return "w";
            }
        }
        for (String item : EXCEL_EXT) {
            if (StringUtils.equalsIgnoreCase(ext, item)) {
                return "s";
            }
        }
        for (String value : PPT_EXT) {
            if (StringUtils.equalsIgnoreCase(ext, value)) {
                return "p";
            }
        }
        for (String s : PDF_EXT) {
            if (StringUtils.equalsIgnoreCase(ext, s)) {
                return "f";
            }
        }
        return null;
    }


    /**
     * 获取请求body MD5
     *
     * @param content 请求body
     * @return
     */
    public static String getContentSha256(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        } else {
            return DigestUtils.sha256Hex(content);
        }
    }

    /**
     * 获取签名请求头
     *
     * @param uriWithQuerystring 请求url，带querystring
     * @param content            请求body
     * @return
     */
    public static HashMap<String, String> getSignatureHeaders(String uriWithQuerystring, String httpMethod, String content) throws Exception {
        if (uriWithQuerystring == null) {
            uriWithQuerystring = "";
        }
        String contentSha256 = getContentSha256(content);
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        String wpsDocsDate = format.format(new Date());
        String authorization = getWpsAuthorization(WPS_SIGNATURE_VERSION, httpMethod,
                uriWithQuerystring, contentSha256, wpsDocsDate, "application/json");
        HashMap<String, String> headers = new HashMap<>();
        headers.put(WPS_DOCS_AUTHORIZATION, authorization);
        headers.put(CONTENT_TYPE, "application/json");
        headers.put(WPS_DOCS_DATE, wpsDocsDate);
        return headers;
    }

    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secret_key);
        byte[] array = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) |
                    0x100).substring(1, 3));
        }
        return sb.toString();
    }

    private static String getWpsAuthorization(String ver, String httpMethod, String uriWithQuerystring, String contentSha256, String dateString, String contentType) throws Exception {
        String signatureStr = String.format("%s%s%s%s%s%s", ver, httpMethod,
                uriWithQuerystring, contentType, dateString, contentSha256);
        return String.format("WPS-4 %s:%s", ACCESS_KEY, HMACSHA256(signatureStr, SECRET_KEY));
    }


    public static String  doRequest(String url, String method, HashMap<String, String> headers, String body) {
        HttpURLConnection connection = null;
        try {
            // 创建远程url连接对象
            URL reqUrl = new URL(url);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) reqUrl.openConnection();
            // 设置连接方式
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            if (body != null) {
                connection.setDoOutput(true);
                connection.setUseCaches(false);
            }
            if (headers != null) {
                for (String key : headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            if (body != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(body);
                writer.close();
            }
            // 通过connection连接，获取输入流
            InputStream resultInputStream;
            if (connection.getResponseCode() == 200) {
                resultInputStream = connection.getInputStream();
                System.out.println("请求成功");
            } else {
                resultInputStream = connection.getErrorStream();
                System.out.println(("请求失败, code: " + connection.getResponseCode() + " message: " + connection.getResponseMessage()));
            }
            String buf;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resultInputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            resultInputStream.close();
            buf = sb.toString();
            System.out.println("请求结果:" + buf);
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();// 关闭远程连接
            }
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        String accessKey = "OTNZQTXWBYGPIQTR"; // 应用id
        String secretKey = "SKdwolamyvmnhthr"; // 应用秘钥
        String wdztApiHost = "https://wlwps.wuling.com.cn:8018/open";
        initAppInfo(accessKey, secretKey);
        String body = "{\"doc_filename\":\"评估结果.docx\",\"doc_url\":\"http://127.0.0.1/api/open/second-dev/doc/download?fileId=12266&token=d148519a3fc6647271513598211c0c12\",\"target_file_format\":\"pdf\",\"task_id\":\"12266-1750303315645\"}";
        String apiUrl = "/api/cps/sync/v1/convert";
        String method = "POST";
        HashMap<String, String> headers = getSignatureHeaders(apiUrl, method, body);
        System.out.println(headers);
        doRequest(wdztApiHost + apiUrl, method, headers, body);
    }


}
