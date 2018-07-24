package movi.ui.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import movi.base.BaseFragment;
import movi.base.Response;
import movi.net.biz.NewsUpdatePsBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.utils.MyTextUtils;

/**
 * 设置界面
 */
@ContentView(R.layout.setting)
public class SettingFragment extends BaseFragment implements NetCallBack {
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
    protected int getResource() {
        return R.layout.setting;
    }

    @Override
    protected void afterViews() {
        super.afterViews();

    }

    /**
     * 点击监听事件
     *
     * @param view
     */
    @Event(value = {R.id.txtResetPwd, R.id.txtInfo, R.id.btn_save})
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
        }

    }

    @Override
    public void onSuccess(Response response) {

    }

    @Override
    public void onFailure(Response error) {

    }
}
