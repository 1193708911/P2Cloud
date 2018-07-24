package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;

import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 剪辑列表提交请求类
 * Created by chjj on 2016/5/30.
 */
public class NewsCutDeleteAllBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }
    //调用请求新闻文稿接口
    public  void requestCutDeleteAll(String type,String playlist_id){
        String requestUrl=ServerConfig.SERVER_PATH_CUT_DELETEALL+playlist_id;
        NetUtils.httpRequest(type,requestUrl,new HashMap<String, String>(), new RequestCallback() {
            @Override
            public Class<? extends Response> getResultType() {
                return Response.class;
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
