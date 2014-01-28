package cn.yixblog.businesses.files.web;

import cn.yixblog.businesses.files.core.IFileSavingStorage;
import cn.yixblog.businesses.files.core.SavingResultInfo;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: Yixian
 * Date: 13-8-31
 * Time: 上午11:52
 */
@RestController
@RequestMapping("/uploader")
public class FileUploadController {
    private Logger logger = Logger.getLogger(getClass());
    @Resource(name = "fileSavingStorage")
    private IFileSavingStorage fileSavingStorage;

    @RequestMapping(value = "/image", method = RequestMethod.POST)
    public JSONObject uploadImage(@RequestParam MultipartFile upfile, @RequestParam String pictitle) throws Exception {
        String[] fileType = {".gif", ".png", ".jpg", ".jpeg"};
        int maxSize = 10000;
        SavingResultInfo up = fileSavingStorage.doSaveFile(upfile, pictitle, fileType, maxSize, user.getIntValue("id"));
        logger.debug("image uploaded,file size:" + up.getSize() + ",url:" + up.getUrl());
        return buildUploadResult(up);
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public JSONObject uploadFile(@RequestParam MultipartFile upfile, @RequestParam(required = false) String pictitle) {
        String[] fileType = {".rar", ".doc", ".docx", ".ppt", ".pptx", ".zip", ".pdf", ".txt", ".xls", ".xlsx", ".jar"};  //允许的文件类型
        int maxSize = 10000;
        SavingResultInfo up = fileSavingStorage.doSaveFile(upfile, pictitle, fileType, maxSize, user.getIntValue("id"));
        logger.debug("file uploaded,file size:" + up.getSize() + ",url:" + up.getUrl());
        return buildUploadResult(up);
    }

    @RequestMapping(value = "/scrawl", method = RequestMethod.POST, params = "action=tmpImg")
    public String uploadScrawl(@RequestParam(required = false) String pictitle, @RequestParam(required = false) MultipartFile upfile) {
        String[] fileType = {".gif", ".png", ".jpg", ".jpeg"};
        int maxSize = 10000;
        SavingResultInfo up = fileSavingStorage.doSaveFile(upfile, pictitle, fileType, maxSize, user.getIntValue("id"));
        return "<script>parent.ue_callback('" + up.getUrl() + "','" + up.getState() + "')</script>";
    }

    @RequestMapping(value = "/scrawl", method = RequestMethod.POST, params = "content")
    public JSONObject uploadScrawl2(@RequestParam String content) {
        String[] fileType = {".gif", ".png", ".jpg", ".jpeg"};
        int maxSize = 10000;
        SavingResultInfo up = fileSavingStorage.doSaveBase64(content, fileType, maxSize, user.getIntValue("id"));
        return buildUploadResult(up);
    }

    @RequestMapping(value = "/remote", method = RequestMethod.POST)
    public JSONObject saveRemoteImage(@RequestParam String upfile) {
        String[] fileType = {".gif", ".png", ".jpg", ".jpeg"};
        int maxSize = 10000;
        SavingResultInfo up = fileSavingStorage.doSaveRemoteImage(upfile, fileType, maxSize, user.getIntValue("id"));
        return buildUploadResult(up);
    }

    private JSONObject buildUploadResult(SavingResultInfo up) {
        JSONObject res = new JSONObject();
        res.put("original", up.getOriginalName());
        res.put("url", up.getUrl());
        res.put("title", up.getTitle());
        res.put("state", up.getState());
        return res;
    }

}
