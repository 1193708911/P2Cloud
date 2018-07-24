package movi.ui.db;



import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by chjj on 2016/8/4.
 */
@Table(name = "VideoInfo")
public class VideoInfo implements Serializable {
    //数据库  id
    @Column(name = "id", isId = true, autoGen = true)
    private int id;
    //用户名
    @Column(name = "userName")
    private String userName;

    //视频的标题
    @Column(name = "title")
    private String title;
    //视频的时长
    @Column(name = "videoFileLeanth")
    private int videoFileLeanth;
    //视频的状态
    @Column(name = "status")
    private String status;
    //视频缩略图的路径
    @Column(name = "picPath")
    private String picPath;
    //视频的上传进度
    @Column(name = "progress")
    private int progress;
    //视频保存的路径
    @Column(name = "videoPath")
    private String videoPath;
    //图片的路径
    @Column(name = "photoPath")
    private String photoPath;
    //xml文件路径
    @Column(name = "xmlFilePath")
    private String xmlFilePath;
    /**
     * 上载类型
     */
    @Column(name = "uploadType")
    private boolean uploadType;
    //时长
    @Column(name="timeLeanth")
    private String timeLeanth;

    //当前名字的截取时间
    @Column(name="currentTime")
    private  String currentTime;
    //图片文件时间搓
    @Column(name="fileIconXmlName")
    private  String fileIconXmlName;
    //是否上载完成
    @Column(name="isUploading")
    private boolean  isUploading;
    //上载按钮文字
    @Column(name="txtUploadText")
    private String  txtUploadText;

    public String getCompressFileName() {
        return compressFileName;
    }

    public void setCompressFileName(String compressFileName) {
        this.compressFileName = compressFileName;
    }

    //记录压缩过后的视频文件
    @Column(name = "txtCompressFileName")
    private String  compressFileName;
    public String getTxtUploadText() {
        return txtUploadText;
    }

    public void setTxtUploadText(String txtUploadText) {
        this.txtUploadText = txtUploadText;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setIsUploading(boolean isUploading) {
        this.isUploading = isUploading;
    }

    public String getFileIconXmlName() {
        return fileIconXmlName;
    }

    public void setFileIconXmlName(String fileIconXmlName) {
        this.fileIconXmlName = fileIconXmlName;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getTimeLeanth() {
        return timeLeanth;
    }

    public void setTimeLeanth(String timeLeanth) {
        this.timeLeanth = timeLeanth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVideoFileLeanth() {
        return videoFileLeanth;
    }

    public void setVideoFileLeanth(int videoFileLeanth) {
        this.videoFileLeanth = videoFileLeanth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public boolean isUploadType() {
        return uploadType;
    }

    public void setUploadType(boolean uploadType) {
        this.uploadType = uploadType;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
