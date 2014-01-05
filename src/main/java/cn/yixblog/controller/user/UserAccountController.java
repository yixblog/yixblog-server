package cn.yixblog.controller.user;

import cn.yixblog.controller.SessionTokens;
import cn.yixblog.core.user.IUserAccountStorage;
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
 * Date: 13-6-15
 * Time: 下午5:17
 */
@RestController
@RequestMapping("/user")
@SessionAttributes({SessionTokens.USER_TOKEN, SessionTokens.VALIDATE_TOKEN})
public class UserAccountController {
    private static final String UID_REX = "[0-9A-Za-z_]+";
    @Resource(name = "userAccountStorage")
    private IUserAccountStorage userAccountStorage;
    private Logger logger = Logger.getLogger(getClass());

    @RequestMapping(value = "/account/login", method = RequestMethod.POST)
    public JSONObject doLogin(@RequestParam String uid, @RequestParam String pwd, @RequestParam String validate, DefaultSessionAttributeStore status, WebRequest request, ModelMap modelMap) {
        JSONObject res = new JSONObject();
        String sessValidate = (String) modelMap.remove(SessionTokens.VALIDATE_TOKEN);
        status.cleanupAttribute(request, SessionTokens.VALIDATE_TOKEN);
        if (!uid.matches(UID_REX)) {
            res.put("success", false);
            res.put("msg", "用户名中包含非法字符");
            return res;
        }
        if (sessValidate == null || !sessValidate.toLowerCase().equals(validate.toLowerCase())) {
            res.put("success", false);
            res.put("msg", "验证码不正确");
            return res;
        }
        res = userAccountStorage.doUserLogin(uid, pwd);
        if (res.getBooleanValue("success")) {
            modelMap.addAttribute(SessionTokens.USER_TOKEN, res.getJSONObject("user"));
        }
        return res;
    }

    @RequestMapping(value = "/account/logout", method = RequestMethod.POST)
    public JSONObject logout(DefaultSessionAttributeStore status, WebRequest request, ModelMap modelMap) {
        JSONObject user = (JSONObject) modelMap.remove(SessionTokens.USER_TOKEN);
        logger.info("user logout:" + user.getString("nick"));
        status.cleanupAttribute(request, SessionTokens.USER_TOKEN);
        JSONObject res = new JSONObject();
        res.put("success", true);
        res.put("msg", "成功退出");
        return res;
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public JSONObject doRegister(@RequestParam String uid, @RequestParam String pwd,
                                 @RequestParam(required = false) String nick,
                                 @RequestParam(required = false) String email, @RequestParam String sex,
                                 @RequestParam String validate, DefaultSessionAttributeStore status, WebRequest request, ModelMap modelMap) {
        if (nick == null) {
            nick = uid;
        }
        JSONObject res = new JSONObject();
        String sessionValidate = (String) modelMap.remove(SessionTokens.VALIDATE_TOKEN);
        logger.debug("session:" + sessionValidate + ",request:" + validate);
        status.cleanupAttribute(request, SessionTokens.VALIDATE_TOKEN);
        if (sessionValidate == null || !sessionValidate.toLowerCase().equals(validate.toLowerCase())) {
            res.put("success", false);
            res.put("msg", "验证码不正确");
            return res;
        }
        if (!uid.matches(UID_REX)) {
            res.put("success", false);
            res.put("msg", "用户名中包含非法字符");
            return res;
        }
        switch (sex) {
            case "男":
            case "女":
            case "保密":
                break;
            default:
                sex = "保密";
        }
        return userAccountStorage.doRegisterUser(uid, pwd, nick, email, sex);
    }

    @RequestMapping(value = "/account/forget_password", method = RequestMethod.POST)
    public JSONObject forgetPwdRequest(@RequestParam String uid, @RequestParam String email) {
        return userAccountStorage.queryForgetPasswordRequest(uid, email);
    }


    @RequestMapping("/reset/{resetcode}.htm")
    //todo change
    public String queryResetCode(@PathVariable("resetcode") String resetCode, Model model) {
        ResetCode reset = ResetCodeFactory.generateResetCode(resetCode);
        if (reset != null) {
            switch (reset.getCodeType()) {
                case ResetCode.TYPE_CONFIRM_EMAIL:
                    JSONObject json = userAccountStorage.doConfirmEmail(resetCode);
                    model.addAttribute("res", json);
                    return "account/user_confirm_email_res";
                case ResetCode.TYPE_RESET_EMAIL:
                    json = userAccountStorage.queryResetEvent(resetCode);
                    model.addAttribute("res", json);
                    return "account/user_confirm_email";
                case ResetCode.TYPE_RESET_PWD:
                    json = userAccountStorage.queryResetEvent(resetCode);
                    model.addAttribute("res", json);
                    return "account/user_reset_pwd";
            }
        }
        return "redirect:/static/pages/illegal.html";
    }

    @RequestMapping(value = "/account/reset_email", method = RequestMethod.POST)
    public JSONObject resetEmail(@RequestParam String email, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        return userAccountStorage.doChangeEmail(user.getIntValue("id"), email);
    }

    @RequestMapping(value = "/account/reset_pwd", method = RequestMethod.POST)
    public JSONObject resetPwd(@RequestParam String pwd, @RequestParam String resetCode) {
        return userAccountStorage.doForceChangePassword(resetCode, pwd);
    }

    @RequestMapping(value = "/account/confirm_email", method = RequestMethod.POST)
    public JSONObject confirmEmail(@ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        return userAccountStorage.requestConfirmEmail(user.getIntValue("id"));
    }

    @RequestMapping(value = "/account/change_pwd", method = RequestMethod.POST)
    public JSONObject changePwd(@RequestParam String oldPwd, @RequestParam String newPwd, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        return userAccountStorage.doChangePwd(user.getIntValue("id"), oldPwd, newPwd);
    }

    @RequestMapping(value = "/account", method = RequestMethod.PUT)
    public JSONObject doChangeInfo(@RequestParam String nick, @RequestParam String sex, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        JSONObject res = userAccountStorage.editUser(user.getIntValue("id"), null, nick, null, sex, null);
        if (res.getBooleanValue("success")) {
            user.put("nick", nick);
            user.put("sex", sex);
        }
        return res;
    }

    @RequestMapping(value = "/account/change_avatar", method = RequestMethod.POST)
    public JSONObject doChangeAvatar(@RequestParam String avatar, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        JSONObject res = userAccountStorage.editUser(user.getIntValue("id"), null, null, null, null, avatar);
        if (res.getBooleanValue("success")) {
            user.put("avatar", avatar);
        }
        return res;
    }

}
