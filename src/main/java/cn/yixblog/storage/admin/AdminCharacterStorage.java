package cn.yixblog.storage.admin;

import cn.yixblog.core.admin.IAdminCharacterStorage;
import cn.yixblog.dao.IAdminDAO;
import cn.yixblog.dao.ICharacterDAO;
import cn.yixblog.dao.beans.AdminBean;
import cn.yixblog.dao.beans.CharacterBean;
import cn.yixblog.storage.AbstractStorage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yxdave
 * Date: 13-6-4
 * Time: 下午12:37
 */
@Repository("adminCharacterStorage")
public class AdminCharacterStorage extends AbstractStorage implements IAdminCharacterStorage {


    @Override
    public List<JSONObject> queryAllCharacters() {
        ICharacterDAO characterMapper = getMapper(ICharacterDAO.class);
        List<CharacterBean> characters = characterMapper.listCharacters();
        return buildJSONList(characters);
    }

    private List<JSONObject> buildJSONList(List<CharacterBean> characters) {
        List<JSONObject> resultList = new ArrayList<>(characters.size());
        for (CharacterBean character : characters) {
            JSONObject json = (JSONObject) JSONObject.toJSON(character);
            resultList.add(json);
        }
        return resultList;
    }

    @Override
    @Transactional
    public JSONObject updateCharacter(int id, String name, int permissionMask) {
        ICharacterDAO characterMapper = getMapper(ICharacterDAO.class);
        JSONObject res = new JSONObject();
        CharacterBean characterBean = characterMapper.getCharacter(id);
        if (characterBean == null) {
            setResult(res, false, "不存在的角色");
            return res;
        }
        if (name != null) {
            characterBean.setName(name);
        }
        setPermission(characterBean, permissionMask);
        characterMapper.update(characterBean);
        setResult(res, true, "修改成功");
        return res;
    }

    /**
     * set character permission
     *
     * @param characterBean  character bean
     * @param permissionMask mask:binary:|adminManage|articleManage|commentManage|systemManage|userManage|
     */
    private void setPermission(CharacterBean characterBean, int permissionMask) {
        characterBean.setAdminManage((permissionMask & 0b10000) > 0);
        characterBean.setArticleManage((permissionMask & 0b1000) > 0);
        characterBean.setCommentManage((permissionMask & 0b100) > 0);
        characterBean.setSystemConfig((permissionMask & 0b10) > 0);
        characterBean.setUserManage((permissionMask & 0b1) > 0);
    }

    @Override
    @Transactional
    public JSONObject saveCharacter(String name, int permissionMask) {
        CharacterBean characterBean = new CharacterBean();
        ICharacterDAO characterMapper = getMapper(ICharacterDAO.class);
        JSONObject res = new JSONObject();
        if (characterMapper.checkNameExists(name) > 0) {
            setResult(res, false, "名称已存在");
            return res;
        }
        characterBean.setName(name);
        setPermission(characterBean, permissionMask);
        characterMapper.save(characterBean);
        setResult(res, true, "添加成功");
        return res;
    }

    @Override
    @Transactional
    public JSONObject deleteCharacter(int id) {
        ICharacterDAO characterMapper = getMapper(ICharacterDAO.class);
        JSONObject res = new JSONObject();
        CharacterBean character = characterMapper.getCharacter(id);
        if (character != null) {
            characterMapper.delete(character);
        }
        setResult(res, true, "删除成功");
        return res;
    }

    @Override
    public JSONObject queryCharacter(int id) {
        ICharacterDAO characterMapper = getMapper(ICharacterDAO.class);
        CharacterBean characterBean = characterMapper.getCharacter(id);
        JSONObject res = new JSONObject();
        if (characterBean == null) {
            setResult(res, false, "未找到对应角色");
            return res;
        }
        setResult(res, true, "查找成功");
        res.put("character", characterBean);
        return res;
    }

    @Override
    @Transactional
    public JSONObject doSetAdminCharacters(int adminId, int... characterIds) {
        ICharacterDAO characterMapper = getMapper(ICharacterDAO.class);
        IAdminDAO adminMapper = getMapper(IAdminDAO.class);
        AdminBean admin = adminMapper.getAdminById(adminId);
        JSONObject res = new JSONObject();
        if (admin == null) {
            setResult(res, false, "不存在的管理员账号");
            return res;
        }
        characterMapper.clearAdminCharacters(adminId);
        if (characterIds != null) {
            for (int cid : characterIds) {
                CharacterBean character = characterMapper.getCharacter(cid);
                if (character != null) {
                    characterMapper.setAdminCharacter(adminId, cid);
                }
            }
        }
        setResult(res, true, "操作成功");
        return res;
    }
}
