package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import movi.base.Response;

/**
 * Created by chjj on 2016/6/15.
 * 抽图界面底部图片bean
 */
public class NewsSheetBtPicListBean extends Response implements Serializable{
    private ArrayList<NewsSheetPicBean> data;
    private String is_cover_id_end;//封面id

    public ArrayList<NewsSheetPicBean> getData() {
        return data;
    }

    public void setData(ArrayList<NewsSheetPicBean> data) {
        this.data = data;
    }

    public String getIs_cover_id_end() {
        return is_cover_id_end;
    }

    public void setIs_cover_id_end(String is_cover_id_end) {
        this.is_cover_id_end = is_cover_id_end;
    }




}
