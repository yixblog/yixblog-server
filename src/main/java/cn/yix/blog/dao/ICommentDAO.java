package cn.yix.blog.dao;

import cn.yix.blog.dao.beans.CommentBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-5-26
 * Time: 上午12:50
 */
public interface ICommentDAO {

    public List<CommentBean> listCommentsByArticle(int articleId, int... pageArgs);

    public int countCommentsByArticle(int articleId);

    public List<CommentBean> listCommentsByAccount(int accountId, int... pageArgs);

    public int countCommentsByAccount(int accountId);

    public void save(CommentBean comment);

    public void delete(CommentBean comment);

    public CommentBean getComment(int commentId);

    public List<CommentBean> listCommentsToAccount(int id, int... rowBounds);

    public int countCommentsToAccount(int id);
}
