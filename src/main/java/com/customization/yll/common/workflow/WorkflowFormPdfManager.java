package com.customization.yll.common.workflow;

import com.customization.yll.common.doc.bean.DocFileInfo;
import com.customization.yll.common.util.DocUtil;
import com.customization.yll.common.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import weaver.conn.RecordSet;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolilin
 * @desc 获取流程创建文档生成的表单pdf，需要标准功能的流程存为文档执行之后才能获取到表单PDF，此类不生成表单PDF，只是获取标准功能
 * 生成的表单PDF
 * @date 2024/9/20
 **/
public class WorkflowFormPdfManager {
    private final String savePath;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public WorkflowFormPdfManager(String savePath) {
        this.savePath = savePath;
    }

    /**
     * 获取流程创建文档生成的表单pdf
     * @param requestId 请求id
     * @param pdfCreateAfterTime 表单pdf需要在指定的时间之后创建
     * @return 表单pdf文件信息
     * @throws FileNotFoundException pdf文件未找到
     */
    public DocFileInfo getFormPdf(int requestId,String pdfCreateAfterTime) throws FileNotFoundException {
        List<DocFileInfo> docList = getWorkflowCreateDocs(requestId,pdfCreateAfterTime);
        if (docList.isEmpty()) {
            throw new FileNotFoundException("获取不到表单pdf");
        }
        for (DocFileInfo docFileInfo : docList) {
            if ("pdf".equals(FileUtil.getSuffix(docFileInfo.getFileName()))) {
                return docFileInfo;
            }
        }
        throw new FileNotFoundException("获取不到表单pdf");
    }

    /**
     * 获取流程创建文档生成的表单pdf
     * @param requestId 请求id
     * @return 表单pdf文件信息
     * @throws FileNotFoundException pdf文件未找到
     */
    public DocFileInfo getFormPdf(int requestId) throws FileNotFoundException {
        return getFormPdf(requestId, null);
    }

    @NotNull
    private List<DocFileInfo> getWorkflowCreateDocs(int requestId,String pdfCreateAfterTime) {
        List<DocFileInfo> docList;
        try {
            if (StringUtils.isEmpty(pdfCreateAfterTime)) {
                docList = DocUtil.getWorkflowCreateDoc(requestId, savePath, new RecordSet());
            }else {
                docList = DocUtil.getWorkflowCreateDoc(requestId, savePath, pdfCreateAfterTime, new RecordSet());
            }
        } catch (IOException e) {
            log.error("获取流程创建文档文件异常",e);
            return new ArrayList<>();
        }
        return docList;
    }
}
