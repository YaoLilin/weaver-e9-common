package com.engine.interfaces.yll.common.web;

import cn.hutool.core.util.StrUtil;
import com.customization.yll.common.util.FileUtil;
import com.customization.yll.common.util.Md5Util;
import com.customization.yll.common.web.modal.vo.OpenApiResult;
import com.engine.common.util.ServiceUtil;
import com.engine.interfaces.yll.common.domain.dto.FileDownloadDto;
import com.engine.interfaces.yll.common.service.FileDownloadService;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;

/**
 * @author 姚礼林
 * @desc 文件下载接口
 * @date 2025/6/17
 **/
public class FileDownloadAction {
    public static final String SECRET = "dd8a36f2-f0bb-4021-b829-083b1fd8489d";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 文件下载
     * @param fileId 为imagefile表的imagefileid字段
     * @param token 验证token，为32位小写md5，生成规则为：文件id+密钥
     * @return 生成下载
     */
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public Response download(@QueryParam("fileId") String fileId ,@QueryParam("token") String token) {
        try {
            if (StrUtil.isEmpty(fileId)) {
                OpenApiResult<String> result = OpenApiResult.failed("[fileId] 参数无值",
                        Response.Status.NOT_FOUND.getStatusCode());
                return Response.ok().entity(result).build();
            }
            if (StrUtil.isEmpty(fileId)) {
                OpenApiResult<String> result = OpenApiResult.failed("[token] 参数无值",
                        Response.Status.NOT_FOUND.getStatusCode());
                return Response.ok().entity(result).build();
            }
            String md5 = Md5Util.createMd5(fileId + SECRET);
            if (!md5.equals(token)) {
                OpenApiResult<String> result = OpenApiResult.failed("token 验证失败",
                        Response.Status.FORBIDDEN.getStatusCode());
                return Response.ok().entity(result).build();
            }
            FileDownloadDto fileInfo = ServiceUtil.getService(FileDownloadService.class)
                    .getFile(Integer.parseInt(fileId));
            String suffix = FileUtil.getSuffix(fileInfo.getFileName());
            // 这里需要编码，不然下载之后文件名显示不了中文
            String filename = URLEncoder.encode(fileInfo.getFileName(),"UTF-8") ;
            return Response
                    .ok(fileInfo.getInputStream())
                    .header("Character-Encoding", "utf-8")
                    .header("Content-Type","application/"+suffix)
                    .header("Content-disposition", "attachment;filename=" + filename)
                    .header("Cache-Control", "no-cache").build();
        } catch (Exception e) {
            log.error("文件下载失败",e);
            OpenApiResult<String> result = OpenApiResult.failed("文件下载失败，发生异常",
                    Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            return Response.ok().entity(result).build();
        }
    }
}
