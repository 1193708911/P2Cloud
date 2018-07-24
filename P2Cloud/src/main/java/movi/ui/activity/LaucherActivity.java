package movi.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;

import movi.MainApplication;
import movi.base.BaseActivity;
import movi.base.Response;
import movi.ui.bean.ChannelListBean;
import movi.ui.bean.LoginListBean;
import movi.ui.bean.ThreeLoginBean;
import movi.net.biz.NewsAppChannelBiz;
import movi.net.biz.NewsLoginBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.utils.CommonUtils;
import movi.utils.DbUtils;
import movi.utils.ToastUtils;
import movi.view.lib.SystemBarTintManager;

@ContentView(R.layout.activity_laucher)
public class LaucherActivity extends BaseActivity implements NetCallBack {
    private static final int delay_mills = 1000;
    public Intent intent;
    private ArrayList<ChannelListBean.ChannelBean> channels;
    private ChannelListBean.ChannelBean channelBean;
    //文件下载线程
    private String path = "";
    private String loginId, loginName, platform;

    private static final int LOGIN = 0x001;
    private static final int CHANNEL = 0x002;
    private static int DEFALUT;
 /*   public void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.drawable.laycher_layout);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }*/
    protected void afterViews() {
        super.afterViews();
//        setStatusColor();
        intent = getIntent();
        if (intent != null) {
            ThreeLoginBean loginBean =JSON.parseObject(intent.getStringExtra("p2Cloud"),ThreeLoginBean.class);
            if (loginBean != null) {
                loginId = loginBean.getLoginId();
                loginName = loginBean.getLoginName();
                platform = loginBean.getPlatform();
                LogUtil.d("第三方登录"+"loginId"+loginId+"platform:"+platform);
                //开始调用接口进行自动调用
                sendGetChannalMsg();
            } else {
                delayLogin();
            }

        } else {
            delayLogin();
        }
    }

    private void delayLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectLogin();
            }
        }, delay_mills);
    }

    private void redirectLogin() {
        Intent intent = new Intent(LaucherActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //发送网络登录请求
    private void sendLoginRequest(String userName, String password) {
        showProgressDialog(this);
        DEFALUT = LOGIN;
        NewsLoginBiz biz = new NewsLoginBiz();
        biz.setNetCallBack(this);
        String  deviceId= CommonUtils.getDeviceId(this);
        biz.requestLogin(ServerConfig.GET, userName, password,"!__!"+deviceId);
    }


    /**
     * 获取平台信息
     */
    private void sendGetChannalMsg() {
        DEFALUT = CHANNEL;
        NewsAppChannelBiz biz = new NewsAppChannelBiz();
        biz.setNetCallBack(this);
        biz.requestAppChannel(ServerConfig.GET);
    }


    @Override
    public void onSuccess(Response response) {
        //处理response
        BaseActivity.dismissProgressDialog();
        if (response instanceof LoginListBean) {
            LoginListBean loginListBean = (LoginListBean) response;
            ToastUtils.showToast(this, loginListBean.getMsg());
            if (loginListBean != null) {
                if(loginListBean.getStatus()!=200){
                    ToastUtils.showToast(LaucherActivity.this, loginListBean.getMsg());
                    redirectLogin();
                    return;
                }

                if (loginListBean.getData() != null) {
                    MainApplication.getInstance().setLoginBean(loginListBean.getData());
                }
                DbUtils.saveUserInfo(loginListBean.getData());
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("logle", MainApplication.getInstance().getChannelBean().getApplogo());
                intent.putExtra("title", MainApplication.getInstance().getChannelBean().getTitle());
                startActivity(intent);
                finish();
            }
        } else if (response instanceof ChannelListBean) {
            ChannelListBean channelListBean = (ChannelListBean) response;
            channels = channelListBean.getData();
            if (channels == null || channels.size() <= 0) {
                return;
            }

            /**
             * 绑定相应的channel
             * 到mainapplication
             */

          /*  if (platform!=null&&platform.equals("cjy")) {
                for (ChannelListBean.ChannelBean channelBean : channels) {
                    if (channelBean.getTitle().equals("湖北广播电视台")) {
                        MainApplication.getInstance().setServerPath(channelBean.getIp());
                        MainApplication.getInstance().setChannelBean(channelBean);

                    }
                }
            }*/
            if (platform != null) {
                for (ChannelListBean.ChannelBean channelBean : channels) {
                    if (platform.equalsIgnoreCase(channelBean.getPlatform())) {
                        MainApplication.getInstance().setServerPath(channelBean.getIp());
                        MainApplication.getInstance().setChannelBean(channelBean);
                    }
                }
            }
            sendLoginRequest(loginId + "!__!" + platform, "0");
//            sendLoginRequest("041189" + "!__!" + "cjy", "0");
        }

    }

    @Override
    public void onFailure(Response error) {
        LogUtil.d(error.toString());
        BaseActivity.dismissProgressDialog();
        if(DEFALUT==LOGIN){
            if(error.getMsg()!=null){
                ToastUtils.showToast(this, error.getMsg());
            }else{
                ToastUtils.showToast(this, "登录失败");
            }

        }
        else if(DEFALUT==CHANNEL){
            ToastUtils.showToast(this, "获取列表失败");
        }

    }


}
