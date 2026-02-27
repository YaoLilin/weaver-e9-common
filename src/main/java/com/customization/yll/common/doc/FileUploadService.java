package com.customization.yll.common.doc;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.manager.EcologyTokenManager;
import com.customization.yll.common.web.util.ApiCallManager;
import okhttp3.MultipartBody;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * @author 姚礼林
 * @desc 调用标准接口上传附件到系统中
 * @date 2025/9/29
 **/
public class FileUploadService {
    private final IntegrationLog log = new IntegrationLog(this.getClass());

    private final String serverUrl;
    private final ApiCallManager apiCallManager;
    private final EcologyTokenManager tokenManager;

    public FileUploadService(String serverUrl, ApiCallManager apiCallManager, EcologyTokenManager tokenManager) {
        this.serverUrl = serverUrl;
        this.apiCallManager = apiCallManager;
        this.tokenManager = tokenManager;
    }

    /**
     * 上传文件到系统
     *
     * @param file 要上传的文件
     * @param fileName 文件名称
     * @param categoryId 目录ID
     * @param userId 用户ID
     * @return 文件ID，如果上传失败返回Optional.empty()
     */
    public Optional<Integer> uploadFile(File file, String fileName, int categoryId, int userId) {
        // 参数验证
        if (file == null || !file.exists()) {
            log.error("文件不存在或为空");
            return Optional.empty();
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            log.error("文件名称不能为空");
            return Optional.empty();
        }

        // 获取token请求头
        Map<String, String> tokenHeader = tokenManager.getHeaderWithToken(String.valueOf(userId));
        if (tokenHeader.isEmpty()) {
            log.error("获取token失败，无法上传文件");
            return Optional.empty();
        }

        // 构建上传接口URL
        String uploadUrl = serverUrl + "/api/doc/upload/uploadFile2Doc";

        try {
            List<MultipartBody.Part> parts = buildParamParts(file, fileName, categoryId);

            // 调用文件上传接口
            String responseBody = apiCallManager.uploadFileResult(uploadUrl, parts, tokenHeader);

            if (StrUtil.isBlank(responseBody)) {
                log.error("文件上传接口返回结果为空");
                return Optional.empty();
            }

            log.info("文件上传接口返回结果：" + responseBody);

            // 解析返回结果
            JSONObject responseJson = JSON.parseObject(responseBody);

            // 获取data部分
            JSONObject data = responseJson.getJSONObject("data");
            if (data == null) {
                log.error("文件上传失败，返回结果中缺少data字段：" + responseBody);
                return Optional.empty();
            }

            // 获取fileid
            Integer imageFileId = data.getInteger("imagefileid");
            if (imageFileId == null) {
                log.error("文件上传失败，返回结果中缺少imagefileid字段：" + responseBody);
                return Optional.empty();
            }

            log.info("文件上传成功，文件ID：" + imageFileId);
            return Optional.of(imageFileId);

        } catch (Exception e) {
            log.error("文件上传过程中发生异常", e);
            return Optional.empty();
        }
    }

