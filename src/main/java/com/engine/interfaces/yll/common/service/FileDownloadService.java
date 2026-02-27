package com.engine.interfaces.yll.common.service;

import com.customization.yll.common.util.DocUtil;
import com.engine.core.impl.Service;
import com.engine.interfaces.yll.common.domain.dto.FileDownloadDto;
import weaver.conn.RecordSet;

/**
 * @author 姚礼林
 * @desc 文件下载业务类
 * @date 2025/6/17
 **/
public class FileDownloadService extends Service {

    public FileDownloadDto getFile(int fileId){
        String fileName = DocUtil.getFileNameByFileId(fileId, new RecordSet());
        FileDownloadDto fileDownloadDto = new FileDownloadDto();
        fileDownloadDto.setFileName(fileName);
        fileDownloadDto.setInputStream(DocUtil.getFileInputStream(fileId));
        return fileDownloadDto;
    }
}
