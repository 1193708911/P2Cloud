package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;

import movi.base.Response;
import movi.ui.bean.NewsWenGaoInfoListBean;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 文稿信息请求
 * Created by chjj on 2016/5/30.
 */
public class NewsGetWenGaoBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }
    public  void requestWenGaoList(String type,String id){
        String requestUrl=ServerConfig.SERVER_PATH_PIC+id;
        NetUtils.httpRequest(type,requestUrl, new HashMap<String,String>(), new RequestCallback() {
            @Override
            public Class<? extends Response> getResultType() {
                return NewsWenGaoInfoListBean.class;
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
