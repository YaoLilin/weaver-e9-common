package com.customization.yll.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.api.doc.detail.service.DocAccService;
import com.api.doc.detail.service.DocSaveService;
import com.api.doc.detail.util.DocDownloadCheckUtil;
import com.customization.yll.common.doc.bean.DocFileInfo;
import com.customization.yll.common.doc.constants.DocFileType;
import com.customization.yll.common.exception.SqlExecuteException;
import com.weaver.formmodel.mobile.utils.AttachUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import weaver.conn.RecordSet;
import weaver.file.ImageFileManager;
import weaver.hrm.User;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.wps.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author yaolilin
 * @desc 处理文档和文件的工具类
 */
@UtilityClass
public class DocUtil {
    public static final int MAIN_BODY_FILE = 1;
    public static final int ATTACHMENT_FILE = 2;
    public static final int ALL_FILE = 2;
    private static final Logger log = LoggerFactory.getLogger(DocUtil.class);

    /**
     * 获取流程存为文档的实体文件，只会获取到最后流程生成的文件
     *
     * @param requestId 流程请求id
     * @param savePath  文件保存目录的绝对路径
     * @param afterTime 获取指定时间之后的文档，比如获取流程归档时间之后的文档，格式为 yyyy-MM-dd HH:mm:ss
     * @param recordSet recordSet
     * @return 流程存为文档的文件路径集合，如果流程存为文档勾选多个生成文件类型，则返回多个文件路径
     * @throws IOException 获取文件时发生异常
     */
    public static List<DocFileInfo> getWorkflowCreateDoc(int requestId, String savePath, String afterTime,
                                                         RecordSet recordSet) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<DocFileInfo> result = new ArrayList<>();
        recordSet.executeQuery("SELECT i.FILEREALPATH,i.IMAGEFILENAME,i.IMAGEFILEID,i.iszip,f.docid,d.doccreatedate,d.doccreatetime  " +
                "from docdetail d,docimagefile f,imagefile i  where FROMWORKFLOW = ? and d.id = f.docid " +
                "and i.imagefileid = f.IMAGEFILEID order by i.IMAGEFILEID desc", requestId);
        int docId = 0;
        while (recordSet.next()) {
            if (!StringUtils.isEmpty(afterTime)) {
                String docCreateTime = recordSet.getString("doccreatedate") + " " + recordSet.getString("doccreatetime");
                LocalDateTime docCreateTimeObj = LocalDateTime.parse(docCreateTime, formatter);
                if (docCreateTimeObj.isBefore(LocalDateTime.parse(afterTime, formatter))) {
                    log.info("没有获取到指定时间之后的文档");
                    break;
                }
            }
            if (docId == 0) {
                docId = recordSet.getInt("docid");
            }
            if (docId != recordSet.getInt("docid")) {
                break;
            }
            int imageFileId = recordSet.getInt("IMAGEFILEID");
            String imageFileName = recordSet.getString("IMAGEFILENAME");
            String saveFile = savePath + getSeparator() + imageFileName;
            getImageFile(imageFileId, saveFile);
            DocFileInfo docFileInfo = new DocFileInfo();
            docFileInfo.setDocId(recordSet.getInt("docid"));
            docFileInfo.setFileName(recordSet.getString("IMAGEFILENAME"));
            docFileInfo.setFileType(DocFileType.ATTACHMENT);
            docFileInfo.setImageFileId(imageFileId);
            docFileInfo.setFilePath(saveFile);
            docFileInfo.setFromZip(false);
            result.add(docFileInfo);
        }
        return result;
    }

    /**
     * 获取流程存为文档的实体文件，只会获取到最后流程生成的文件
     *
     * @param requestId 流程请求id
     * @param savePath  文件保存目录的绝对路径
     * @param recordSet recordSet
     * @return 流程存为文档的文件路径集合，如果流程存为文档勾选多个生成文件类型，则返回多个文件路径
     * @throws IOException 获取文件时发生异常
     */
    public static List<DocFileInfo> getWorkflowCreateDoc(int requestId, String savePath, RecordSet recordSet) throws IOException {
        return getWorkflowCreateDoc(requestId, savePath, "", recordSet);
    }

    /**
     * 获取流程存为文档的文件，只获取流程归档时生成的文件，流程务必设置在归档时生成文档，如果流程没有归档或者流程归档时没有生成文件会返回空集合
     *
     * @param requestId 流程请求id
     * @param savePath  文件保存目录的绝对路径
     * @param recordSet recordSet
     * @return 流程存为文档的文件路径集合，如果流程存为文档勾选多个生成文件类型，则返回多个文件路径
     * @throws IOException 获取文件时发生异常
     */
    public static List<DocFileInfo> getArchiveWorkflowCreateDoc(int requestId, String savePath, RecordSet recordSet) throws IOException {
        String workflowArchiveTime = WorkflowUtil.getWorkflowArchiveTime(requestId, recordSet);
        if (workflowArchiveTime.isEmpty()) {
            log.info("流程没有归档");
            return new ArrayList<>();
        }
        return getWorkflowCreateDoc(requestId, savePath, workflowArchiveTime, recordSet);
    }


    /**
     * 获取文档内的文件
     *
     * @param docId     文档id
     * @param savePath  保存获取到的文件的目录路径
     * @param type      需要获取的文件类型，1为正文，2为附件，3为全部，如果传入其它值则会获取全部文件
     * @param recordSet recordSet
     * @return 获取到的文件路径集合，如果没有获取到文件则返回空集合
     * @throws IOException 获取文件时发生异常
     */
    public static List<String> getDocFiles(int docId, String savePath, int type, RecordSet recordSet) throws IOException {
        List<String> paths = new ArrayList<>();
        String condition = "";
        if (type == MAIN_BODY_FILE) {
            condition = " and f.isextfile!='1'";
        } else if (type == ATTACHMENT_FILE) {
            condition = " and f.isextfile ='1'";
        }
        recordSet.executeQuery("SELECT i.FILEREALPATH,i.IMAGEFILENAME,i.IMAGEFILEID,i.iszip,f.docid,f.isextfile  " +
                "from docimagefile f,imagefile i where f.docid =? and i.imagefileid = f.IMAGEFILEID " + condition, docId);
        while (recordSet.next()) {
            String fileName = recordSet.getString("IMAGEFILENAME");
            String saveFile = savePath + getSeparator() + fileName;
            getImageFile(recordSet.getInt("IMAGEFILEID"), saveFile);
            paths.add(saveFile);
        }
        return paths;
    }

    /**
     * 获取文档内的文件，包括正文（doc文档）和附件
     *
     * @param docId     文档id
     * @param savePath  保存获取到的文件的目录路径
     * @param recordSet recordSet
     * @return 获取到的文件路径集合，如果没有获取到文件则返回空集合
     * @throws IOException 获取文件时发生异常
     */
    public static List<String> getDocFiles(int docId, String savePath, RecordSet recordSet) throws IOException {
        return getDocFiles(docId, savePath, ALL_FILE, recordSet);
    }

    /**
     * 获取文档的正文文件（必需为doc文档）
     *
     * @param docId     文档id
     * @param savePath  保存获取到的文件的目录路径
     * @param recordSet recordSet
     * @return 获取到的文件路径，如果没有获取到文件则返回空字符串
     * @throws IOException 获取文件时发生异常
     */
    public static String getDocMainBodyFile(int docId, String savePath, RecordSet recordSet) throws IOException {
        List<String> filePaths = getDocFiles(docId, savePath, MAIN_BODY_FILE, recordSet);
        if (filePaths.isEmpty()) {
            return "";
        }
        return filePaths.get(0);
    }

    /**
     * 获取文档的附件文件
     *
     * @param docId     文档id
     * @param savePath  保存获取到的文件的目录路径
     * @param recordSet recordSet
     * @return 获取到的文件路径集合，如果没有获取到文件则返回空集合
     * @throws IOException 获取文件时发生异常
     */
    public static List<String> getDocAttachmentFile(int docId, String savePath, RecordSet recordSet) throws IOException {
        return getDocFiles(docId, savePath, ATTACHMENT_FILE, recordSet);
    }

    /**
     * 获取文档附件的文件路径
     *
     * @param imageFileId 文件id
     * @param saveFile    文件保存路径
     * @throws IOException 获取文件时发生异常
     */
    public static void getImageFile(int imageFileId, String saveFile) throws IOException {
        try (InputStream inputStream = ImageFileManager.getInputStreamById(imageFileId)) {
            Files.copy(inputStream,Paths.get(saveFile));
        }
    }

    /**
     * 获取文档中的所有文件信息，包括文档中的附件,如果附件有多个版本，则只会获取最新一个版本
     *
     * @param docId 文档id
     * @return 文档中的所有文件
     */
    public static List<DocFileInfo> getDocFileInfos(int docId, RecordSet recordSet) {
        log.info("文档id：" + docId);
        List<DocFileInfo> fileInfoList = new ArrayList<>();
        String sql = "SELECT t1.imagefilename,t1.imagefileid,t1.iszip,t2.docfiletype,t2.isextfile,t2.versionid " +
                "FROM imagefile t1 JOIN docimagefile t2 ON t1.imagefileid=t2.imagefileid " +
                "WHERE t2.docid=? AND t2.versionid=(SELECT MAX(versionid) FROM docimagefile WHERE id=t2.id)";
        recordSet.executeQuery(sql, docId);
        while (recordSet.next()) {
            String fileName = recordSet.getString("imagefilename");
            log.info("文件名称：" + fileName);
            int imageFileId = recordSet.getInt("imagefileid");
            boolean isAttachment = "1".equals(recordSet.getString("isextfile"));
            DocFileType fileType = isAttachment ? DocFileType.ATTACHMENT : DocFileType.MAIN_BODY;
            DocFileInfo docFileInfo = new DocFileInfo();
            docFileInfo.setFileName(fileName);
            docFileInfo.setFileType(fileType);
            docFileInfo.setDocId(docId);
            docFileInfo.setImageFileId(imageFileId);
            docFileInfo.setFilePath("");
            docFileInfo.setFromZip(false);
            docFileInfo.setType(recordSet.getString("docfiletype"));
            docFileInfo.setVersionId(recordSet.getInt("versionid"));
            fileInfoList.add(docFileInfo);
        }
        return fileInfoList;
    }

    /**
     * 获取文档正文文件信息
     * @param docId 文档id
     * @param recordSet recordSet
     * @return 文档正文文件信息
     */
    public static Optional<DocFileInfo> getMainBodyDocFileInfo(int docId, RecordSet recordSet) {
        List<DocFileInfo> docFileInfos = getDocFileInfos(docId, recordSet);
        return docFileInfos.stream().filter(i -> i.getFileType() == DocFileType.MAIN_BODY).findAny();
    }

    /**
     * 获取文件流
     *
     * @param imageFileId 文件id
     * @return 文件流
     */
    public static InputStream getFileInputStream(int imageFileId) {
        return ImageFileManager.getInputStreamById(imageFileId);
    }

    /**
     * 根据 imageFileId 获取文件名，imageFileId 为 imagefile 表字段
     *
     * @param imageFileId imageFileId
     * @param recordSet   recordSet
     * @return 文件名
     */
    public static String getFileNameByFileId(int imageFileId, RecordSet recordSet) {
        recordSet.executeQuery("SELECT IMAGEFILENAME from imagefile where imagefileid=?", imageFileId);
        recordSet.next();
        return recordSet.getString("IMAGEFILENAME");
    }

    /**
     * 使用 wps 转 pdf，只有集成了wps在线编辑才可以使用
     *
     * @param newFileName 转换后的文件名称，不带后缀
     * @param imageFileId 需要转换的文件id
     * @return 转换后生成的pdf文件id，如果转换失败则为 empty
     */
    public static Optional<Integer> convertPdfByOfficedService(String newFileName, int imageFileId) {
        Map<String, Object> paramsMap = new HashMap<>(5);
        paramsMap.put("fileId", imageFileId);
        paramsMap.put("fileName", newFileName);
        try {
            return Optional.of(CommonUtil.getOfficedService().convertToPDF(paramsMap));
        } catch (Exception e) {
            log.error("使用 wps 转pdf失败，检查是否安装 wps 服务", e);
            return Optional.empty();
        }
    }

    /**
     * 获取附件的下载地址
     *
     * @param imageFileId 附件id，可通过查询 docimagefile 表的 imagefileid 字段获得
     * @return 附件的下载地址
     */
    public static String getDownloadUrl(int imageFileId) {
        String encryptedImageId = DocDownloadCheckUtil.getDownloadfileidstr(String.valueOf(imageFileId));
        return "/weaver/weaver.file.FileDownload?fileid=" + encryptedImageId;
    }

    /**
     * 将附件绑定到指定文档
     *
     * @param attachmentDocId 附件文档id
     * @param mainDocId       要将附件绑定到的文档id
     * @param recordSet       recordSet
     */
    public static void bindImageFileToDoc(int attachmentDocId, int mainDocId, RecordSet recordSet) {
        DocAccService docAccService = new DocAccService();
        List<String> imageFileIds = AttachUtil.convertDocsToImageID(attachmentDocId + "");
        for (String imageFileId : imageFileIds) {
            if (!isImageFileInDoc(Integer.parseInt(imageFileId), mainDocId, recordSet)) {
                int result = docAccService.buildRelForDoc(attachmentDocId, mainDocId, true, -1);
                if (result == 0) {
                    log.error("附件绑定到文档失败，附件id：" + imageFileId + ",文档id：" + mainDocId);
                }
            }
        }
    }

    /**
     * 附件是否在指定文档中
     *
     * @param imageFileId 附件id
     * @param docId       文档id
     * @param recordSet   recordSet
     * @return 如果存在为true
     */
    public static boolean isImageFileInDoc(int imageFileId, int docId, RecordSet recordSet) {
        recordSet.executeQuery("select 1 from docimagefile where docid=? and imagefileid=?", docId, imageFileId);
        return recordSet.next();
    }

    /**
     * 移除指定文档中的附件
     *
     * @param docId     文档id
     * @param recordSet recordSet
     * @return 是否移除成功
     */
    public boolean removeImageFiles(int docId, RecordSet recordSet) {
        return recordSet.executeUpdate("delete from DocImageFile where docid=? and isextfile='1'", docId);
    }

    /**
     * 替换文档中的文件，可替换正文和附件，替换后文件会产生新的版本
     * @param docId 文档id
     * @param oldImageFileId 旧附件id
     * @param newImageFileId 新附件id
     * @param isMainBody 是否正文
     * @param recordSet recordSet
     * @return 是否替换成功
     */
    public boolean replaceDocFile(int docId,int oldImageFileId, int newImageFileId,int operatorId,
                                  boolean isMainBody,String versionDesc, RecordSet recordSet) throws SqlExecuteException{
        DocAccService accService = new DocAccService();
        // 替换附件
        Map<String, Object> result = accService.replaceAcc(newImageFileId, oldImageFileId,
                docId, versionDesc, new User(operatorId));
        log.info("替换结果：" + result);
        if (CollUtil.isEmpty(result) || !result.containsKey("imagefileId")) {
            return false;
        }
        if (isMainBody) {
            // 如果替换的文件是正文，则需要更新标识为正文
            if (!recordSet.executeUpdate("UPDATE docimagefile SET isextfile=0 WHERE docid=? AND imagefileid=?",
                    docId, newImageFileId)) {
                throw new SqlExecuteException("更新标识为正文失败");
            }
        }
        if (-1 == MapUtil.getInt(result, "docId")) {
            log.info("文档文件替换不成功，正在处理，docId:"+ docId);
            recordSet.executeQuery("SELECT id FROM docimagefile WHERE docid=? AND imagefileid=?",
                    docId, oldImageFileId);
            recordSet.next();
            int id = recordSet.getInt("id");
            log.info("查询到的docimagefile表id：" + id);

            String updateSql = "UPDATE docimagefile SET id=?,docid=?,isextfile=? WHERE imagefileid=?";
            if (!recordSet.executeUpdate(updateSql, id, docId, isMainBody ? 0 : 1, newImageFileId)) {
                throw new SqlExecuteException("处理正文替换失败，sql:" + updateSql);
            }
        }
        // 修改版本信息
        if (!recordSet.executeUpdate("UPDATE docimagefile SET versiondetail=? WHERE docid=? AND imagefileid=?",
                versionDesc, docId, newImageFileId)) {
            throw new SqlExecuteException("修改版本信息失败");
        }
        return true;
    }

    /**
     * 根据文件名，获取 docimagefile 表中的 docfiletype（文件类型）
     * @param fileName 文件名
     * @return 文件类型
     */
    public static Optional<Integer> getDocFileType(String fileName) {
        String suffix = FileUtil.getSuffix(fileName);
        if (suffix.isEmpty()) {
            return Optional.empty();
        }
        switch (suffix) {
            case "doc":
                return Optional.of(3);
            case "docx":
                return Optional.of(7);
            case "xls":
                return Optional.of(4);
            case "xlsx":
                return Optional.of(8);
            case "ppt":
                return Optional.of(5);
            case "pptx":
                return Optional.of(9);
            case "wps":
                return Optional.of(6);
            case "et":
                return Optional.of(10);
            default:
                return Optional.empty();
        }
    }

    /**
     * 将实体文件生成附件
     * @param fileName 附件名称
     * @param filePath 附件路径
     * @return 附件id
     * @throws IOException IO异常
     */
    public static int createNewImageFile(String fileName,String filePath) throws IOException {
        ImageFileManager imageFileManager = new ImageFileManager();
        imageFileManager.resetParameter();
        imageFileManager.setImagFileName(fileName);
        imageFileManager.setData(Files.readAllBytes(Paths.get(filePath)));
        return imageFileManager.saveImageFile();
    }

    /**
     * 将传入的文件生成为文档，传入的文件会保存为文档附件
     * @param filePath 文档附件文件路径
     * @param docDirId 文档目录id
     * @param docName 文档名称，需要带上文件名后缀
     * @param docCreator 文档创建者id
     * @return 文档id
     */
    public static Optional<Integer> createDocByFile(String filePath, int docDirId, String docName, int docCreator) {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("创建文档失败，文件不存在");
            return Optional.empty();
        }
        int newImageFile;
        try {
            newImageFile = createNewImageFile(docName, filePath);
        } catch (IOException e) {
            log.error("创建文档失败，创建附件异常",e);
            return Optional.empty();
        }
        DocSaveService docSaveService = new DocSaveService();
        try {
            int docId = docSaveService.accForDoc(docDirId, newImageFile, new User(docCreator));
            return Optional.of(docId);
        } catch (Exception e) {
            log.error("创建文档失败，创建文档异常",e);
            return Optional.empty();
        }
    }

    private static String getSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

}
