package com.customization.yll.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author 姚礼林
 * @desc Zip 压缩工具类测试
 * @date 2025/10/24
 **/
class ZipUtilTest {

    @Test
    void zip() throws IOException {
        String sourcePath = "/Users/yaolilin/Desktop/压缩测试";
        File zip = ZipUtil.zip(sourcePath, null, true);
        Assertions.assertTrue(Files.exists(zip.toPath()));
    }

    @Test
    void zip_withNotDir() throws IOException {
        String sourcePath = "/Users/yaolilin/Desktop/未命名.txt";
        File zip = ZipUtil.zip(sourcePath, null, false);
        Assertions.assertTrue(Files.exists(zip.toPath()));
    }

    @Test
    void zip_notWithRoot() throws IOException {
        String sourcePath = "/Users/yaolilin/Desktop/压缩测试";
        File zip = ZipUtil.zip(sourcePath, null, false);
        Assertions.assertTrue(Files.exists(zip.toPath()));
    }

    @Test
    void zip_withCustomZipFilePath() throws IOException {
        String sourcePath = "/Users/yaolilin/Desktop/压缩测试";
        String zipFilePath = "/Users/yaolilin/Desktop/压缩测试test.zip";
        File zip = ZipUtil.zip(sourcePath, zipFilePath, true);
        Assertions.assertTrue(Files.exists(zip.toPath()));
    }

    @Test
    void zip_withCharset() throws IOException {
        String sourcePath = "/Users/yaolilin/Desktop/压缩测试";
        File zip = ZipUtil.zip(sourcePath, StandardCharsets.UTF_8);
        Assertions.assertTrue(Files.exists(zip.toPath()));
    }

    @Test
    void unzip() throws IOException {
        String zipFilePath = "/Users/yaolilin/Downloads/test/压缩测试.zip";
        File unzip = ZipUtil.unzip(zipFilePath, "/Users/yaolilin/Downloads/test/压缩测试");
        Assertions.assertTrue(Files.exists(unzip.toPath()));
    }
}
