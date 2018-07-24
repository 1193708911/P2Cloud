package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import movi.base.Response;

/**
 * Created by admin on 2017/4/6.
 */

public class NewClipStatusBean extends Response implements Serializable {

    private ArrayList<NewClipBean> data;

    public ArrayList<NewClipBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewClipBean> data) {
        this.data = data;
    }
public class NewClipBean implements Serializable {


    private String photo;
    private String userid;
    private String createtime;
    private String clip_name;
    private String duration;
    private String creator;
    private ArrayList mp4;
    private ArrayList clip_status;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getClip_name() {
        return clip_name;
    }

    public void setClip_name(String clip_name) {
        this.clip_name = clip_name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public ArrayList getMp4() {
        return mp4;
    }

    public void setMp4(ArrayList mp4) {
        this.mp4 = mp4;
    }

    public ArrayList getClip_status() {
        return clip_status;
    }

    public void setClip_status(ArrayList clip_status) {
        this.clip_status = clip_status;
    }

}

}
