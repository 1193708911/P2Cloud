package movi.net.request;



import movi.base.Response;


/**
 * Created by ZHXG on 2016/3/31.
 */
public interface NetCallBack {
    public void onSuccess(Response response);

    public void onFailure(Response error);
}
