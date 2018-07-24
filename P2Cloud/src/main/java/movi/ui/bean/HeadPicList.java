package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import movi.base.Response;

/**
 * 头部剪辑列表
 */
public class HeadPicList extends Response implements Serializable {
    private ArrayList<HeadPic> data;

    public ArrayList<HeadPic> getData() {
        return data;
    }

    public void setData(ArrayList<HeadPic> data) {
        this.data = data;
    }

    public class HeadPic {
        private String id;
        private String title;
        private String author_id;
        private String media_id;
        private String playlist_id;
        private String duration_time_code_all;
        private String duration_time_postion;
        private String typephoto;
        private String typewg;
        private String playliststatus;
        private String playlistsrc;

        private  boolean  isChecked;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor_id() {
            return author_id;
        }

        public void setAuthor_id(String author_id) {
            this.author_id = author_id;
        }

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        public String getPlaylist_id() {
            return playlist_id;
        }

        public void setPlaylist_id(String playlist_id) {
            this.playlist_id = playlist_id;
        }

        public String getDuration_time_code_all() {
            return duration_time_code_all;
        }

        public void setDuration_time_code_all(String duration_time_code_all) {
            this.duration_time_code_all = duration_time_code_all;
        }

        public String getDuration_time_postion() {
            return duration_time_postion;
        }

        public void setDuration_time_postion(String duration_time_postion) {
            this.duration_time_postion = duration_time_postion;
        }

        public String getTypephoto() {
            return typephoto;
        }

        public void setTypephoto(String typephoto) {
            this.typephoto = typephoto;
        }

        public String getTypewg() {
            return typewg;
        }

        public void setTypewg(String typewg) {
            this.typewg = typewg;
        }

        public String getPlayliststatus() {
            return playliststatus;
        }

        public void setPlayliststatus(String playliststatus) {
            this.playliststatus = playliststatus;
        }

        public String getPlaylistsrc() {
            return playlistsrc;
        }

        public void setPlaylistsrc(String playlistsrc) {
            this.playlistsrc = playlistsrc;
        }
    }
}
