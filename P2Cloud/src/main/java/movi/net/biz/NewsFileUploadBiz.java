package movi.net.biz;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import movi.MainApplication;
import movi.base.Response;
import movi.ui.bean.NewsSheetPicBean;
import movi.net.config.ServerConfig;
import movi.ui.fragment.NewsSheetFragment;
import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;

/**
 * 文稿中心Biz请求
 * Created by chjj on 2016/5/30.
 */
public class NewsFileUploadBiz {
    private  boolean isFengMian=false;
    public MyCallBack callBack;


    public void requestFileUpload(String type, String is_cover, String title, String is_cover_id, String is_cover_id_end, String playlist_id, ArrayList<NewsSheetPicBean> files) {
        String requestUrl = PreferencesUtil.getString("address") + ServerConfig.SERVER_PATH_PHOTOUPLOAD;
        RequestParams param = new RequestParams(requestUrl);
        param.addBodyParameter("is_cover_id", is_cover_id);
        param.addBodyParameter("is_cover_id_end", is_cover_id_end);
        param.addBodyParameter("is_cover", is_cover);
        param.addBodyParameter("playlist_id", playlist_id);
        param.addBodyParameter("title", title);
        if (files != null && files.size() >1) {
            for (int i = 1; i < files.size(); i++) {
                NewsSheetPicBean picBean = files.get(i);
                if (picBean.getIsNet().equals("0")) {
                    File bitmapFile = new File(picBean.getLogo());
                    param.addBodyParameter("photo[" + (i-1) + "]", bitmapFile);
                    LogUtil.d("-----------"+i+"-----------"+bitmapFile);
                }
                if (picBean.getIs_cover()!=null&&"1".equals(picBean.getIs_cover())) {
                    isFengMian=true;
                }
            }
        }else{
            ToastUtils.showToast(MainApplication.getContext(),"当前上传图片列表为空");
        }
        if (isFengMian) {
            fileUpload(param);
        } else {
            ToastUtils.showToast(MainApplication.getContext(), "请设置封面");
        }


    }

    private void fileUpload(RequestParams param) {
        LogUtil.d("-------"+param.toString());
        x.http().post(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                callBack.onSucces(s);
                //解析s
                Response response = JSON.parseObject(s, Response.class);
                NewsSheetFragment.isSubmit=false;
                LogUtil.d("后台返回数据"+s);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                callBack.onFailure("请求失败");
                NewsSheetFragment.isSubmit=false;
            }

            @Override
            public void onCancelled(CancelledException e) {
                NewsSheetFragment.isSubmit=false;

            }

            @Override
            public void onFinished() {
                NewsSheetFragment.isSubmit=false;

            }
        });
    }


    public interface MyCallBack {
        public void onSucces(String result);

        public void onFailure(String error);
    }

    public void setMyCallBack(MyCallBack callback) {
        this.callBack = callback;

    }

}
