package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import movi.base.Response;
import movi.ui.bean.CloudListBean;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 文稿中心Biz请求
 * Created by chjj on 2016/5/30.
 */
public class NewsCloudListUploadBiz {
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
    public  void requestCloudListRequest(String type){

        String requestUrl=ServerConfig.SERVER_PATH_PLAYADD;
        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("name[]","name1");
        paramMap.put("name[]","name2");
        NetUtils.httpRequest(ServerConfig.POST,requestUrl, paramMap, new RequestCallback() {
            @Override
            public Class<? extends Response> getResultType() {
                return CloudListBean.class;
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
