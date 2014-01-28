package cn.yixblog.businesses.accounts.admin.storage

import cn.yixblog.businesses.accounts.admin.core.IAdminAccountStorage
import cn.yixblog.dao.beans.AdminBean
import cn.yixblog.dao.mappers.AdminMapper
import cn.yixblog.services.VelocityMailTemplateSender
import cn.yixblog.tools.resetCode.ResetCodeFactory
import cn.yixblog.tools.resetCode.bean.ResetCode
import cn.yixblog.utils.YixStringUtils
import cn.yixblog.utils.timertask.ClearResetCodeTask
import cn.yixblog.utils.timertask.ClearTaskBean
import com.alibaba.fastjson.JSONObject
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.support.SqlSessionDaoSupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-5-26
 * Time: 下午5:46
 */
@Repository("adminAccountStorage")
class AdminAccountStorage extends SqlSessionDaoSupport implements IAdminAccountStorage {
    @Resource(name = "mailTemplate")
    private VelocityMailTemplateSender mailTemplate;
    @Resource(name = "resetCodeClearer")
    private ClearResetCodeTask clearResetCodeTask;

    @Override
    @Resource(name = "sessionFactory")
    void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    @Transactional
    JSONObject doLogin(String uid, String pwd) {
        uid = uid.toLowerCase();
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminByUid(uid);
        if (adminBean == null) {
            return [success: false, msg: '用户名不存在']
        }
        String pwdMd5 = YixStringUtils.createPwdMd5(uid, pwd);
        if (!adminBean.getPwd().equals(pwdMd5)) {
            return [success: false, msg: '密码错误']
        }
        updateLastLogin(adminMapper, adminBean);
        return [success: true, msg: '登陆成功', admin: adminBean]
    }

    private void updateLastLogin(AdminMapper adminMapper, AdminBean adminBean) {
        adminBean.setLastLogin(System.currentTimeMillis());
        adminMapper.update(adminBean);
    }

    @Override
    @Transactional
    JSONObject saveAdminAccount(String uid, String pwd, String email) {
        uid = uid.toLowerCase();
        email = email.toLowerCase();
        JSONObject res;
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        if ((res = checkUidExists(uid, email, adminMapper)) != null) {
            return res;
        }
        AdminBean adminBean = initAdminBean(uid, pwd, email);
        adminMapper.save(adminBean);

        res = [success: true, msg: "添加账号成功", admin: adminBean]

//        ClearTaskBean confirmTask = registerResetTask(adminBean, ClearTaskBean.TYPE_CLEAR_ADMIN_CONFIRMEMAIL, 24);
//        confirmTask.setAdmin(adminBean);
//        clearResetCodeTask.addTask(confirmTask);
        //send email
//        if (mailTemplate.sendConfirmEmail(adminBean, confirmTask.getResetCode().generateResetCode())) {
//            setResult(res, true, "操作成功，邮件已发送到:" + email);
//        } else {
//            setResult(res, false, "邮件发送失败，请稍后重试");
//            return res;
//        }
        return res;
    }

    private AdminBean initAdminBean(String uid, String pwd, String email) {
        AdminBean adminBean = new AdminBean();
        adminBean.setUid(uid);
        adminBean.setTempEmail(email);
        adminBean.setPwd(YixStringUtils.createPwdMd5(uid, pwd));
        adminBean.setRegTime(System.currentTimeMillis());
        return adminBean;
    }

    private JSONObject checkUidExists(String uid, String email, AdminMapper adminMapper) {
        if (adminMapper.getAdminByUid(uid) != null) {
            return [success: false, msg: "用户名已存在"];
        }
        if (checkEmailExists(email, adminMapper)) {
            return [success: false, msg: "该邮箱已被注册"];
        }
        return null;
    }

