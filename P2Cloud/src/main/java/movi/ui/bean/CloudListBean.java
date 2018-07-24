package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import movi.base.Response;

/**
 * Created by chjj on 2016/5/12.
 */
public class CloudListBean extends Response implements Serializable{
    public ArrayList<CloudBean> getData() {
        return data;
    }

    public void setData(ArrayList<CloudBean> data) {
        this.data = data;
    }

    public ArrayList<CloudBean>  data;

    public  class CloudBean implements  Serializable{

       private String clip_name;
        private String   gps_source;
        private String   media_id;
        private String   author_id;
        private String   user_clip_name;
        private String   start_timecode;
        private String    creation_date;
        private String    shot_id;
        private String   id;
        private String   codec;
        private String   duration;
        private String   frame_rate;
        private String    drop_frame_flag;
        private String    aspect_ratio;
        private String    created;
        private String    model_name;
        private String    manufacturer;
        private String    shot_mark;
        private String   namespace;
        private String     take_number;
        private String    uid;
        private String     jpg_url;
        private String      mp4_url;
        private String  program_name;

        public String getProgram_name() {
            return program_name;
        }

        public void setProgram_name(String program_name) {
            this.program_name = program_name;
        }

        public String getClip_name() {
            return clip_name;
        }

        public void setClip_name(String clip_name) {
            this.clip_name = clip_name;
        }

        public String getGps_source() {
            return gps_source;
        }

        public void setGps_source(String gps_source) {
            this.gps_source = gps_source;
        }

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getUser_clip_name() {
            return user_clip_name;
        }

        public void setUser_clip_name(String user_clip_name) {
            this.user_clip_name = user_clip_name;
        }

        public String getStart_timecode() {
            return start_timecode;
        }

        public void setStart_timecode(String start_timecode) {
            this.start_timecode = start_timecode;
        }

        public String getCreation_date() {
            return creation_date;
        }

        public void setCreation_date(String creation_date) {
            this.creation_date = creation_date;
        }

        public String getShot_id() {
            return shot_id;
        }

        public void setShot_id(String shot_id) {
            this.shot_id = shot_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCodec() {
            return codec;
        }

        public void setCodec(String codec) {
            this.codec = codec;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFrame_rate() {
            return frame_rate;
        }

        public void setFrame_rate(String frame_rate) {
            this.frame_rate = frame_rate;
        }

        public String getDrop_frame_flag() {
            return drop_frame_flag;
        }

        public void setDrop_frame_flag(String drop_frame_flag) {
            this.drop_frame_flag = drop_frame_flag;
        }

        public String getAspect_ratio() {
            return aspect_ratio;
        }

        public void setAspect_ratio(String aspect_ratio) {
            this.aspect_ratio = aspect_ratio;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getModel_name() {
            return model_name;
        }

        public void setModel_name(String model_name) {
            this.model_name = model_name;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getShot_mark() {
            return shot_mark;
        }

        public void setShot_mark(String shot_mark) {
            this.shot_mark = shot_mark;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getTake_number() {
            return take_number;
        }

        public void setTake_number(String take_number) {
            this.take_number = take_number;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getJpg_url() {
            return jpg_url;
        }

        public void setJpg_url(String jpg_url) {
            this.jpg_url = jpg_url;
        }

        public String getMp4_url() {
            return mp4_url;
        }

        public void setMp4_url(String mp4_url) {
            this.mp4_url = mp4_url;
        }
    }
}
