package com.customization.yll.common.workflow;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.util.DocUtil;
import com.customization.yll.common.util.WorkflowUtil;
import com.engine.workflow.biz.requestForm.WfToDocBiz;
import com.engine.workflow.biz.requestForm.WfWaterMark4WfToDocBiz;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import org.jetbrains.annotations.NotNull;
import org.xhtmlrenderer.pdf.ITextRenderer;
import weaver.conn.RecordSet;
import weaver.file.FileUpload;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;
import weaver.system.SystemComInfo;
import weaver.workflow.workflow.WorkflowConfigComInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * @author yaolilin
 * @desc 流程表单转pdf，用的是标准功能的流程存为文档功能，请确保开启流程存为文档功能并勾选生成表单PDF
 * @date 2024/12/9
 **/
public class WorkflowFormPdfCreator {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 生成流程表单pdf
     * @param fileSavePath pdf文件保存路径，为全路径，不能是目录，要加上文件名
     * @param nodeId 使用指定节点的模板生成pdf
     * @param requestId 请求id
     * @return 是否生成成功
     */
    public boolean createFormPdf(String fileSavePath, int nodeId, int requestId) {
        int workflowId = WorkflowUtil.getWorkflowId(requestId, new RecordSet());
        Optional<Integer> fileIdOp = createFormPdf(workflowId, nodeId, requestId);
        if (!fileIdOp.isPresent()) {
            return false;
        }
        try {
            // 保存pdf文件到指定路径
            DocUtil.getImageFile(fileIdOp.get(), fileSavePath);
            return true;
        } catch (IOException e) {
            log.error("获取文件失败", e);
            return false;
        }
    }

    /**
     * 生成流程表单pdf,获取pdf文件的文件id
     * @param workflowId 流程id
     * @param nodeId 使用指定节点的模板生成pdf
     * @param requestId 请求id
     * @return 表单pdf文件的文件id，该文件id为 imagefile 表的id
     */
    public Optional<Integer> createFormPdf(int workflowId, int nodeId, int requestId) {
        LinkedHashMap<String, String> fileIdMap = workflowSaveAsDoc(workflowId, nodeId, requestId);
        String pdfFileId = fileIdMap.get("offline_pdf");
        if (StrUtil.isEmpty(pdfFileId)) {
            log.error("生成pdf失败，没有找到 pdf fileId");
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(pdfFileId));
    }

    /**
     * 生成流程表单pdf,获取pdf文件的文件id
     * @param nodeId 使用指定节点的模板生成pdf
     * @param requestId 请求id
     * @return 表单pdf文件的文件id，该文件id为 imagefile 表的id
     */
    public Optional<Integer> createFormPdf(int nodeId, int requestId) {
        int workflowId = WorkflowUtil.getWorkflowId(requestId, new RecordSet());
        return createFormPdf(workflowId, nodeId, requestId);
    }

    /**
     * 还未做好
     * 生成流程表单pdf，先获取离线表单html，然后再转为pdf，这样pdf会更加还原
     * @param fileSavePath pdf文件保存路径，为全路径，不能是目录，要加上文件名
     * @param workflowId  流程id
     * @param nodeId 使用指定节点的模板生成pdf
     * @param requestId 请求id
     * @return 是否生成成功
     */
    public boolean createFormPdfByHtml(String fileSavePath, int workflowId, int nodeId, int requestId) {
        LinkedHashMap<String, String> fileIdMap = workflowSaveAsDoc(workflowId, nodeId, requestId);
        String htmlFileId = fileIdMap.get("offline_html");
        String htmlFileName = UUID.randomUUID() + ".html";
        // todo 临时目录 需要修改
        String htmlFilePath = "/Users/yaolilin/weaver/ecology/filesystem/temp/" + htmlFileName;
        try {
            DocUtil.getImageFile(Integer.parseInt(htmlFileId), htmlFilePath);
        } catch (IOException e) {
            return false;
        }
        File inputFile = new File(htmlFilePath);
        File outputFile = new File(fileSavePath);
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, Files.newOutputStream(outputFile.toPath()));
            document.open();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(inputFile);
            renderer.layout();
            renderer.createPDF(writer.getOs());
            document.close();
            writer.close();
        } catch (DocumentException | IOException | com.lowagie.text.DocumentException e) {
            return false;
        }
        return true;
    }

    /**
     * 触发流程存为文档功能
     * @return 各文件类型对应的fileId, 如："online_html" -> "6817" ，"offline_html" -> "6818"，"offline_pdf" -> "6819"
     */
    @NotNull
    private LinkedHashMap<String, String> workflowSaveAsDoc(int workflowId, int nodeId, int requestId) {
        WfToDocBiz wfbiz = new WfToDocBiz();
        // 防止高并发文件名重复
        String fileName = UUID.randomUUID().toString();
        String tempPath = getFileSavePath();
        // 是否开启水印
        boolean isOpenWaterMark = WfWaterMark4WfToDocBiz.isOpenWaterMark(Util.getIntValue(workflowId));
        String onlineHtmlMethod = Util.null2String(new WorkflowConfigComInfo().getValue("onlineHtmlMethod"));
        String modeId = wfbiz.getModeid(Util.getIntValue(workflowId), Util.getIntValue(requestId), nodeId);
        // 离线html/pdf
        WfToDocBiz wfToDocBiz = new WfToDocBiz(new User(1), 100, getKeepSign(workflowId + ""),
                getDocFiles(workflowId + ""), modeId, isOpenWaterMark);
        wfToDocBiz.setOnlineHtmlMethod(onlineHtmlMethod);
        wfToDocBiz.generatepdfandhtml(requestId + "", fileName, tempPath, "submit");
        String requestName = WorkflowUtil.getRequestName(requestId, new RecordSet());
        return wfToDocBiz.getfileids(requestName, fileName, tempPath);
    }

    /**
     * 获得文件保存目录
     */
    private String getFileSavePath() {
        SystemComInfo syscominfo = new SystemComInfo();
        return FileUpload.getCreateDir(syscominfo.getFilesystem());
    }

    /**
     * 得到keepsign 是否保留签字意见
     */
    private int getKeepSign(String workflowid) {
        RecordSet rs = new RecordSet();
        int keepsign = 0;
        rs.executeQuery("select keepsign from workflow_base where id = ?", workflowid);
        if (rs.next()) {
            keepsign = rs.getInt("keepsign");
        }
        return keepsign;
    }

    /**
     * 得到docfiles  文档附件 在线表单/离线表单(HTML)/离线表单(PDF)
     */
    private String getDocFiles(String workflowid) {
        RecordSet rs = new RecordSet();
        String docfiles = "";
        String wfdocpath = "";
        rs.executeQuery("select docfiles,wfdocpath from workflow_base where id = ?", workflowid);
        if (rs.next()) {
            docfiles = Util.null2String(rs.getString("docfiles"));
            wfdocpath = Util.null2String(rs.getString("wfdocpath"));
        }
        if (docfiles.isEmpty() && !wfdocpath.isEmpty()) {
            docfiles = "1";
            rs.executeUpdate("update workflow_base set docfiles ='1' where id = ?", workflowid);
        }
        return docfiles;
    }
}
