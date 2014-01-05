package cn.yixblog.controller.sysadmin;

import cn.yixblog.controller.SessionTokens;
import cn.yixblog.core.admin.IAdminAccountStorage;
import cn.yixblog.utils.ResetCodeFactory;
import cn.yixblog.utils.bean.ResetCode;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-5-28
 * Time: 上午11:39
 */
@RestController
@RequestMapping("/sys")
@SessionAttributes({SessionTokens.ADMIN_TOKEN, SessionTokens.VALIDATE_TOKEN})
public class AdminAccountController {
    @Resource(name = "adminAccountStorage")
    private IAdminAccountStorage adminAccountStorage;
    private Logger logger = Logger.getLogger(getClass());

    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    public JSONObject doLogin(@RequestParam String uid, @RequestParam String pwd, @RequestParam String validate, DefaultSessionAttributeStore status, WebRequest request, ModelMap modelMap) {
        String sessionValidate = (String) modelMap.remove(SessionTokens.VALIDATE_TOKEN);
        status.cleanupAttribute(request, SessionTokens.VALIDATE_TOKEN);
        if (sessionValidate != null && sessionValidate.equals(validate)) {
            JSONObject res = adminAccountStorage.doLogin(uid, pwd);
            logger.debug("admin login:" + uid);
            if (res.get("admin") != null) {
                modelMap.addAttribute("admin", res.get("admin"));
            }
            return res;
        }
        JSONObject res = new JSONObject();
        res.put("success", false);
        res.put("msg", "验证码不正确");
        return res;
    }

    @RequestMapping(value = "/admin/loginstatus", method = RequestMethod.POST)
    public JSONObject loginStatus(ModelMap modelMap) {
        JSONObject admin = (JSONObject) modelMap.get("admin");
        JSONObject res = new JSONObject();
        if (admin != null) {
            res.put("admin", admin);
            res.put("success", true);
        } else {
            res.put("success", false);
        }
        return res;
    }

    @RequestMapping(value = "/admin/logout", method = RequestMethod.POST)
    public JSONObject logout(DefaultSessionAttributeStore status, WebRequest request, ModelMap modelMap) {
        JSONObject res = new JSONObject();
        modelMap.remove(SessionTokens.ADMIN_TOKEN);
        status.cleanupAttribute(request, SessionTokens.ADMIN_TOKEN);
        res.put("success", true);
        return res;
    }

    @RequestMapping("/reset/{resetCode}.htm")
    //todo have to change
    public String doReset(@PathVariable String resetCode, Model model) {
        ResetCode code = ResetCodeFactory.generateResetCode(resetCode);
        if (code != null) {
            switch (code.getCodeType()) {
                case ResetCode.TYPE_CONFIRM_EMAIL:
                    JSONObject json = adminAccountStorage.doConfirmEmail(resetCode);
                    model.addAttribute("res", json);
                    return "account/admin_confirm_email_res";
                case ResetCode.TYPE_RESET_PWD:
                    json = adminAccountStorage.queryResetCode(resetCode);
                    model.addAttribute("res", json);
                    return "account/admin_force_reset_pwd_dialog";
                case ResetCode.TYPE_RESET_EMAIL:
                    json = adminAccountStorage.queryResetCode(resetCode);
                    model.addAttribute("res", json);
                    return "account/admin_reset_email";
            }
        }
        return "redirect:/static/pages/illegal.html";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public JSONObject saveAdmin(@RequestParam String uid, @RequestParam String pwd, @RequestParam String email) {
        JSONObject res = new JSONObject();
        if (!uid.matches("[0-9a-zA-Z_]+")) {
            res.put("success", false);
            res.put("msg", "用户名中包含不合法字符");
        } else {
            res = adminAccountStorage.saveAdminAccount(uid, pwd, email);
        }
        return res;
    }

    @RequestMapping(value = "/admin/change_pwd", method = RequestMethod.POST)
    public JSONObject changePwd(@RequestParam String oldPwd, @RequestParam String newPwd, @ModelAttribute("admin") JSONObject admin) {
        int adminId = admin.getIntValue("id");
        return adminAccountStorage.doChangePassword(adminId, oldPwd, newPwd);
    }

    @RequestMapping(value = "/admins", method = RequestMethod.GET)
    public JSONObject listAdminAccounts(@ModelAttribute("admin") JSONObject admin,
                                        @RequestParam(required = false) String uid, @RequestParam(required = false) String email,
                                        @RequestParam(required = false, defaultValue = "1") int page,
                                        @RequestParam(required = false, defaultValue = "20") int pageSize) {
        if (!admin.getBooleanValue("adminmanage")) {
            JSONObject errorMsg = new JSONObject();
            errorMsg.put("success", false);
            errorMsg.put("msg", "您没有权限查看管理员账号");
            return errorMsg;
        }
        return adminAccountStorage.queryAdminList(uid, email, page, pageSize);
    }

    @RequestMapping(value = "/admin/{adminId}", method = RequestMethod.GET)
    public JSONObject adminDetail(@ModelAttribute("admin") JSONObject admin, @PathVariable int adminId) {
        if (!admin.getBooleanValue("adminmanage")) {
            JSONObject errorMsg = new JSONObject();
            errorMsg.put("success", false);
            errorMsg.put("msg", "您没有权限查看管理员账号");
            return errorMsg;
        }
        return adminAccountStorage.queryAdminById(adminId);
    }

    @RequestMapping(value = "/admin/{adminId}", method = RequestMethod.DELETE)
    public JSONObject deleteAdmin(@PathVariable int adminId, @ModelAttribute("admin") JSONObject admin) {
        JSONObject res = new JSONObject();
        if (adminId == admin.getIntValue("id")) {
            res.put("success", false);
            res.put("msg", "不能删除自己的账号");
            return res;
        }
        if (!admin.getBooleanValue("adminmanage")) {
            res.put("success", false);
            res.put("msg", "您没有权限删除管理员账号");
            return res;
        }
        return adminAccountStorage.deleteAdmin(adminId);
    }

    @RequestMapping(value = "/admin/{id}", method = RequestMethod.PUT)
    public JSONObject editAdmin(@PathVariable int id, @RequestParam String pwd, @RequestParam(required = false) String email, @ModelAttribute("admin") JSONObject admin) {
        JSONObject res = new JSONObject();
        if (!admin.getBooleanValue("adminmanage")) {
            res.put("success", false);
            res.put("msg", "您没有权限修改管理员账号");
            return res;
        }
        return adminAccountStorage.doEditAdmin(id, pwd, email);
    }
}
