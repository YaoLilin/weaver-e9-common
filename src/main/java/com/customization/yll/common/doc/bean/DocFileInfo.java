package com.customization.yll.common.doc.bean;

import com.customization.yll.common.doc.constants.DocFileType;

import java.util.Objects;

/**
 * @author yaolilin
 * @desc 文档文件信息
 * @date 2024/9/5
 **/
public class DocFileInfo {
    private DocFileType fileType;
    private Integer docId;
    private Integer imageFileId;
    private String filePath;
    private boolean isFromZip;
    private String fileName;
    private Integer versionId;
    /**
     * 文件类型，1:文档中的图片 2:附件(包括附件中的图片) 3.world 文档 4.excel 文档 5.ppt文档 6.金山wps文档
     * 7、docx文档 8、xlsx文档 9、pptx文档 10、金山et文档 11、html文档中的视频
     */
    private String type;

    @Override
    public String toString() {
        return "DocFileInfo{" +
                "fileType=" + fileType +
                ", docId=" + docId +
                ", imageFileId=" + imageFileId +
                ", filePath='" + filePath + '\'' +
                ", isFromZip=" + isFromZip +
                ", fileName='" + fileName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public DocFileType getFileType() {
        return fileType;
    }

    public void setFileType(DocFileType fileType) {
        this.fileType = fileType;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getImageFileId() {
        return imageFileId;
    }

    public void setImageFileId(Integer imageFileId) {
        this.imageFileId = imageFileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFromZip() {
        return isFromZip;
    }

    public void setFromZip(boolean fromZip) {
        isFromZip = fromZip;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocFileInfo that = (DocFileInfo) o;
        return Objects.equals(imageFileId, that.imageFileId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(imageFileId);
    }

}
