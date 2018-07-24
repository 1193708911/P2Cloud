package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;

import movi.base.Response;
import movi.ui.bean.HeadPicList;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 剪辑列表
 * Created by chjj on 2016/5/30.
 */
public class NewsHeadPicBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }

    /**
     * 头部剪辑列表
     * @param type
     * @param
     * @param
     * @param
     */
    //调用请求剪辑列表
    public  void requestHeadPic(String type){
        String requestUrl=ServerConfig.SERVER_PATH_HEADPIC;
        NetUtils.httpRequest(ServerConfig.GET,requestUrl,  new HashMap<String,String>(), new RequestCallback() {
            @Override
            public Class<? extends Response> getResultType() {
                return HeadPicList.class;
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
