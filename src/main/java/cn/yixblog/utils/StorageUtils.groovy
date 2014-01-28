package cn.yixblog.utils

import cn.yixblog.tools.resetCode.bean.ResetCode
import cn.yixblog.utils.timertask.ClearTaskBean
import com.alibaba.fastjson.JSONObject
import org.apache.ibatis.session.RowBounds

/**
 * Created by Yixian on 14-1-24.
 */
class StorageUtils {
    static JSONObject setPageInfo(int totalCount, int page, int pageSize) {
        return [totalcount: totalCount, totalpage: calculateTotalPage(totalCount, pageSize), pagesize: pageSize, page: page];
    }

    private static int calculateTotalPage(int totalCount, int pageSize) {
        return (totalCount + pageSize - 1) / pageSize;
    }

    static  RowBounds getRowBounds(int page,int pageSize){
        return new RowBounds((page-1)*pageSize,pageSize);
    }

    static int transferResetCodeType(int type) {
        switch (type) {
            case ClearTaskBean.TPPE_CLEAR_ADMIN_RESETPWD:
            case ClearTaskBean.TYPE_CLEAR_USER_RESETPWD:
                return ResetCode.TYPE_RESET_PWD;
            case ClearTaskBean.TYPE_CLEAR_ADMIN_CONFIRMEMAIL:
            case ClearTaskBean.TYPE_CLEAR_USER_CONFIRMEMAIL:
                return ResetCode.TYPE_CONFIRM_EMAIL;
            case ClearTaskBean.TYPE_CLEAR_ADMIN_RESETEMAIL:
            case ClearTaskBean.TYPE_CLEAR_USER_RESETEMAIL:
                return ResetCode.TYPE_RESET_EMAIL;
            default:
                return -1;
        }
    }
}
