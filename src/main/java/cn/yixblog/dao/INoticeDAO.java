package cn.yixblog.dao;

import cn.yixblog.dao.beans.NoticeBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-6-18
 * Time: 下午9:33
 */
public interface INoticeDAO {
    public List<NoticeBean> list(int... pageArgs);

    public int count();

    public NoticeBean queryById(int id);

    public void save(NoticeBean bean);

    public void update(NoticeBean bean);

    public void delete(int id);
}