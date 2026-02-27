package com.engine.interfaces.yll.common.domain.dto;

import java.io.InputStream;

/**
 * @author 姚礼林
 * @desc 文件下载信息
 * @date 2025/6/17
 **/
public class FileDownloadDto {
    private InputStream inputStream;
    private String fileName;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
