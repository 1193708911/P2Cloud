package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import movi.base.Response;
import movi.ui.bean.NewsSheetMovieListBean;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 文稿中心Biz请求
 * Created by chjj on 2016/5/30.
 */
public class NewsSheetMovieBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }
    //调用请求新闻文稿接口
    public  void requestSheetMovie(String type,String playlistid){
        String requestUrl=ServerConfig.SERVER_PATH_SHEET_MOVIE+playlistid;
        Map<String,String> paramMap=new HashMap<>();

        NetUtils.httpRequest(type,requestUrl, paramMap, new RequestCallback() {
            @Override
            public Class<? extends Response> getResultType() {
                return NewsSheetMovieListBean.class;
            }

            @Override
            public void onSuccess(Response response) {
                LogUtil.d(response.toString());
                callBack.onSuccess(response);
            }

            @Override
            public void onFailure(Response response) {
                callBack.onFailure(response);

            }
        });

    }

}
