package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;

import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;
import movi.ui.bean.NewClipStatusBean;
import movi.ui.bean.NewsCutBottomPicListBean;
import movi.ui.bean.NewsPicListBean;

/**
 * 文稿中心Biz请求
 * Created by chjj on 2016/5/30.
 */
public class NewsClipStatusBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }

    public  void requestClipStatus(String type){
        String requestUrl=ServerConfig.SERVER_PATH_CLIP_STATUS;
        NetUtils.httpRequest(ServerConfig.GET,requestUrl, new HashMap<String,String>(), new RequestCallback() {
            @Override
            public Class<? extends Response> getResultType() {
                return NewsPicListBean.class;
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
