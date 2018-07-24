package movi.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import movi.base.Response;
import movi.ui.bean.NewsLiuLiangListBean;
import movi.net.biz.NewsSelectLiuLiangBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.utils.AppManager;
import movi.utils.CommonUtils;
import movi.utils.ToastUtils;

/**
 * Created by chjj on 2016/5/13.
 */
@ContentView(value = R.layout.home_saveliu)
public class SaveLiuliangAcitivity extends Activity implements NetCallBack {
    @ViewInject(value = R.id.txtMaxSaveLiuLiang)
    private TextView txtMaxSaveLiuLiang;
    @ViewInject(value = R.id.txtTimeSave)
    private TextView txtTimeSave;
    @ViewInject(value = R.id.txtTotalLiuLiang)
    private TextView txtTotalLiuLiang;
    @ViewInject(value = R.id.txtEffectTime)
    private TextView txtEffectTime;
    //使用期
    @ViewInject(value = R.id.txtTime)
    private TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        sendGetLiuLiangRequest();
    }

    /**
     * 獲取流量請求
     */
    private void sendGetLiuLiangRequest() {
        NewsSelectLiuLiangBiz  biz=new NewsSelectLiuLiangBiz();
        biz.setNetCallBack(this);
        biz.requestSelectLiuLiang(ServerConfig.GET);
    }

    @Override
    public void onSuccess(Response response) {
        NewsLiuLiangListBean  listBean= (NewsLiuLiangListBean) response;
        NewsLiuLiangListBean.NewsLiuLiangBean  bean=listBean.getData();
        showResult(bean);
    }

    @Override
    public void onFailure(Response error) {
        ToastUtils.showToast(this, "数据请求失败");

    }
    private void showResult(NewsLiuLiangListBean.NewsLiuLiangBean bean) {
        txtTotalLiuLiang.setText(CommonUtils.fromHtml("#1341DD", bean.getTraffic())+"GB");
        txtTime.setText(bean.getCreated()+"到"+bean.getModified());
        txtMaxSaveLiuLiang.setText(CommonUtils.fromHtml("#1341DD", bean.getStorage())+"TB");
        txtTimeSave.setText(CommonUtils.fromHtml("#1341DD", bean.getStoragedate())+"GB");
        txtEffectTime.setText(bean.getDatemark());
    }

    /**
     * 监听事件
     * @param view
     */
    @Event(value = {R.id.txtBack})
    private void onClick(View view){
        switch (view.getId()){
            case R.id.txtBack:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
