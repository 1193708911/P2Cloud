package movi.net.biz;

import org.xutils.common.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import movi.base.Response;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.net.request.NetUtils;
import movi.net.request.RequestCallback;

/**
 * 反馈信息接口
 * Created by chjj on 2016/5/30.
 */
public class NewsAdviseBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }
    //调用请求新闻文稿接口
    public  void requestAdviseMsg(String type,String email,String idea){
        String requestUrl=ServerConfig.SERVER_PATH_ADVISE;
        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("email",email);
        paramMap.put("idea",idea);
        NetUtils.httpRequest(type,requestUrl, paramMap, new RequestCallback() {
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
