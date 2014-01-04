package cn.yixblog.dao;

import cn.yixblog.dao.beans.ArticleBean;
import cn.yixblog.dao.beans.TagCountBean;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-5-26
 * Time: 上午12:40
 */
public interface IArticleDAO {
    /**
     * list articles
     *
     * @param params   param map,available keys:title,addtimeBegin,addtimeEnd
     * @param pageArgs page args
     * @return list of ArticlesBean
     */
    public List<ArticleBean> listNewArticles(JSONObject params, int... pageArgs);

    public List<ArticleBean> listHotArticles(JSONObject params, int... pageArgs);

    public List<String> getArticleTags(int articleId);

    public List<String> getUserTags(int userId);

    public int countArticles(JSONObject params);

    public List<TagCountBean> listTags(int topNumber);

    public ArticleBean getArticle(int id);

    public List<ArticleBean> listArticlesByTag(String tag, int... pageArgs);

    public int countArticlesByTag(String tag);

    public List<ArticleBean> listArticlesByAccount(int accountId, int... pageArgs);

    public int countArticlesByAccount(int accountId);

    public void saveArticle(ArticleBean article);

    public void saveTag(ArticleBean article);

    public void update(ArticleBean article);

    public void delete(ArticleBean article);

    public void clearArticleTags(ArticleBean article);
}