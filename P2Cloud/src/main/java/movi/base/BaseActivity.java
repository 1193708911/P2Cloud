package movi.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

import com.ctvit.p2cloud.R;

import org.xutils.DbManager;
import org.xutils.x;

import movi.MainApplication;
import movi.utils.AppManager;
import movi.ui.view.CustomProgressDialog;
import movi.view.lib.SystemBarTintManager;

/**
 * Created by chjj on 2016/5/9.
 * 基类
 */
public abstract class BaseActivity extends FragmentActivity {
    protected static FragmentManager fragmentManager;
    protected DbManager db;
    private static CustomProgressDialog dialog;
    public static BaseActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置支持窗口为透明色解决fragment切换时卡顿问题
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        AppManager.getAppManager().addActivity(this);
        fragmentManager = getSupportFragmentManager();
        db = MainApplication.getDbManager();
        x.view().inject(this);
        this.mContext = this;
//        setStatusColor();
        afterViews();
    }
/*
    public void setStatusColor() {
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
    }*/

    //用于进行初始化
    protected void afterViews() {
    }

    ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }


    /**
     * 进度条显示
     */
    public static void showProgressDialog(Context context) {
        if (dialog == null) {
            dialog = new CustomProgressDialog(context, R.style.CustomAlertDialog);
        }
        if (dialog.isShowing()) {
            return;
        }
        dialog.setContentView(R.layout.customprogressdialog);
        dialog.show();

    }

    /**
     * 进度条隐藏
     */
    public static void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    /**
     * /data/hubei001/P2PROXY/CONTENTS/PROXY  mp4上传路径
     * /data/hubei001/P2PROXY/CONTENTS/ICON/   bmp上传路径
     * /data/hubei001/P2PROXY/CONTENTS/CLIP/     xml 上传路径
     */

    public static String REMOTE_SERVER_PATH = "10.7.0.58";
    public static String USER_NAME = "root";
    public static String USER_PASSWORD = "123456";


}
