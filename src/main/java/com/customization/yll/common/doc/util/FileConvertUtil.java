package com.customization.yll.common.doc.util;

import com.customization.yll.common.doc.DocConvertorByWpsApi;
import com.customization.yll.common.util.DocUtil;
import com.customization.yll.common.util.FileUtil;
import com.customization.yll.common.util.LogUtil;
import lombok.experimental.UtilityClass;
import weaver.integration.logging.Logger;

import java.io.IOException;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 文件格式转换
 * @date 2025/7/28
 **/
@UtilityClass
public class FileConvertUtil{
    private static final Logger log = LogUtil.getIntegrationLog(FileConvertUtil.class);

    /**
     * 使用wps集成将文件转为pdf，只有集成了wps在线编辑才可以使用
     *
     * @param newFileName 转换后的文件名称，不带后缀
     * @param imageFileId 需要进行转换的文件id
     * @return 转换后生成的pdf文件id，如果转换失败则为 empty
     */
    public static Optional<Integer> convertToPdfByWpsIntegration(String newFileName, int imageFileId) {
        return DocUtil.convertPdfByOfficedService(newFileName, imageFileId);
    }

    public static boolean convertToPdfByWpsIntegrationSave(String saveFilePath, int imageFileId) {
        String fileName = saveFilePath.substring(saveFilePath.lastIndexOf(FileUtil.getSeparator()) + 1);
        Optional<Integer> newFileId = convertToPdfByWpsIntegration(fileName, imageFileId);
        if (!newFileId.isPresent()) {
            log.error("使用wps文档转pdf失败，imageFileId：" + imageFileId + "，文件保存路径：" + saveFilePath);
            return false;
        }

        int fileId = newFileId.get();
        log.info("转换后的文件id：" + fileId);
        if (fileId < 1) {
            log.error("转换失败，转换后文件id：" + fileId);
            return false;
        }
        log.info("转换成功，新文件id：" + fileId);
        try {
            DocUtil.getImageFile(fileId, saveFilePath);
        } catch (IOException e) {
            log.error("获取转换后的文件失败", e);
            return false;
        }
        return true;
    }

    /**
     * 获取文件格式转换器，使用WPS Api（文档中台）进行文档转换
     * @param oaHost oa系统地址
     * @param host wps文档中台服务器地址
     * @param secretKey wps文档中台secretKey
     * @param accessKey wps文档中台accessKey
     * @return 文件格式转换器
     */
    public static DocConvertorByWpsApi getConvertorWithWpsApi(String oaHost, String host,
        String secretKey, String accessKey) {
        return new DocConvertorByWpsApi(oaHost, host, secretKey, accessKey);
    }

}
