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
 * 新闻文稿修改接口
 * Created by chjj on 2016/5/30.
 */
public class NewsEditWenGaoBiz {
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }
    //调用请求新闻文稿接口
    public  void requestWenGaoMsg(String type,String id,String playlist_id,String title,String keyword,String content){
        String requestUrl=ServerConfig.SERVER_PATH_WebGao;
        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("id",id);
        paramMap.put("playlist_id",playlist_id);
        paramMap.put("title",title);
        paramMap.put("keyword",keyword);
        paramMap.put("content",content);

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
