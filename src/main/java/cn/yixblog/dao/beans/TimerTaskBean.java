package cn.yixblog.dao.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * timer task
 * Created by Yixian on 14-1-28.
 */
public class TimerTaskBean {
    public static final int TYPE_CLEAR_USER_RESETPWD=1;
    public static final int TPPE_CLEAR_ADMIN_RESETPWD=2;
    public static final int TYPE_CLEAR_USER_RESETEMAIL=3;
    public static final int TYPE_CLEAR_ADMIN_RESETEMAIL=4;
    public static final int TYPE_CLEAR_USER_CONFIRMEMAIL=5;
    public static final int TYPE_CLEAR_ADMIN_CONFIRMEMAIL=6;
    public static final int TYPE_CLEAR_USER_BAN = 7;

    private String code;
    private long addtime;
    private long overtime;
    private int type;
    private String data;//json formatted data

    public long getAddtime() {
        return addtime;
    }

    public void setAddtime(long addtime) {
        this.addtime = addtime;
    }

    public long getOvertime() {
        return overtime;
    }

    public void setOvertime(long overtime) {
        this.overtime = overtime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public JSONObject getDataObject(){
        return JSON.parseObject(data);
    }
}
