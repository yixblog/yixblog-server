package cn.yixblog.controller.user;

import cn.yixblog.core.user.IUserAccountStorage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: Yixian
 * Date: 13-10-7
 * Time: 下午5:02
 */
@RestController
@RequestMapping("/user")
public class UserInfoController {
    @Resource(name = "userAccountStorage")
    private IUserAccountStorage accountStorage;

    @RequestMapping("/{userId}")
    public JSONObject centerPage(@PathVariable int userId) {
        return accountStorage.queryUser(userId);
    }
}
