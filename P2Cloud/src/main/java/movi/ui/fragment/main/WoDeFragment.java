package movi.ui.fragment.main;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import movi.MainApplication;
import movi.ui.activity.AboutActivity;
import movi.ui.activity.AdviseActivity;
import movi.ui.activity.LoginActivity;
import movi.ui.activity.SaveLiuliangAcitivity;
import movi.ui.activity.SettingActivity;
import movi.base.BaseActivity;
import movi.base.BaseFragment;
import movi.base.Response;
import movi.net.biz.NewsExitBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.ui.view.MyAlertDialog;
import movi.utils.AppManager;

/**
 * Created by chjj on 2016/7/22.
 */
@ContentView(R.layout.fragment_mine)
public class WoDeFragment extends BaseFragment implements NetCallBack {
    @ViewInject(R.id.txtUserName)
    private TextView txtUserName;

    @Override
    protected int getResource() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initView();
    }

    //初始化
    private void initView() {
        txtUserName.setText(MainApplication.getInstance().getUser().getUsername());
    }

    /**
     * 监听事件
     *
     * @param view
     */
    @Event(value = {R.id.ll_setting, R.id.ll_liuliang, R.id.ll_about, R.id.ll_advise, R.id.txtExit})
    private void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ll_setting:
                intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_liuliang:
                intent = new Intent(context, SaveLiuliangAcitivity.class);
                startActivity(intent);
                break;
            case R.id.ll_about:
                intent = new Intent(context, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_advise:
                intent = new Intent(context, AdviseActivity.class);
                startActivity(intent);
                break;
            case R.id.txtExit:
                appExit();
                break;

        }

    }

    private void appExit() {
        final MyAlertDialog  dialog=new MyAlertDialog(context).builder();
        dialog.setMsg("是否退出应用");
        dialog.setNegativeButton("否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton("是", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sendExitRequest();
            }
        });
        dialog.show();

    }

    //发送退出请求
    private void sendExitRequest() {
        BaseActivity.showProgressDialog(context);
        NewsExitBiz biz = new NewsExitBiz();
        biz.setNetCallBack(this);
        biz.requestExitMsg(ServerConfig.GET);
    }

    /**
     * 退出登录
     */
    private void exit() {
        BaseActivity.dismissProgressDialog();
        try {
            MainApplication.getDbManager().dropDb();
            android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
            AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            LogUtil.d("当前执行了退出接口");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Response response) {
        exit();
    }

    @Override
    public void onFailure(Response error) {
        exit();
    }
}
