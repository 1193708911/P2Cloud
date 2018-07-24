package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.Map;

import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 剪辑列表提交请求类
 * Created by chjj on 2016/5/30.
 */
public class NewsCutBottomSaveBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }
    public  void requestCutBottomSave(String type,Map<String,String> paramMap){
        String requestUrl=ServerConfig.SERVER_PATH_PLAYADD;
        NetUtils.httpRequest(type,requestUrl,paramMap, new RequestCallback() {
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
