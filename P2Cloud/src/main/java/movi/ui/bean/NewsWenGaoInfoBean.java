package movi.ui.bean;

import java.io.Serializable;

import movi.base.Response;

/**
 * Created by chjj on 2016/6/29.
 */
public class NewsWenGaoInfoBean extends Response implements Serializable {

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }

    private boolean data;


}
