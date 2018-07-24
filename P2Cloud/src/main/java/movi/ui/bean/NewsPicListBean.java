package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import movi.base.Response;

/**
 * 合成列表发布
 */
public class NewsPicListBean extends Response implements Serializable {

    private ArrayList<NewsPicBean> data;

    public ArrayList<NewsPicBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewsPicBean> data) {
        this.data = data;
    }

    public static class NewsPicBean implements  Serializable{
        /**
         * userid : 70015
         * shot_id : 607
         * createtime : 2017-04-06 05:59:17
         * clip_name : 1491458176968
         * mp4 : hdata/ctvit001/P2PROXY/CONTENTS/PROXY/1491458176968.mp4
         * duration : 00:00:15:15
         * creator : ctvit001
         * photo : http://123.56.6.125/data/ctvit001/P2PROXY/CONTENTS/PREVIEW/1491458176968.jpg
         * clip_status : ["素材","处理","成功"]
         */

        private String userid;
        private String shot_id;
        private String createtime;
        private String clip_name;
        private ArrayList mp4;
        private String duration;
        private String creator;
        private String photo;
        private ArrayList clip_status;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getShot_id() {
            return shot_id;
        }

        public void setShot_id(String shot_id) {
            this.shot_id = shot_id;
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

        public ArrayList getMp4() {
            return mp4;
        }

        public void setMp4(ArrayList mp4) {
            this.mp4 = mp4;
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

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public ArrayList getClip_status() {
            return clip_status;
        }

        public void setClip_status(ArrayList clip_status) {
            this.clip_status = clip_status;
        }
    }
}
