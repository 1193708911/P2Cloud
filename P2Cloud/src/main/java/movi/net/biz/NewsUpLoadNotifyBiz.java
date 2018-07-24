package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import movi.MainApplication;
import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 上传成功后通知后台接口
 */
public class NewsUpLoadNotifyBiz {
    public NetCallBack callBack;
    public void setNetCallBack(NetCallBack callBack) {
        this.callBack = callBack;
    }
    public void requestUpLoadNotify(String type, String clipname, String username) {
        String requestUrl = MainApplication.getInstance().getServerPath()+ServerConfig.SERVER_PATH_NOTIFY_UPLOAD;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("clipname", clipname);
        paramMap.put("username", username);
        NetUtils.httpRequest(type, requestUrl, paramMap, new RequestCallback() {
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
