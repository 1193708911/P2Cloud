package movi.ui.bean;

import java.io.Serializable;

import movi.base.Response;

/**
 * Created by chjj on 2016/6/7.
 * 文稿信息列表
 */
public class NewsWenGaoInfoListBean extends Response implements Serializable {
    public NewsWenGaoInfoBean getData() {
        return data;
    }

    public void setData(NewsWenGaoInfoBean data) {
        this.data = data;
    }

    private NewsWenGaoInfoBean data;

    public class NewsWenGaoInfoBean {
        private String title;
        private String keyword;
        private String content;
        private String id;
        private String create_time;
        private String playlist_id;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getPlaylist_id() {
            return playlist_id;
        }

        public void setPlaylist_id(String playlist_id) {
            this.playlist_id = playlist_id;
        }
    }

}
