package movi.ui.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import movi.MainApplication;
import movi.ui.adapter.ChannelAdapter;
import movi.base.BaseActivity;
import movi.base.Response;
import movi.ui.bean.ChannelListBean;
import movi.ui.bean.LoginListBean;
import movi.net.biz.NewApkDownLoadBiz;
import movi.net.biz.NewsAppChannelBiz;
import movi.net.biz.NewsLoginBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.utils.AppManager;
import movi.utils.CommonUtils;
import movi.utils.DbUtils;
import movi.utils.MyTextUtils;
import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;
import movi.ui.view.MyAlertDialog;
import movi.view.lib.SystemBarTintManager;

/**
 * 登录界面
 * 同时进行下载更新
 */
//@ContentView(R.layout.activity_login)
public class LoginActivity extends FragmentActivity implements NetCallBack, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {
    @ViewInject(value = R.id.et_userName)
    private EditText et_userName;
    @ViewInject(value = R.id.et_passWord)
    private EditText et_passWord;
    @ViewInject(value = R.id.btn_login)
    private Button btn_login;
    @ViewInject(R.id.cb_remember)
    private CheckBox cb_remember;
    @ViewInject(R.id.txtPingTai)
    private TextView txtPingTai;
    private ArrayList<ChannelListBean.ChannelBean> channels;
    private ChannelListBean.ChannelBean channelBean;
    private AlertDialog dialog;
    private static int position = -1;
    private boolean isChecked = PreferencesUtil.getBoolean("autoRem");
    //文件下载线程
    private String path = "";

