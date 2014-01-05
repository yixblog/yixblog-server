package cn.yixblog.dao.mappers;

import cn.yixblog.dao.beans.ImageBean;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Yixian
 * Date: 13-9-6
 * Time: 下午10:22
 */
public interface ImageMapper {
    public List<ImageBean> listUserImages(int userId, RowBounds pageParam);

    public void saveImage(ImageBean image);

    public void clearUnusedImage();

    public void deleteImage(int id);

    public int getUserImageCount(int userId);

    public ImageBean findOneImage(int imageId);
}
