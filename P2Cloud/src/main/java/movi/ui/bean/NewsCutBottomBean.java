package movi.ui.bean;

import java.io.Serializable;

/**
 * Created by chjj on 2016/6/26.
 */
public class NewsCutBottomBean implements Serializable {
    private String id;
    private String playlist_id;
    private String shot_id;
    private String in_position;
    private String in_position_time_code;
    private String out_position;
    private String out_position_time_code;
    private String duration_time_code;
    private String creation_date;
    private String item_order;
    private String thumbnail_src;
    private String created;
    private String updated;
    private String mp4url;
    private String duration;
    private String start_timecode;
    private String item_id;
    private String media_id;
    private String author_id;
    private String  IsNet="1";


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    //新添加选中的第几个
    private  boolean  isChecked=false;

    public String getIsNet() {
        return IsNet;
    }

    public void setIsNet(String isNet) {
        IsNet = isNet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }

    public String getShot_id() {
        return shot_id;
    }

    public void setShot_id(String shot_id) {
        this.shot_id = shot_id;
    }

    public String getIn_position() {
        return in_position;
    }

    public void setIn_position(String in_position) {
        this.in_position = in_position;
    }

    public String getIn_position_time_code() {
        return in_position_time_code;
    }

    public void setIn_position_time_code(String in_position_time_code) {
        this.in_position_time_code = in_position_time_code;
    }

    public String getOut_position() {
        return out_position;
    }

    public void setOut_position(String out_position) {
        this.out_position = out_position;
    }

    public String getOut_position_time_code() {
        return out_position_time_code;
    }

    public void setOut_position_time_code(String out_position_time_code) {
        this.out_position_time_code = out_position_time_code;
    }

    public String getDuration_time_code() {
        return duration_time_code;
    }

    public void setDuration_time_code(String duration_time_code) {
        this.duration_time_code = duration_time_code;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public String getItem_order() {
        return item_order;
    }

    public void setItem_order(String item_order) {
        this.item_order = item_order;
    }

    public String getThumbnail_src() {
        return thumbnail_src;
    }

    public void setThumbnail_src(String thumbnail_src) {
        this.thumbnail_src = thumbnail_src;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getMp4url() {
        return mp4url;
    }

    public void setMp4url(String mp4url) {
        this.mp4url = mp4url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStart_timecode() {
        return start_timecode;
    }

    public void setStart_timecode(String start_timecode) {
        this.start_timecode = start_timecode;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
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
}