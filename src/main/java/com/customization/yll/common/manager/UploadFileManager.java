package com.customization.yll.common.manager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author yaolilin
 * @desc 将文件上传到系统中
 * @date 2024/9/3
 **/
public class UploadFileManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final EcologyTokenManager tokenManager;
    private final String serverAddress;
    private String uploadUserId = "1";

    /**
     * @param serverAddress 系统访问地址
     * @param tokenManager 用于获取token，进行接口认证
     */
    public UploadFileManager(String serverAddress, EcologyTokenManager tokenManager ) {
        this.tokenManager = tokenManager;
        this.serverAddress = serverAddress;
    }

    /**
     * @param serverAddress 系统访问地址,必需带上 http:// 或 https://
     * @param uploadUserId 上传文件的用户id
     * @param tokenManager 用于获取token，进行接口认证
     */
    public UploadFileManager(String serverAddress,String uploadUserId, EcologyTokenManager tokenManager ) {
        this.tokenManager = tokenManager;
        this.uploadUserId = uploadUserId;
        this.serverAddress = serverAddress;
    }

    /**
     * 调用系统标准接口上传文件 <br>
     * 返回示例：
     * <pre>
     * {@code
     * {
     *      "data": {
     *           "fileExtendName": "png",
     *           "isImg": "",
     *           "filelink": "/spa/document/index2file.jsp?imagefileId=3915#/main/document/fileView",
     *           "uploaddate": "2024-09-03 20:13:49",
     *           "showLoad": "true",
     *           "acclink": "/weaver/weaver.file.FileDownload?fileid=a37fa1127b03d2b396541aea6d89c477cb27178ab5061879894ff4aa8476dea595e955128686e6e36d3a0868eea8ed35713d3b1f4555f2a29",
     *           "filesize": "169KB",
     *           "loadlink": "/weaver/weaver.file.FileDownload?fileid=a37fa1127b03d2b396541aea6d89c477cb27178ab5061879894ff4aa8476dea595e955128686e6e36d3a0868eea8ed35713d3b1f4555f2a29&download=1",
     *           "secretLevel": "4",
     *           "filename": "??2024-09-02 11.05.19.png",
     *           "fileidCode": "a37fa1127b03d2b396541aea6d89c477cb27178ab5061879894ff4aa8476dea595e955128686e6e36d3a0868eea8ed35713d3b1f4555f2a29",
     *           "secretLevelValidity": "",
     *           "showDelete": "false",
     *           "imgSrc": "/weaver/weaver.file.FileDownload?fileid=a37fa1127b03d2b396541aea6d89c477cb27178ab5061879894ff4aa8476dea595e955128686e6e36d3a0868eea8ed35713d3b1f4555f2a29",
     *           "secretLevelValidityValue": "",
     *           "fileid": 3915,
     *           "username": "展璐芸"
     *      },
     *      "status": 1
     * }
     * }
     * </pre>
     *
     * @param filePath 文件绝对路径
     * @return 接口返回结果
     */
    public String uploadFile(String filePath){
        Map<String, String> header = tokenManager.getHeaderWithToken(uploadUserId);
        if (header.isEmpty()) {
            log.error("获取token失败");
            return "";
        }
        String urlString = serverAddress+"/api/doc/upload/uploadFile";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 构建multipart实体
            HttpEntity multipart = buildEntity(filePath);
            HttpPost httpPost = new HttpPost(urlString);
            httpPost.setEntity(multipart);
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.setHeader(entry.getKey(),entry.getValue());
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            if (StringUtils.isEmpty(result)) {
                log.error("请求上传文件接口失败，返回结果为空");
                return "";
            }
            return result;
        } catch (IOException e) {
            log.error("请求上传文件接口失败", e);
        }
        return "";
    }

    private  HttpEntity buildEntity(String  filePath) {
        File file = new File(filePath);
        return MultipartEntityBuilder.create()
                .addPart("file", new FileBody(file))
                .addPart("name", new StringBody(file.getName(), org.apache.http.entity.ContentType.TEXT_PLAIN))
                .addPart("ts", new StringBody(System.currentTimeMillis() + "", org.apache.http.entity.ContentType.TEXT_PLAIN))
                .build();
    }


}
