package com.customization.yll.common.util;

import cn.hutool.core.io.IoUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author 姚礼林
 * @desc 接口生成文件下载响应工具类
 * @date 2025/8/29
 **/
@UtilityClass
@Slf4j
public class WebFileDownloadUtil {

    /**
     * 接口生成文件下载响应，让接口可下载文件
     * @param response 响应
     * @param file 需要下载的文件
     * @param fileName 下载文件的文件名
     * @throws IOException IO异常
     */
    public static void setDownloadResponse(HttpServletResponse response, File file, String fileName) throws IOException {
        setResponseHeader(response, fileName);
        IoUtil.copy(Files.newInputStream(file.toPath()), response.getOutputStream());
    }


    private static void setResponseHeader(HttpServletResponse response, String fileName) {
        String suffix = FileUtil.getSuffix(fileName);
        response.setHeader("Character-Encoding", "utf-8");
        response.setHeader("Content-Type", "application/" + suffix);

        // 正确处理中文文件名编码
        try {
            // 使用UTF-8编码文件名，并添加引号
            String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=\""
                    + fileName + "\"; filename*=UTF-8''" + encodedFileName);
        } catch (Exception e) {
            // 如果编码失败，使用原始文件名
            log.warn("文件名编码失败，使用原始文件名: {}", fileName);
            response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
        }
    }
}
