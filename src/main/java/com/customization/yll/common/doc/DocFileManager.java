package com.customization.yll.common.doc;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ZipUtil;
import com.customization.yll.common.IntegrationLog;
import com.customization.yll.common.doc.bean.DocFileInfo;
import com.customization.yll.common.doc.constants.DocFileType;
import com.customization.yll.common.util.DocUtil;
import com.customization.yll.common.util.FileUtil;
import weaver.conn.RecordSet;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 获取文档中的所有文件，包括正文和附件
 * @author yaolilin
 * @date 2024-09-05
 */
public class DocFileManager {
    private final RecordSet recordSet = new RecordSet();
    private final IntegrationLog log = new IntegrationLog(DocFileManager.class);

    /**
     * 获取文档中的所有文件，包括文档中的附件,如果附件有多个版本，则只会获取最新一个版本
     *
     * @param docId 文档id
     * @param saveDirPath 文件保存目录路径
     * @param unzip 是否解压压缩包获取里面的文件
     * @return 文档中的所有文件
     */
    public List<DocFileInfo> getDocFiles(int docId, String saveDirPath, boolean unzip) throws IOException {
        log.info("获取文档文件，文档id："+docId);
        List<DocFileInfo> fileInfoList = new ArrayList<>();
        List<DocFileInfo> docFileInfos = DocUtil.getDocFileInfos(docId,recordSet);
        // 根据 imagefileid 去重，因为可能有垃圾数据导致重复
        Set<DocFileInfo> docFileInfosSet = new HashSet<>(docFileInfos);
        for (DocFileInfo fileInfo : docFileInfosSet) {
            String imageFilePath = saveDirPath + getSeparator() + fileInfo.getFileName();
            if (Files.exists(Paths.get(imageFilePath))) {
                imageFilePath = saveDirPath + getSeparator() + fileInfo.getImageFileId() +
                        "-" + fileInfo.getVersionId() + "-" + fileInfo.getFileName();
                log.info("文件已存在，文件名:{},新的文件路径为:{}", fileInfo.getFileName(), imageFilePath);
            }
            DocUtil.getImageFile(fileInfo.getImageFileId(), imageFilePath);
            String suffix = FileUtil.getSuffix(fileInfo.getFileName());
            if (unzip && "zip".equalsIgnoreCase(suffix)) {
                fileInfoList.addAll(getZipFiles(imageFilePath, docId, fileInfo.getImageFileId()));
                log.info("解压完成");
            } else {
                fileInfo.setType(recordSet.getString("docfiletype"));
                fileInfo.setFilePath(imageFilePath);
                fileInfoList.add(fileInfo);
            }
        }

        return fileInfoList;
    }

    /**
     * 获取文档中的所有文件，包括文档中的附件
     * @param docId 文档id
     * @param saveDirPath 文件保存目录路径
     * @return 文档中的所有文件
     * @throws IOException 获取文件发生异常
     */
    public List<DocFileInfo> getDocFiles(int docId,String saveDirPath) throws IOException {
        return getDocFiles(docId,saveDirPath,false);
    }

    private List<DocFileInfo> getZipFiles(String sourceFile,int docId, int imageFileId) {
        log.info("需要进行解压");
        // 解压到当前目录
        File unDir = new File(sourceFile.substring(0,sourceFile.lastIndexOf(".zip")));
        ZipUtil.unzip(new File(sourceFile), unDir, CharsetUtil.CHARSET_UTF_8);
        return takeFilesInDir(unDir.getAbsolutePath(),docId, imageFileId);
    }

    private DocFileInfo buildDocFileInfo(String fileName, DocFileType fileType, Integer docId, Integer imageFileId,
                                         String filePath,
                                         boolean isFromZip) {
        DocFileInfo fileInfo = new DocFileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setFileType(fileType);
        fileInfo.setDocId(docId);
        fileInfo.setImageFileId(imageFileId);
        fileInfo.setFilePath(filePath);
        fileInfo.setFromZip(isFromZip);
        return fileInfo;
    }

    private List<DocFileInfo> takeFilesInDir(String dirPath, int docId,int imageFileId) {
        File dir = new File(dirPath);
        List<DocFileInfo> files = new ArrayList<>();
        File[] fileList = dir.listFiles(pathname -> !pathname.isHidden());
        if (fileList != null) {
            for (File f : fileList) {
                if (f.isDirectory()) {
                    files.addAll(takeFilesInDir(f.getAbsolutePath(), docId,imageFileId));
                } else if ("zip".equalsIgnoreCase(FileUtil.getSuffix(f.getName()))) {
                    files.addAll(getZipFiles(f.getAbsolutePath(),docId, imageFileId));
                } else {
                    files.add(buildDocFileInfo(f.getName(), DocFileType.ATTACHMENT, docId, imageFileId,
                            f.getAbsolutePath(), true));
                }
            }
        }
        return files;
    }

    private String getSeparator() {
        return FileSystems.getDefault().getSeparator();
    }
}