    @Override
    JSONObject queryForgetPasswordRequest(String uid, String email) {
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        uid = uid.toLowerCase();
        AdminBean adminBean = adminMapper.getAdminByUid(uid);
        if (adminBean == null) {
            return [success: false, msg: "用户名不存在"];
        }
        email = email.toLowerCase();
        if (!adminBean.getEmail().equals(email)) {
            return [success: false, msg: "用户名和邮箱不匹配"];
        }
        //add clear task
        ClearTaskBean task = registerResetTask(adminBean, ClearTaskBean.TPPE_CLEAR_ADMIN_RESETPWD, 3);
        task.setAdmin(adminBean);
        clearResetCodeTask.addTask(task);
        if (mailTemplate.sendResetPasswordEmail(adminBean, task.getResetCode().generateResetCode())) {
            return [success: true, msg: "密码重置邮件已发送至$email,请注意查收。邮件有效期为3小时"];
        } else {
            return [success: false, msg: "系统错误，邮件发送失败，请稍后重试"];
        }
    }

    private ClearTaskBean registerResetTask(AdminBean adminBean, int type, long delayHours) {
        ResetCode resetCode = ResetCodeFactory.generateResetCode(transferResetCodeType(type), adminBean.getUid());
        long timeDelay = delayHours * 3600 * 1000;
        return new ClearTaskBean(resetCode, resetCode.getCreateTime() + timeDelay, adminBean.getId(), type);
    }

    @Override
    @Transactional
    JSONObject doForceResetPassword(String resetCode, String pwd) {
        JSONObject res = new JSONObject();
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        ClearTaskBean task = clearResetCodeTask.queryTask(resetCode);
        if (task == null) {
            setResult(res, false, "不存在的申请，请检查申请是否已超时");
            return res;
        }
        int id = task.getAdmin().getId();
        AdminBean adminBean = adminMapper.getAdminById(id);
        if (adminBean == null) {
            setResult(res, false, "不存在的用户，可能已被删除");
            return res;
        }
        adminBean.setPwd(YixStringUtils.createPwdMd5(adminBean.getUid(), pwd));
        adminMapper.update(adminBean);
        setResult(res, true, "修改成功");
        clearResetCodeTask.clearTask(resetCode);
        return res;
    }

    @Override
    @Transactional
    JSONObject doChangePassword(int id, String oldPwd, String newPwd) {
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(id);
        JSONObject res = new JSONObject();
        if (adminBean == null) {
            setResult(res, false, "不存在的用户id");
            return res;
        }
        String oldPwdMd5 = YixStringUtils.createPwdMd5(adminBean.getUid(), oldPwd);
        if (!oldPwdMd5.equals(adminBean.getPwd())) {
            setResult(res, false, "旧密码不匹配");
            return res;
        }
        adminBean.setPwd(YixStringUtils.createPwdMd5(adminBean.getUid(), newPwd));
        adminMapper.update(adminBean);
        setResult(res, true, "密码修改成功");
        return res;
    }

    @Override
    JSONObject queryResetEmailRequest(int id) {
        JSONObject res = new JSONObject();
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(id);
        if (adminBean == null) {
            setResult(res, false, "不存在的用户");
            return res;
        }
        ClearTaskBean task = registerResetTask(adminBean, ClearTaskBean.TYPE_CLEAR_ADMIN_RESETEMAIL, 3);
        task.setAdmin(adminBean);
        clearResetCodeTask.addTask(task);
        if (!mailTemplate.sendResetEmailMail(adminBean, task.getResetCode().generateResetCode())) {
            setResult(res, false, "邮件发送失败，请稍后重试");
            return res;
        }
        setResult(res, true, "操作成功，邮件已发送至" + adminBean.getEmail());
        return res;
    }

    @Override
    @Transactional
    JSONObject doResetEmail(String resetCode, String email) {
        JSONObject res = new JSONObject();
        ClearTaskBean task = clearResetCodeTask.queryTask(resetCode);
        if (task == null) {
            setResult(res, false, "申请已失效，请重新申请或检查是否有更新的申请邮件");
            return res;
        }
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(task.getAdmin().getId());
        if (adminBean == null) {
            setResult(res, false, "不存在的用户，可能已被删除账号");
            return res;
        }
        if (checkEmailExists(email, adminMapper)) {
            setResult(res, false, "操作失败，此邮箱已被注册");
            return res;
        }
        adminBean.setTempEmail(email);
        adminMapper.update(adminBean);
        ClearTaskBean confirmTask = registerResetTask(adminBean, ClearTaskBean.TYPE_CLEAR_ADMIN_CONFIRMEMAIL, 24);
        confirmTask.setAdmin(adminBean);
        clearResetCodeTask.clearTask(resetCode);
        clearResetCodeTask.addTask(confirmTask);
        //send email
        if (mailTemplate.sendConfirmEmail(adminBean, confirmTask.getResetCode().generateResetCode())) {
            setResult(res, true, "操作成功，邮件已发送到:" + email);
        } else {
            setResult(res, false, "邮件发送失败，请稍后重试");
            return res;
        }
        return res;
    }

