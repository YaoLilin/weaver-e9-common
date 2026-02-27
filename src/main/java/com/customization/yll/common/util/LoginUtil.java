package com.customization.yll.common.util;

import com.customization.yll.common.IntegrationLog;
import weaver.general.Util;
import weaver.general.WHashMap;
import weaver.hrm.OnLineMonitor;
import weaver.hrm.User;
import weaver.hrm.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 姚礼林
 * @desc 用户登陆工具类
 * @date 2024/6/3
 */
public class LoginUtil {
    private static final IntegrationLog log = new IntegrationLog(LoginUtil.class);

    /**
     * 创建session和cookie ，进行登录
     *
     * @param userid              用户id
     * @param request             HttpServletRequest
     * @param response            HttpServletResponse
     * @param maxInactiveInterval 最大非活动时间，单位：秒，过了此时间没有操作则退出登录
     * @return 是否登录成功
     */
    public static boolean createSessionAndCookie(int userid, HttpServletRequest request, HttpServletResponse response
            , int maxInactiveInterval) {
        User user = new UserManager().getUserByUserIdAndLoginType(userid, "1");
        if (user != null && user.getUID() != 0 && !"".equals(user.getLoginid())) {
            request.getSession(true).setMaxInactiveInterval(maxInactiveInterval);
            request.getSession().setAttribute("weaver_user@bean", user);

            request.getSession(true).setAttribute("moniter", new OnLineMonitor("" + user.getUID(), user.getLoginip()));
            Util.setCookie(response, "loginfileweaver", "/main.jsp", maxInactiveInterval);
            Util.setCookie(response, "loginidweaver", "" + user.getUID(), maxInactiveInterval);
            Util.setCookie(response, "languageidweaver", "7", maxInactiveInterval);

            Map logmessages = (Map) request.getSession().getServletContext().getAttribute("logmessages");
            if (logmessages == null) {
                logmessages = new WHashMap();
                logmessages.put("" + user.getUID(), "");
                request.getSession().getServletContext().setAttribute("logmessages", logmessages);
            }
            //登录日志
            weaver.systeminfo.SysMaintenanceLog log1 = new weaver.systeminfo.SysMaintenanceLog();
            log1.resetParameter();
            log1.setRelatedId(userid);
            log1.setRelatedName(user.getLastname());
            log1.setOperateType("6");
            log1.setOperateDesc("");
            log1.setOperateItem("60");
            log1.setOperateUserid(userid);
            log1.setClientAddress(request.getRemoteAddr());
            try {
                log1.setSysLogInfo();
            } catch (Exception e) {
                log.error("记录登录日志发生异常", e);
            }
            return true;
        }
        return false;

    }
}
