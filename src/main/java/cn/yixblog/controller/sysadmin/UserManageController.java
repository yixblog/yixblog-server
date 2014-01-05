package cn.yixblog.controller.sysadmin;

import cn.yixblog.controller.SessionTokens;
import cn.yixblog.core.user.IUserAccountStorage;
import cn.yixblog.utils.DateUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-6-16
 * Time: 下午11:14
 */
@RequestMapping("/sys")
@SessionAttributes(SessionTokens.ADMIN_TOKEN)
@RestController
public class UserManageController {
    @Resource(name = "userAccountStorage")
    private IUserAccountStorage userAccountStorage;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public JSONObject queryUser(@RequestParam(defaultValue = "1", required = false) int page,
                                @RequestParam(defaultValue = "20", required = false) int pageSize,
                                @RequestParam(required = false) String uid, @RequestParam(required = false) String email,
                                @RequestParam(required = false) String nick, @RequestParam(required = false) String qq,
                                @RequestParam(required = false) String weibo, @RequestParam(required = false) String startDate,
                                @RequestParam(required = false) String endDate, @ModelAttribute(SessionTokens.ADMIN_TOKEN) JSONObject admin) {
        if (!admin.getBooleanValue("accountmanage")) {
            JSONObject res = new JSONObject();
            res.put("success", false);
            res.put("msg", "您没有用户管理权限");
            return res;
        }
        JSONObject params = new JSONObject();
        String dateFormat = "yyyy-MM-dd";
        if (uid != null) {
            params.put("uid", uid);
        }
        if (email != null) {
            params.put("email", email);
        }
        if (nick != null) {
            params.put("nick", nick);
        }
        if (qq != null) {
            params.put("qq", qq);
        }
        if (weibo != null) {
            params.put("weibo", weibo);
        }
        if (startDate != null) {
            params.put("regtimebegin", DateUtils.getTimeMillis(startDate, dateFormat));
        }
        if (endDate != null) {
            params.put("regtimeend", DateUtils.getTimeMillis(endDate, dateFormat));
        }
        return userAccountStorage.queryUsers(params, page, pageSize);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public JSONObject getUserInfo(@PathVariable int id, @ModelAttribute(SessionTokens.ADMIN_TOKEN) JSONObject admin) {
        if (!admin.getBooleanValue("accountmanage")) {
            JSONObject res = new JSONObject();
            res.put("success", false);
            res.put("msg", "您没有用户管理权限");
            return res;
        }
        return userAccountStorage.queryUser(id);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public JSONObject editUser(@PathVariable int id, @RequestParam(required = false) String nick,
                               @RequestParam(required = false) String pwd,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String sex,
                               @ModelAttribute(SessionTokens.ADMIN_TOKEN) JSONObject admin) {
        JSONObject res = new JSONObject();
        if (!admin.getBooleanValue("accountmanage")) {
            res.put("success", false);
            res.put("msg", "您没有用户管理权限");
            return res;
        }
        return userAccountStorage.editUser(id, pwd, nick, email, sex, null);
    }

    @RequestMapping(value = "/user/{id}/ban", method = RequestMethod.POST)
    public JSONObject banUser(@PathVariable int id, @RequestParam int banDays,
                              @ModelAttribute(SessionTokens.ADMIN_TOKEN) JSONObject admin) {
        JSONObject res = new JSONObject();
        if (!admin.getBooleanValue("accountmanage")) {
            res.put("success", false);
            res.put("msg", "您没有权限执行此操作");
            return res;
        }
        return userAccountStorage.doBanUser(id, banDays);
    }
}
