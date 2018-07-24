package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;

import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 删除抽图接口
 * Created by chjj on 2016/5/30.
 */
public class NewsDeleteSheetBiz {
    public NetCallBack callBack;
    public void setNetCallBack(NetCallBack callBack) {
        this.callBack = callBack;
    }
    public void requestDeleteSheet(String type, String id) {
        String requestUrl = ServerConfig.SERVER_PATH_PHOTODELETE + id;
        NetUtils.httpRequest(ServerConfig.GET, requestUrl, new HashMap<String, String>(), new RequestCallback() {
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
