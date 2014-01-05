package cn.yixblog.controller.user;

import cn.yixblog.controller.SessionTokens;
import cn.yixblog.core.file.IImageListStorage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: Yixian
 * Date: 13-11-9
 * Time: 下午11:19
 */
@RestController
@RequestMapping("/user")
@SessionAttributes(SessionTokens.USER_TOKEN)
public class UserImageController {
    @Resource(name = "imageListStorage")
    private IImageListStorage imageListStorage;

    @RequestMapping(value = "/images", method = RequestMethod.GET)
    public JSONObject getUserImages(@RequestParam(defaultValue = "0", required = false) int page,
                                    @RequestParam(defaultValue = "20", required = false) int pageSize,
                                    @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        return imageListStorage.listUserImages(page, pageSize, user.getIntValue("id"));
    }

    @RequestMapping(value = "/image/{id}", method = RequestMethod.DELETE)
    public JSONObject deleteImage(@PathVariable int id, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        return imageListStorage.deleteUserImage(id, user.getIntValue("id"));
    }
}
