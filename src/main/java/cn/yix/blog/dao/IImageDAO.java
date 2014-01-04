package cn.yix.blog.dao;

import cn.yix.blog.dao.beans.ImageBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Yixian
 * Date: 13-9-6
 * Time: 下午10:22
 */
public interface IImageDAO {
    public List<ImageBean> listUserImages(int userId, int... pageParam);

    public void saveImage(ImageBean image);

    public void clearUnusedImage();

    public void deleteImage(int id);

    public int getUserImageCount(int userId);

    public ImageBean findOneImage(int imageId);
}
