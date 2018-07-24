package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;

import movi.base.Response;
import movi.ui.bean.NewsLiuLiangListBean;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 文稿中心Biz请求
 * Created by chjj on 2016/5/30.
 */
public class NewsSelectLiuLiangBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }

    /**
     * @param type
     * @param
     * @param
     * @param
     */
    //调用请求新闻文稿接口
    public  void requestSelectLiuLiang(String type){

        String requestUrl=ServerConfig.SERVER_PATH_SELECT_LIULIANG;
        NetUtils.httpRequest(ServerConfig.GET,requestUrl, new HashMap<String,String>(), new RequestCallback() {
            @Override
            public Class<? extends Response> getResultType() {
                return NewsLiuLiangListBean.class;
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
