package cn.yixblog.controller.user;

import cn.yixblog.controller.SessionTokens;
import cn.yixblog.core.comment.ICommentStorage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-7-30
 * Time: 下午10:12
 */
@RestController
@RequestMapping("/user")
@SessionAttributes(SessionTokens.USER_TOKEN)
public class UserCommentsController {
    @Resource(name = "commentStorage")
    private ICommentStorage commentStorage;

    @RequestMapping(value = "/comments/from_me", method = RequestMethod.GET)
    public JSONObject getMyComments(@ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user, @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return commentStorage.queryUserComments(user.getIntValue("id"), page, pageSize);
    }

    @RequestMapping(value = "/comments/to_me", method = RequestMethod.GET)
    public JSONObject getCommentsToMe(@ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user, @RequestParam(required = false, defaultValue = "1") int page,
                                      @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return commentStorage.queryCommentsToUser(user.getIntValue("id"), page, pageSize);
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public JSONObject addComment(@ModelAttribute(SessionTokens.USER_TOKEN) JSONObject user, @RequestParam String content,
                                 @RequestParam(required = false, defaultValue = "") String title, @RequestParam int articleId) {
        return commentStorage.saveComment(user.getIntValue("id"), articleId, title, content);
    }

    @RequestMapping(value = "/comment/{commentId}", method = RequestMethod.GET)
    public JSONObject quoteComment(@PathVariable int commentId) {
        return commentStorage.getOneComment(commentId);
    }
}
