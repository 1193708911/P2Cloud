package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import movi.base.Response;

/**
 * 合成列表发布
 */
public class NewsPicListBean1 extends Response implements Serializable {
    private ArrayList<NewsPicBean> data;

    public ArrayList<NewsPicBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewsPicBean> data) {
        this.data = data;
    }

    public class NewsPicBean implements Serializable {
        private String duration;
        private String mp4;
        private String photo;
        private String num;
        private String clip_name;
        private String clip_status_end;
        private ArrayList clip_status;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getMp4() {
            return mp4;
        }

        public void setMp4(String mp4) {
            this.mp4 = mp4;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getClip_name() {
            return clip_name;
        }

        public void setClip_name(String clip_name) {
            this.clip_name = clip_name;
        }

        public String getClip_status_end() {
            return clip_status_end;
        }

        public void setClip_status_end(String clip_status_end) {
            this.clip_status_end = clip_status_end;
        }

        public ArrayList getClip_status() {
            return clip_status;
        }

        public void setClip_status(ArrayList clip_status) {
            this.clip_status = clip_status;
        }
    }

}
