package cn.yixblog.controller.sysadmin;

import cn.yixblog.controller.SessionTokens;
import com.alibaba.fastjson.JSONObject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-7-8
 * Time: 下午11:37
 */
@RestController
@RequestMapping("/sys")
@SessionAttributes(SessionTokens.ADMIN_TOKEN)
public class SystemController {
    @RequestMapping("/index.htm")
    public String getAdminIndex(Model model,@ModelAttribute(SessionTokens.ADMIN_TOKEN)JSONObject admin){
        model.addAttribute(SessionTokens.ADMIN_TOKEN,admin);
        List<JSONObject> permissions = new ArrayList<>();
        model.addAttribute("permissions",permissions);
        return "admin_index";
    }
}