    //判断是否为登录还是获取基本信息
    private static int DEFALUT;
    private static final int DEFAULT_LOGIN = 1;
    private static final int DEFAULT_CHANNELE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppManager.getAppManager().addActivity(this);
        x.view().inject(this);
        afterViews();
//        setStatusColor();
    }

   /* public void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.drawable.header_layout);
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
    }
*/
    @Event(value = {R.id.btn_login, R.id.txtPingTai})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (MyTextUtils.isEmpty(et_userName, getString(R.string.username_regest_empty))
                        && MyTextUtils.isEmpty(et_passWord, getString(R.string.password_notempty))) {
                    sendLoginRequest(et_userName.getText().toString().trim(), et_passWord.getText().toString().trim());
                }
                break;
            case R.id.txtPingTai:
                showAlertDilog();
                break;
        }
    }

    //显示选择电视台
    private void showAlertDilog() {
        View view = LayoutInflater.from(this).inflate(R.layout.channal_layout, null);
        ListView lv_chananl = (ListView) view.findViewById(R.id.lv_channal);
        dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view);
        dialog.show();
        if (channels == null) {
            channels = new ArrayList<>();
        }
        ChannelAdapter adapter = new ChannelAdapter(this, channels);
        lv_chananl.setAdapter(adapter);
        lv_chananl.setOnItemClickListener(this);


    }

    //发送网络登录请求
    private void sendLoginRequest(String userName, String password) {
        //同时保存到share中 准备到第二次进来读取
        DEFALUT = DEFAULT_LOGIN;
        BaseActivity.showProgressDialog(this);
        NewsLoginBiz biz = new NewsLoginBiz();
        biz.setNetCallBack(this);
        String deviceId = CommonUtils.getDeviceId(this);
        biz.requestLogin(ServerConfig.GET, userName, password, "!__!" + deviceId);

    }

    /**
     * 初始化调用的方法
     * 其他地方需要在这里进行处理
     */
    protected void afterViews() {
        setData();
        sendGetChannalMsg();
        setLisenner();

    }

    private void setLisenner() {
        cb_remember.setOnCheckedChangeListener(this);
    }

    //设置相应的数据
    private void setData() {
        if (PreferencesUtil.getBoolean("autoRem")) {
            //记住密码功能
            String username = PreferencesUtil.getString("username");
            String password = PreferencesUtil.getString("password");
            String pingTai = PreferencesUtil.getString("title");
            String address = PreferencesUtil.getString("address");
            if (pingTai != null && address != null) {
                txtPingTai.setText(pingTai);
                LogUtil.d("-----------------------------------------" + address);
                MainApplication.getInstance().setServerPath(address);
                LogUtil.d("当前要设置的address" + address);
            }
            et_userName.setText(username);
            et_passWord.setText(password);
            cb_remember.setChecked(true);
            txtPingTai.setText(pingTai);

        } else {
            et_userName.setText("");
            et_passWord.setText("");
            cb_remember.setChecked(false);
            txtPingTai.setText("");
        }
    }

    /**
     * 获取平台信息
     */
    private void sendGetChannalMsg() {
        DEFALUT = DEFAULT_CHANNELE;
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

            if (loginListBean != null) {
                if (loginListBean.getData() != null) {
                    if (loginListBean.getStatus() != 200) {
                        ToastUtils.showToast(LoginActivity.this, loginListBean.getMsg());
                        return;
                    }
                    MainApplication.getInstance().setLoginBean(loginListBean.getData());
                }
                DbUtils.saveUserInfo(loginListBean.getData());
                /**
                 * 该出判断不判断没什么影响
                 */
                if (isChecked) {
                    PreferencesUtil.putBoolean("autoRem", isChecked);
                    PreferencesUtil.putString("username", et_userName.getText().toString());
                    PreferencesUtil.putString("password", et_passWord.getText().toString());
                }


                Intent intent = new Intent(this, HomeActivity.class);
//                intent.putExtra("logle", PreferencesUtil.getString("logle"));
//                intent.putExtra("title", PreferencesUtil.getString("title"));
                intent.putExtra("logle", MainApplication.getInstance().getChannelBean().getApplogo());
                intent.putExtra("title", MainApplication.getInstance().getChannelBean().getTitle());
                startActivity(intent);
            }
        } else if (response instanceof ChannelListBean) {
            ChannelListBean channelListBean = (ChannelListBean) response;
            channels = channelListBean.getData();
            //判断当前preferutils 中是否有值
            if (channels == null || channels.size() <= 0) {
                return;
            }

            /**
             * 绑定相应的channel
             * 到mainapplication
             */
            if (PreferencesUtil.getString("title") != null) {
                for (ChannelListBean.ChannelBean channelBean : channels) {
                    if (PreferencesUtil.getString("title").equals(channelBean.getTitle())) {
                        MainApplication.getInstance().setChannelBean(channelBean);
                    }
                }
            }
            /**
             * 同时进行判断下载更新
             */
            final ChannelListBean.ChannelBean mchannelBean = channels.get(0);
            //判断相应的版本号
            if (CommonUtils.isVersionEquals(CommonUtils.getAppVersionName(this), mchannelBean.getVersionsanz())) {
//            if (!mchannelBean.getVersionsanz().equals(CommonUtils.getAppVersionName(this))) {
                path = mchannelBean.getAnzurl();
                BaseActivity.REMOTE_SERVER_PATH = mchannelBean.getSftpip();
                if (null == path || path.equals("")) {
                    path = "https://p2cloudweb.ctvit.tv/app/P2Cloud.apk";
                }
                final MyAlertDialog dialog = new MyAlertDialog(this).builder();
                dialog.setTitle("提示");
                dialog.setMsg("是否下载更新");
                dialog.setPositiveButton("是", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (path != null) {
                            downLoad(path);
                            LogUtil.d("当前的url的----------------------------------------------------" + path);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("否", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }

        }
    }

//    }

    /**
     * 显示进度条
     */
    private void downLoad(String path) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File saveDir = Environment.getExternalStorageDirectory();
            NewApkDownLoadBiz.DownLoad(path, this);
        } else {
            Toast.makeText(getApplicationContext(), R.string.sdcarderror, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Response error) {
        LogUtil.d(error.toString());
        BaseActivity.dismissProgressDialog();
        if (DEFALUT == DEFAULT_LOGIN) {
            ToastUtils.showToast(this, "登录失败");
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        channelBean = channels.get(position);
        dialog.dismiss();
        txtPingTai.setText(channelBean.getTitle());
        MainApplication.getInstance().setServerPath(channelBean.getIp());
        MainApplication.getInstance().setChannelBean(channelBean);
        LogUtil.d("1111111111111111111111111" + channelBean.getIp());
        LogUtil.d("111111111111111111111" + MainApplication.getInstance().getServerPath());
        PreferencesUtil.putString("title", channelBean.getTitle());
        PreferencesUtil.putString("address", channelBean.getIp());
        PreferencesUtil.putString("logle", channelBean.getApplogo());
        PreferencesUtil.putString("sftp", channelBean.getSftpip());

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            this.isChecked = true;
        } else {
            this.isChecked = false;
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
