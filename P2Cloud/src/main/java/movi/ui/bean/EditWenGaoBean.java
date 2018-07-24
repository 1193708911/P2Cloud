package movi.ui.bean;

import java.io.Serializable;

import movi.base.Response;

/**
 * Created by chjj on 2016/6/3.
 */
public class EditWenGaoBean extends Response implements Serializable {


    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {


        private SBean s;

        public SBean getS() {
            return s;
        }

        public void setS(SBean s) {
            this.s = s;
        }

        public static class SBean {
            private String classX;
            private String alias;
            private String playlist_id;
            private String title;
            private String content;
            private String keyword;
            private String create_time;
            private String type;
            private String is_cover;
            private String id;

            public String getClassX() {
                return classX;
            }

            public void setClassX(String classX) {
                this.classX = classX;
            }

            public String getAlias() {
                return alias;
            }

            public void setAlias(String alias) {
                this.alias = alias;
            }

            public String getPlaylist_id() {
                return playlist_id;
            }

            public void setPlaylist_id(String playlist_id) {
                this.playlist_id = playlist_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getKeyword() {
                return keyword;
            }

            public void setKeyword(String keyword) {
                this.keyword = keyword;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getIs_cover() {
                return is_cover;
            }

            public void setIs_cover(String is_cover) {
                this.is_cover = is_cover;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