    private boolean checkEmailExists(String email, AdminMapper adminMapper) {
        return adminMapper.getAdminByEmail(email) != null;
    }

    @Override
    JSONObject queryResetCode(String resetCode) {
        ClearTaskBean task = clearResetCodeTask.queryTask(resetCode);
        JSONObject res = new JSONObject();
        if (task == null) {
            setResult(res, false, "申请已失效，请重新申请或检查是否有更新的申请邮件");
            return res;
        }
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(task.getAdmin().getId());
        if (adminBean == null) {
            setResult(res, false, "不存在的用户，可能已被删除账号");
            return res;
        }
        setResult(res, true, "操作成功");
        res.put("admin", adminBean);
        return res;
    }

    @Override
    @Transactional
    JSONObject deleteAdmin(int id) {
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(id);
        if (adminBean != null) {
            adminBean.setId(id);
            adminMapper.delete(adminBean);
            logger.info("删除管理员账号:" + adminBean.getUid());
        }
        JSONObject res = new JSONObject();
        setResult(res, true, "删除成功");
        return res;
    }

    @Override
    @Transactional
    JSONObject doEditAdmin(int id, String pwd, String email) {
        if (email != null) {
            email = email.toLowerCase();
        }
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean admin = adminMapper.getAdminById(id);
        JSONObject res = new JSONObject();
        if (admin == null) {
            setResult(res, false, "不存在的管理员账号");
            return res;
        }
        String pwdMd5 = YixStringUtils.createPwdMd5(admin.getUid(), pwd);
        admin.setPwd(pwdMd5);
        admin.setEmail(email);
        adminMapper.update(admin);
        setResult(res, true, "修改成功");
        return res;
    }

    @Override
    @Transactional
    JSONObject doConfirmEmail(String resetCode) {
        JSONObject res = new JSONObject();
        ClearTaskBean task = clearResetCodeTask.queryTask(resetCode);
        if (task == null) {
            setResult(res, false, "申请已失效，请重新申请或检查是否有更新的申请邮件");
            return res;
        }
        int id = task.getId();
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(id);
        if (adminBean == null) {
            setResult(res, false, "不存在的用户，可能已被删除账号");
            return res;
        }
        adminBean.setEmail(adminBean.getTempEmail());
        adminBean.setTempEmail(null);
        adminMapper.update(adminBean);
        clearResetCodeTask.clearTask(resetCode);
        setResult(res, true, "操作成功");
        return res;
    }

    @Override
    JSONObject queryAdminById(int id) {
        JSONObject res = new JSONObject();
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(id);
        res.put("admin", adminBean);
        return res.getJSONObject("admin");
    }

    @Override
    JSONObject queryAdminList(String uid, String email, int page, int pageSize) {
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        Map<String, Object> params = new HashMap<>();
        if (uid != null) {
            params.put("uid", "%" + uid + "%");
        }
        if (email != null) {
            params.put("email", email);
        }
        int totalCount = adminMapper.countAdmins(params);
        List<AdminBean> admins = adminMapper.listAdmins(params, getRowBounds(page, pageSize));
        JSONObject res = new JSONObject();
        setPageInfo(res, totalCount, page, pageSize);
        res.put("admins", admins);
        return res;
    }

    @Override
    @Transactional
    void doClearTempEmail(int id, String email) {
        AdminMapper adminMapper = getSqlSession().getMapper(AdminMapper.class);
        AdminBean adminBean = adminMapper.getAdminById(id);
        if (adminBean != null && email.equals(adminBean.getTempEmail())) {
            adminBean.setTempEmail(null);
            adminMapper.update(adminBean);
        }
    }

}
