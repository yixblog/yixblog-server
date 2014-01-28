package cn.yixblog.dao.mappers;

import cn.yixblog.dao.beans.TimerTaskBean;

/**
 * dao for timer task
 * Created by Yixian on 14-1-28.
 */
public interface TimerTaskMapper {

    public TimerTaskBean getTask(String code);

    public void clearOvertimes();

    public void deleteTask(String code);

    public void addTask(TimerTaskBean task);
}
