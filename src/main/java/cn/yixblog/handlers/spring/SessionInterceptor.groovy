package cn.yixblog.handlers.spring

import cn.yixblog.core.admin.IAdminAccountStorage
import cn.yixblog.core.user.IUserAccountStorage
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.log4j.Logger
import org.springframework.util.AntPathMatcher
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * Created by dyb on 14-1-4.
 */
class SessionInterceptor extends HandlerInterceptorAdapter {
    private String[] adminBlackList;
    private String[] userBlackList;
    private boolean open;
    private Logger logger = Logger.getLogger(getClass());
    @Resource(name = "adminAccountStorage")
    private IAdminAccountStorage adminAccountStorage;
    @Resource(name = "userAccountStorage")
    private IUserAccountStorage userAccountStorage;

    public void setAdminBlackList(String[] adminBlackList) {
        this.adminBlackList = adminBlackList;
    }

    public void setUserBlackList(String[] userBlackList) {
        this.userBlackList = userBlackList;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!open) {
            return true;
        }
        HttpSession session = request.getSession();
        JSONObject admin = (JSONObject) session.getAttribute("admin");
        JSONObject user = (JSONObject) session.getAttribute("user");
        String uri = getRequestURI(request);
        if (admin == null) {
            for (String adminBlackpattern : adminBlackList) {
                if (checkUriMatch(uri, adminBlackpattern)) {
                    PrintWriter writer = response.getWriter();
                    writer.write(JSON.toJSONString([success: false, msg: "您必须登录管理员账号后才能访问"]));
                    writer.flush();
                    writer.close();
                    return false;
                }
            }
        }
        if (user == null) {
            for (String userBlackpattern : userBlackList) {
                if (checkUriMatch(uri, userBlackpattern)) {
                    PrintWriter writer = response.getWriter();
                    writer.write(JSON.toJSONString([success: false, msg: "您必须登录"]));
                    writer.flush();
                    writer.close();
                    return false;
                }
            }
        }
        boolean handleResult = super.preHandle(request, response, handler);
        if (admin != null) {
            updateSessionAdmin(session, admin.getIntValue("id"));
        }
        if (user != null) {
            updateSessionUser(session, user.getIntValue("id"));
        }
        return handleResult;
    }

    private void updateSessionAdmin(HttpSession session, int adminId) {
        JSONObject updatedAdmin = adminAccountStorage.queryAdminById(adminId);
        session.setAttribute("admin", updatedAdmin);
    }

    private void updateSessionUser(HttpSession session, int userId) {
        JSONObject updatedUser = userAccountStorage.queryUser(userId).getJSONObject("user");
        logger.debug("session update user info:" + updatedUser.toJSONString());
        session.setAttribute("user", updatedUser);
    }

    private String getRequestURI(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ctxPath = request.getContextPath();
        return uri.replace(ctxPath, "");
    }

    private boolean checkUriMatch(String uri, String pattern) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, uri);
    }

}
