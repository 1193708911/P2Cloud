package movi.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import movi.MainApplication;
import movi.base.Response;
import movi.ui.bean.LoginListBean;
import movi.net.biz.NewsUpdateInfoBiz;
import movi.net.biz.NewsUpdatePsBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.utils.AppManager;
import movi.utils.MyTextUtils;
import movi.utils.ToastUtils;
import movi.utils.Util;

/**
 * 设置界面
 */
@ContentView(R.layout.setting)
public class SettingActivity extends Activity implements NetCallBack {
    @ViewInject(R.id.txtInfo)
    private TextView txtInfo;
    @ViewInject(R.id.txtResetPwd)
    private TextView txtResetPwd;
    @ViewInject(R.id.v_info)
    private View v_info;
    @ViewInject(R.id.v_reset)
    private View v_reset;
    @ViewInject(R.id.ll_info)
    private LinearLayout ll_info;
    @ViewInject(R.id.ll_reset)
    private LinearLayout ll_reset;
    @ViewInject(R.id.etEmail)
    private EditText etEmail;
    @ViewInject(R.id.etName)
    private EditText etName;
    @ViewInject(R.id.etXing)
    private EditText etXing;
    @ViewInject(R.id.etNewPs)
    private EditText etNewPs;
    @ViewInject(R.id.etSureNewPs)
    private EditText etSureNewPs;
    @ViewInject(R.id.etSurePs)
    private EditText etSurePs;
    private boolean isInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);
        setData();
    }

    /**
     * 绑定数据
     */
    private void setData() {
        if (isInfo) {
            LoginListBean.LoginBean loginBean = MainApplication.getInstance().getLoginBean();
            etXing.setText(loginBean.getGive_name());
            etName.setText(loginBean.getUsername());
        }

    }

    /**
     * 点击监听事件
     *
     * @param view
     */
    @Event(value = {R.id.txtResetPwd, R.id.txtInfo, R.id.btn_save, R.id.txtBack})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtResetPwd:
                resetBac();
                v_reset.setBackgroundColor(Color.WHITE);
                ll_reset.setVisibility(View.VISIBLE);
                isInfo = false;
                break;
            case R.id.txtInfo:
                resetBac();
                v_info.setBackgroundColor(Color.WHITE);
                ll_info.setVisibility(View.VISIBLE);
                isInfo = true;
                break;
            case R.id.btn_save:
                if (isInfo) {
                    if (MyTextUtils.isEmpty(etName, "名不能为空")
                            && MyTextUtils.isEmpty(etXing, "姓不能为空")
                            && MyTextUtils.isEmpty(etEmail, "邮箱不能为空")) {
                        //业务处理
                        if (!Util.isEmail(etEmail.getText().toString())) {
                            ToastUtils.showToast(this, "邮箱格式不正确，请重新填写");
                            return;
                        }
                        sendHttpRequest(etName.getText().toString(), etXing.getText().toString(), etEmail.getText().toString());
                    }
                } else {
                    if (MyTextUtils.isEmpty(etNewPs, "新密码不能为空")
                            && MyTextUtils.isEmpty(etSureNewPs, "确认新密码不能为空")
                            && MyTextUtils.isEmpty(etSurePs, "确认密码不能为空")) {
                        //业务处理
                        sendHttpRequest(etNewPs.getText().toString(), etSureNewPs.getText().toString(), etSurePs.getText().toString());
                    }
                }
                break;
            case R.id.txtBack:
                this.finish();
                break;
        }
    }

    public void resetBac() {
        v_info.setBackgroundColor(Color.TRANSPARENT);
        v_reset.setBackgroundColor(Color.TRANSPARENT);
        ll_info.setVisibility(View.GONE);
        ll_reset.setVisibility(View.GONE);
    }

    //发送请求
    public void sendHttpRequest(String ming, String xing, String email) {
        if (!isInfo) {
            NewsUpdatePsBiz updatePsBiz = new NewsUpdatePsBiz();
            updatePsBiz.setNetCallBack(this);
            updatePsBiz.requestUpdatePs(ServerConfig.GET, etNewPs.getText().toString(), etSureNewPs.getText().toString(), etSurePs.getText().toString());
        } else {
            NewsUpdateInfoBiz updateInfoBiz = new NewsUpdateInfoBiz();
            updateInfoBiz.setNetCallBack(this);
            updateInfoBiz.requestUpdatInfo(ServerConfig.GET, etXing.getText().toString(), etName.getText().toString(), etEmail.getText().toString());
        }

    }

    @Override
    public void onSuccess(Response response) {
        ToastUtils.showToast(this, "保存成功");
    }

    @Override
    public void onFailure(Response error) {
        ToastUtils.showToast(this, "保存失败");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
