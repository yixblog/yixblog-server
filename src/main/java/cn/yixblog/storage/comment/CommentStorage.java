package cn.yixblog.storage.comment;

import cn.yixblog.core.comment.ICommentStorage;
import cn.yixblog.dao.IAccountDAO;
import cn.yixblog.dao.IArticleDAO;
import cn.yixblog.dao.ICommentDAO;
import cn.yixblog.dao.beans.AccountBean;
import cn.yixblog.dao.beans.ArticleBean;
import cn.yixblog.dao.beans.CommentBean;
import cn.yixblog.storage.AbstractStorage;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yixian
 * Date: 13-6-26
 * Time: 下午1:21
 */
@Repository("commentStorage")
public class CommentStorage extends AbstractStorage implements ICommentStorage {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public JSONObject queryArticleComments(int articleId, int page, int pageSize) {
        JSONObject res = new JSONObject();
        ICommentDAO commentMapper = getMapper(ICommentDAO.class);
        int totalCount = commentMapper.countCommentsByArticle(articleId);
        List<CommentBean> comments = commentMapper.listCommentsByArticle(articleId, getRowBounds(page, pageSize));
        setResult(res, true, "查询成功");
        setPageInfo(res, totalCount, page, pageSize);
        res.put("comments", comments);
        return res;
    }

    @Override
    public JSONObject queryUserComments(int userId, int page, int pageSize) {
        JSONObject res = new JSONObject();
        ICommentDAO commentMapper = getMapper(ICommentDAO.class);
        int totalCount = commentMapper.countCommentsByAccount(userId);
        List<CommentBean> comments = commentMapper.listCommentsByAccount(userId, getRowBounds(page, pageSize));
        setResult(res, true, "查询成功");
        setPageInfo(res, totalCount, page, pageSize);
        res.put("comments", comments);
        return res;
    }

    @Override
    public JSONObject queryCommentsToUser(int id, int page, int pageSize) {
        ICommentDAO commentMapper = getMapper(ICommentDAO.class);
        List<CommentBean> comments = commentMapper.listCommentsToAccount(id, getRowBounds(page, pageSize));
        int totalNumber = commentMapper.countCommentsToAccount(id);
        JSONObject res = new JSONObject();
        res.put("msg", "查询成功");
        res.put("success", true);
        res.put("comments", comments);
        setPageInfo(res, totalNumber, page, pageSize);
        return res;
    }

    @Override
    public JSONObject getOneComment(int commentId) {
        ICommentDAO commentMapper = getMapper(ICommentDAO.class);
        CommentBean bean = commentMapper.getComment(commentId);
        JSONObject res = new JSONObject();
        if (bean != null) {
            setResult(res, true, "查询成功");
            res.put("comment", bean);
        } else {
            setResult(res, false, "未找到对应评论，可能已被删除");
        }
        return res;
    }

    @Override
    @Transactional
    public JSONObject saveComment(int userId, int articleId, String title, String content) {
        JSONObject res = new JSONObject();
        IAccountDAO accountMapper = getMapper(IAccountDAO.class);
        AccountBean user = accountMapper.getAccountById(userId);
        if (user == null) {
            setResult(res, false, "不存在的用户");
            return res;
        }
        IArticleDAO articleMapper = getMapper(IArticleDAO.class);
        ArticleBean article = articleMapper.getArticle(articleId);
        if (article == null) {
            setResult(res, false, "不存在的文章");
            return res;
        }
        CommentBean comment = new CommentBean();
        comment.setAddTime(System.currentTimeMillis());
        comment.setAuthor(user);
        comment.setArticle(article);
        comment.setContent(content);
        comment.setTitle(title);
        ICommentDAO commentMapper = getMapper(ICommentDAO.class);
        commentMapper.save(comment);
        setResult(res, true, "评论添加成功");
        return res;
    }

    @Override
    @Transactional
    public JSONObject deleteComment(int commentId) {
        JSONObject res = new JSONObject();
        ICommentDAO commentMapper = getMapper(ICommentDAO.class);
        CommentBean comment = commentMapper.getComment(commentId);
        if (comment != null) {
            commentMapper.delete(comment);
        }
        logger.info("delete comment not found:" + commentId);
        setResult(res, true, "删除成功");
        return res;
    }

}