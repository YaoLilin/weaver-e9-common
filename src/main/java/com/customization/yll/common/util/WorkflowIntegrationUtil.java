package com.customization.yll.common.util;

import weaver.conn.RecordSet;
import weaver.general.Util;
import weaver.hrm.HrmUserVarify;
import weaver.hrm.User;
import weaver.ofs.bean.OfsSysInfo;
import weaver.ofs.bean.OfsTodoData;
import weaver.ofs.dao.OfsRequestBaseDao;
import weaver.ofs.manager.utils.OfsTodoDataUtils;
import weaver.ofs.service.OfsSysInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一待办工具类
 * @author yaolilin
 */
public class WorkflowIntegrationUtil {

    /**
     * 获取统一待办的流程链接
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return 待办链接
     */
    public static String getWorkflowUtl(HttpServletRequest request, HttpServletResponse response,boolean isPc) {
        RecordSet rs = new RecordSet();
        User user = HrmUserVarify.getUser(request,response);
        int userId = user.getUID();
        String sysId = request.getParameter("sysId") ;
        String workflowId = request.getParameter("workflowId") ;
        String flowId = request.getParameter("flowId") ;
        OfsSysInfoService ofsSysInfoService = new OfsSysInfoService() ;
        OfsSysInfo ofsSysInfo = ofsSysInfoService.getOneBean(Util.getIntValue(sysId , 0)) ;
        OfsTodoDataUtils todoDataUtils = new OfsTodoDataUtils() ;
        OfsRequestBaseDao ofsRequestBaseDao = new OfsRequestBaseDao() ;
        String requestId = ofsRequestBaseDao.getRequestid(ofsSysInfo.getSyscode() , Util.getIntValue(workflowId , 0) , flowId , rs.getDBType()) ;
        OfsTodoData todoData = todoDataUtils.getTodoData(requestId , Util.null2String(userId)) ;
        return  isPc ? todoData.getPcurl() : todoData.getAppurl() ;
    }
}
