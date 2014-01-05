package cn.yixblog.controller.user;

import cn.yixblog.controller.SessionTokens;
import cn.yixblog.core.article.IArticleStorage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: yixian
 * Date: 13-6-27
 * Time: 下午8:41
 * <p/>
 * controller dealing article add,update request,require user login
 */
@RestController
@RequestMapping("/user")
@SessionAttributes(SessionTokens.USER_TOKEN)
public class ArticleEditController {
    @Resource(name = "articleStorage")
    private IArticleStorage articleStorage;

    @RequestMapping(value = "/article", method = RequestMethod.POST)
    public JSONObject addArticle(@RequestParam String title, @RequestParam String content, @RequestParam String tags, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        int userId = user.getIntValue("id");
        String[] tagArray = tags.split(",");
        for (int i = 0, len = tagArray.length; i < len; i++) {
            tagArray[i] = tagArray[i].trim();
        }
        return articleStorage.saveArticle(userId, title, content, false, tagArray);
    }

    @RequestMapping(value = "/article/{id}", method = RequestMethod.PUT)
    public JSONObject editArticle(@PathVariable int id, @RequestParam String title, @RequestParam String content, @RequestParam String tags, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        int userId = user.getIntValue("id");
        String[] tagArray = tags.split(",");
        return articleStorage.editArticle(userId, id, title, content, tagArray);
    }

    @RequestMapping(value = "/article/{id}", method = RequestMethod.DELETE)
    public JSONObject deleteArticle(@PathVariable int id, @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        int userId = user.getIntValue("id");
        return articleStorage.deleteArticle(userId, id);
    }

    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    public JSONObject querySelfArticles(@RequestParam(required = false) String tag,
                                        @RequestParam(required = false, defaultValue = "1") int page,
                                        @RequestParam(required = false, defaultValue = "15") int pageSize,
                                        @ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user) {
        int userId = user.getIntValue("id");
        if ("".equals(tag)) {
            tag = null;
        }
        JSONObject res = articleStorage.queryArticles(page, pageSize, null, null, userId, tag, null, null);
        res.put("tag", tag);
        return res;
    }

}
