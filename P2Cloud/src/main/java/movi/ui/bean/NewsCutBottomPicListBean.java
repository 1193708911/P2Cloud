package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import movi.base.Response;

/**
 * 剪辑列表底部图片信息
 */
public class NewsCutBottomPicListBean extends Response implements Serializable {


    private ArrayList<NewsCutBottomBean> data;
    private String playliststatus;//0代表保存，1代表提交private String

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlayliststatus() {
        return playliststatus;
    }

    public void setPlayliststatus(String playliststatus) {
        this.playliststatus = playliststatus;
    }

    private String title;

    public ArrayList<NewsCutBottomBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewsCutBottomBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NewsCutBottomPicListBean{" +
                "data=" + data +
                ", playliststatus='" + playliststatus + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}


