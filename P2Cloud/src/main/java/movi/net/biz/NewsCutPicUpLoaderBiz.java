package movi.net.biz;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import movi.MainApplication;
import movi.net.request.NetCallBack;
import movi.utils.ToastUtils;

/**
 * 文稿中心Biz请求
 * Created by chjj on 2016/5/30.
 */
public class NewsCutPicUpLoaderBiz{
    public NetCallBack  callBack;
    public void setNetCallBack(NetCallBack callBack){
        this.callBack=callBack;
    }
     public    void  httpRequest(String type,File  file){
    RequestParams  params=new RequestParams("http://123.56.6.125/wap/listshow");
     File  mfile=new File("mnt/sdcard/oppo.3gp");
    params.addBodyParameter("jog",mfile);
    x.http().post(params, new Callback.CommonCallback<String>() {
        @Override
        public void onSuccess(String s) {
            ToastUtils.showToast(MainApplication.getContext(),"成功");
        }

        @Override
        public void onError(Throwable throwable, boolean b) {

        }

        @Override
        public void onCancelled(CancelledException e) {

        }

        @Override
        public void onFinished() {

        }
    });

}
}
