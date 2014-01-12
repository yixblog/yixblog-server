package cn.yixblog.controller.article;

import cn.yixblog.core.article.IArticleStorage;
import cn.yixblog.core.comment.ICommentStorage;
import cn.yixblog.utils.DateUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-6-21
 * Time: 下午5:27
 */
@RestController
@RequestMapping("/art")
public class ArticleController {

    @Resource(name = "articleStorage")
    public IArticleStorage articleStorage;

    @Resource(name = "commentStorage")
    private ICommentStorage commentStorage;

    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    public JSONObject listArticlesByJSON(@RequestParam(required = false) String keywords, @RequestParam(required = false, defaultValue = "") String startDate,
                                         @RequestParam(required = false, defaultValue = "") String endDate, @RequestParam(required = false, defaultValue = "1") int page,
                                         @RequestParam(required = false, defaultValue = "15") int pageSize, @RequestParam(required = false, defaultValue = "addtime") String sortkey,
                                         @RequestParam(required = false, defaultValue = "0") int userId, @RequestParam(required = false) String tag) {
        if ("".equals(tag)) {
            tag = null;
        }
        return articleStorage.queryArticles(page, pageSize, DateUtils.parseDate(startDate, DateUtils.DATE_FORMAT), DateUtils.parseDate(endDate, DateUtils.DATE_FORMAT), userId, tag, keywords.split(" "), sortkey);
    }

    @RequestMapping(value = "/articles/new",method = RequestMethod.GET)
    public JSONObject listNewArticles(@RequestParam(required = false,defaultValue = "10") int count){
        return articleStorage.queryArticles(1,count,null,null,0,null,null,"addtime");
    }

    @RequestMapping(value = "/articles/hot",method = RequestMethod.GET)
    public JSONObject listHotArticles(@RequestParam(required = false,defaultValue = "10") int count){
        return articleStorage.queryArticles(1,count,null,null,0,null,null,"replycount");
    }

    @RequestMapping(value = "/article/{articleId}", method = RequestMethod.GET)
    public JSONObject showArticle(@PathVariable int articleId) {
        return articleStorage.queryArticle(articleId);
    }

    @RequestMapping("/tags")
    public JSONObject getTopTags(@RequestParam(required = false, defaultValue = "10") int topnumber) {
        return articleStorage.queryTags(topnumber);
    }

    @RequestMapping(value = "/hotusers", method = RequestMethod.GET)
    public JSONObject listHotUsers(@RequestParam(required = false, defaultValue = "10") int topnumber) {
        return articleStorage.queryArticleAuthors(topnumber);
    }

    @RequestMapping(value = "/article/{articleId}/comments", method = RequestMethod.GET)
    public JSONObject listArticleComments(@PathVariable int articleId, @RequestParam(defaultValue = "1", required = false) int page, @RequestParam(defaultValue = "20", required = false) int pageSize) {
        return commentStorage.queryArticleComments(articleId, page, pageSize);
    }
}
