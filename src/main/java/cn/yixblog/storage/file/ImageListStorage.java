package cn.yixblog.storage.file;

import cn.yixblog.core.file.IImageListStorage;
import cn.yixblog.dao.IImageDAO;
import cn.yixblog.dao.beans.ImageBean;
import cn.yixblog.storage.AbstractStorage;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yixian
 * Date: 13-9-6
 * Time: 下午4:52
 */
@Repository("imageListStorage")
public class ImageListStorage extends AbstractStorage implements IImageListStorage {

    private String webRoot;
    private Logger logger = Logger.getLogger(getClass());

    public ImageListStorage() {
        webRoot = System.getProperty("web.root");
    }


    @Override
    public List<String> listAllImages(int userid) {
        IImageDAO mapper = getMapper(IImageDAO.class);
        List<ImageBean> images = mapper.listUserImages(userid);
        List<String> urls = new ArrayList<>();
        for (ImageBean image : images) {
            urls.add(image.getUrl());
        }
        return urls;
    }

    @Override
    public JSONObject listUserImages(int page, int pageSize, int userId) {
        IImageDAO mapper = getMapper(IImageDAO.class);
        int imageCount = mapper.getUserImageCount(userId);
        List<ImageBean> images = mapper.listUserImages(userId, getRowBounds(page, pageSize));
        JSONObject res = new JSONObject();
        setResult(res, true, "查询成功");
        setPageInfo(res, imageCount, page, pageSize);
        res.put("images", images);
        return res;
    }

    @Override
    public JSONObject deleteUserImage(int imageId, int userId) {
        IImageDAO mapper = getMapper(IImageDAO.class);
        ImageBean img = mapper.findOneImage(imageId);
        JSONObject res = new JSONObject();
        if (img.getUser().getId() != userId) {
            setResult(res, false, "您无权删除别人的图片");
            return res;
        }
        String imageRealPath = webRoot + img.getUrl();
        logger.debug("image real path to delete:" + imageRealPath);

        File imageFile = new File(imageRealPath);
        if (imageFile.exists() && !FileUtils.deleteQuietly(imageFile)) {
            setResult(res, false, "删除失败");
            return res;
        }
        mapper.deleteImage(imageId);
        setResult(res, true, "删除成功");
        return res;
    }


}