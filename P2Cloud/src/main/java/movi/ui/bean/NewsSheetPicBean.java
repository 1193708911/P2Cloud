package movi.ui.bean;

/**
 * Created by chjj on 2016/6/20.
 * <p/>
 * 下方图片信息实体类
 */
public class NewsSheetPicBean {
    private String title;


    private String playlist_id;
    private String id;
    private String type;
    private String logo;
    private String is_cover;
    private String isNet = "1";

    public String getIsNet() {
        return isNet;
    }

    public void setIsNet(String isNet) {
        this.isNet = isNet;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIs_cover() {
        return is_cover;
    }

    public void setIs_cover(String is_cover) {
        this.is_cover = is_cover;
    }

    @Override
    public String toString() {
        return "NewsSheetPicBean{" +
                "title='" + title + '\'' +
                ", playlist_id='" + playlist_id + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", logo='" + logo + '\'' +
                ", is_cover='" + is_cover + '\'' +
                ", isNet='" + isNet + '\'' +
                '}';
    }
}