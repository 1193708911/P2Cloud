package movi.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.ctvit.p2cloud.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import movi.base.Response;
import movi.net.biz.NewsAdviseBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.utils.AppManager;
import movi.utils.MyTextUtils;
import movi.utils.ToastUtils;

@ContentView(R.layout.activity_advise)
public class AdviseActivity extends Activity implements NetCallBack {
    @ViewInject(R.id.etAdvise)
    private EditText etAdvise;
    @ViewInject(R.id.etEmail)
    private EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        AppManager.getAppManager().addActivity(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    /**
     * 监听事件
     *
     * @param view
     */
    @Event(value = {R.id.txtBack, R.id.txtSend})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtBack:
                this.finish();
                break;
            case R.id.txtSend:
                sendHttpAdviseRequest();
                break;
        }
    }

    /**
     * 发送反馈请求
     */
    private void sendHttpAdviseRequest() {
        if (MyTextUtils.isEmpty(etAdvise, "反馈意见不能为空，请填写")) {
            String email = "0";
            String advise="0";
            advise=etAdvise.getText().toString();
            if (!TextUtils.isEmpty(etEmail.getText().toString().trim()) ) {
                email = etEmail.getText().toString();
            }
            /**
             * 发送请求
             */
        sendAdviseHttpRequest(email,advise);
        }

    }

    private void sendAdviseHttpRequest(String email,String advise) {
        NewsAdviseBiz  adviseBiz=new NewsAdviseBiz();
        adviseBiz.setNetCallBack(this);
        adviseBiz.requestAdviseMsg(ServerConfig.POST,email,advise);

    }

    @Override
    public void onSuccess(Response response) {
        if(response.getStatus()==200){
            ToastUtils.showToast(this,"提交反馈成功");
            return;
        }
        ToastUtils.showToast(this,"提交反馈失败");

    }

    @Override
    public void onFailure(Response error) {
        ToastUtils.showToast(this,"提交反馈失败");
    }
}
