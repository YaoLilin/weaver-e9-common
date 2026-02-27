package com.customization.yll.common.doc;

import com.customization.yll.common.manager.EcologyTokenManager;
import com.customization.yll.common.web.util.ApiCallManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weaver.general.GCONST;

import java.io.File;
import java.util.Optional;

/**
 * @author 姚礼林
 * @desc 文件上传测试
 * @date 2025/9/30
 **/
class FileUploadServiceTest {
    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        GCONST.setRootPath("/Users/yaolilin/weaver/ecology/");
        GCONST.setServerName("ecology");
        fileUploadService = new FileUploadService("http://localhost", new ApiCallManager(),
                new EcologyTokenManager("552BD156-96EB-DE32-5D55-24F889A2FCAF", "http://localhost"));
    }

    @Test
    void uploadFile() {
        File file = new File("/Users/yaolilin/Desktop/测试.docx");
        Optional<Integer> fileIdOp = fileUploadService.uploadFile(file, file.getName(), 88, 1);
        Assertions.assertTrue(fileIdOp.isPresent());
        System.out.println("fileId:" + fileIdOp.get());
    }

    @Test
    void createDoc() {
        File file = new File("/Users/yaolilin/Desktop/测试.docx");
        Optional<Integer> fileIdOp = fileUploadService.uploadFile(file, file.getName(), 88, 1);
        Assertions.assertTrue(fileIdOp.isPresent());
        System.out.println("fileId:" + fileIdOp.get());
        Optional<Integer> docId = fileUploadService.createDoc(fileIdOp.get(), 88, 1);
        Assertions.assertTrue(docId.isPresent());
        System.out.println("docId:" + docId.get());
    }

    @Test
    void uploadFileAndCreateDoc() {
        File file = new File("/Users/yaolilin/Desktop/测试.docx");
        Optional<Integer> docId = fileUploadService.uploadFileAndCreateDoc(file, file.getName(),
                88, 1);
        Assertions.assertTrue(docId.isPresent());
        System.out.println("docId:" + docId.get());
    }
}
