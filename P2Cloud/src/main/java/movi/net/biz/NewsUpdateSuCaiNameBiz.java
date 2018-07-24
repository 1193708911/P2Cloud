package movi.net.biz;

import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;
import movi.ui.bean.NewsSheetBtPicListBean;

/**
 * 反馈信息接口
 * Created by chjj on 2016/5/30.
 */
public class NewsUpdateSuCaiNameBiz {
    public NetCallBack callBack;

    public void setNetCallBack(NetCallBack callBack) {
        this.callBack = callBack;
    }


    public void requestUpdateSuCaiName(Map<String,String> param) {
        String requestUrl=ServerConfig.SERVER_PATH_UPDATE_SUCAINAME;
        NetUtils.httpRequest(ServerConfig.POST, requestUrl, param, new RequestCallback() {
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