    /**
     * 创建文档
     *
     * @param fileId 文件ID
     * @param categoryId 目录ID
     * @param userId 用户ID
     * @return 文档ID，如果创建失败返回Optional.empty()
     */
    public Optional<Integer> createDoc(int fileId, int categoryId, int userId) {
        // 参数验证
        if (fileId <= 0) {
            log.error("文件ID无效：{}", fileId);
            return Optional.empty();
        }

        if (categoryId <= 0) {
            log.error("目录ID无效：{}", categoryId);
            return Optional.empty();
        }

        // 获取token请求头
        Map<String, String> tokenHeader = tokenManager.getHeaderWithToken(String.valueOf(userId));
        if (tokenHeader.isEmpty()) {
            log.error("获取token失败，无法创建文档");
            return Optional.empty();
        }

        // 构建创建文档接口URL
        String createDocUrl = serverUrl + "/api/doc/save/accForDoc";

        try {
            // 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("fileid", String.valueOf(fileId));
            params.put("secid", String.valueOf(categoryId));

            // 调用创建文档接口
            String responseBody = apiCallManager.getResult(createDocUrl, params, tokenHeader);

            if (responseBody == null || responseBody.trim().isEmpty()) {
                log.error("创建文档接口返回结果为空");
                return Optional.empty();
            }

            log.info("创建文档接口返回结果：" + responseBody);

            // 解析返回结果
            JSONObject responseJson = JSON.parseObject(responseBody);

            // 检查接口调用是否成功
            if (responseJson.getInteger("status") == null || responseJson.getInteger("status") != 1) {
                log.error("创建文档失败，接口返回状态异常：" + responseBody);
                return Optional.empty();
            }

            // 获取docid
            Integer docId = responseJson.getInteger("docid");
            if (docId == null) {
                log.error("创建文档失败，返回结果中缺少docid字段：" + responseBody);
                return Optional.empty();
            }

            log.info("创建文档成功，文档ID：" + docId);
            return Optional.of(docId);

        } catch (Exception e) {
            log.error("创建文档过程中发生异常", e);
            return Optional.empty();
        }
    }

    /**
     * 上传文件并创建文档（组合方法）
     *
     * 该方法会先调用 uploadFile 方法上传文件获取文件ID，
     * 然后使用文件ID调用 createDoc 方法创建文档，
     * 最后返回文档ID。
     *
     * 使用示例：
     * <pre>{@code
     * // 上传文件并创建文档
     * File file = new File("/path/to/document.doc");
     * Optional<Integer> docId = uploadService.uploadFileAndCreateDoc(
     *     file,           // 要上传的文件
     *     "document.doc", // 文件名
     *     123,           // 目录ID
     *     456            // 用户ID
     * );
     *
     * if (docId.isPresent()) {
     *     System.out.println("文件上传并创建文档成功，文档ID：" + docId.get());
     * } else {
     *     System.out.println("文件上传或创建文档失败");
     * }
     * }</pre>
     *
     * @param file 要上传的文件
     * @param fileName 文件名称
     * @param categoryId 目录ID
     * @param userId 用户ID
     * @return 文档ID，如果上传或创建失败返回Optional.empty()
     */
    public Optional<Integer> uploadFileAndCreateDoc(File file, String fileName, int categoryId, int userId) {
        log.info("开始上传文件并创建文档流程，文件名：{}，目录ID：{}，用户ID：{}", fileName, categoryId, userId);

        try {
            // 第一步：上传文件
            log.info("步骤1：开始上传文件");
            Optional<Integer> fileIdOpt = uploadFile(file, fileName, categoryId, userId);

            if (!fileIdOpt.isPresent()) {
                log.error("文件上传失败，无法继续创建文档");
                return Optional.empty();
            }

            Integer fileId = fileIdOpt.get();
            log.info("文件上传成功，文件ID：{}", fileId);

            // 第二步：创建文档
            log.info("步骤2：开始创建文档，使用文件ID：{}", fileId);
            Optional<Integer> docIdOpt = createDoc(fileId, categoryId, userId);

            if (!docIdOpt.isPresent()) {
                log.error("文档创建失败，文件ID：{}", fileId);
                return Optional.empty();
            }

            Integer docId = docIdOpt.get();
            log.info("文件上传并创建文档成功，文件ID：{}，文档ID：{}", fileId, docId);

            return Optional.of(docId);

        } catch (Exception e) {
            log.error("上传文件并创建文档过程中发生异常", e);
            return Optional.empty();
        }
    }

    @NotNull
    private static List<MultipartBody.Part> buildParamParts(File file, String fileName, int categoryId) {
        // 构建MultipartBody.Part列表
        List<MultipartBody.Part> parts = new ArrayList<>();

        // 添加category参数
        parts.add(MultipartBody.Part.createFormData("category", String.valueOf(categoryId)));
        parts.add(ApiCallManager.buildFilePart(file,fileName));

        // 添加name参数
        parts.add(MultipartBody.Part.createFormData("name", fileName));

        return parts;
    }
}
