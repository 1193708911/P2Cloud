package movi.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import io.vov.vitamio.widget.MediaController;
import movi.MainApplication;
import movi.base.BaseActivity;
import movi.base.Response;
import movi.net.biz.NewsExitBiz;
import movi.net.config.ServerConfig;
import movi.ui.fragment.main.HomeFragment;
import movi.ui.fragment.main.PaiSheFragment;
import movi.ui.fragment.main.WoDeFragment;
import movi.ui.fragment.main.ZhiBoFragment;
import movi.net.request.NetCallBack;
import movi.utils.AppManager;
import movi.utils.LogUtils;


@ContentView(R.layout.activity_home1)
public class HomeActivity extends BaseActivity implements NetCallBack {

    @ViewInject(R.id.imbPaiShe)
    private ImageButton imbPaiShe;
    @ViewInject(R.id.imgZhiBo)
    private ImageButton imgZhiBo;
    @ViewInject(R.id.imgWoDe)
    private ImageButton imgWoDe;
    @ViewInject(R.id.imgCut)
    private ImageButton imgCut;
    @ViewInject(R.id.rl_first)
    private RelativeLayout rl_first;
    @ViewInject(R.id.imgLogle)
    private ImageView  imgLogle;
    private static HomeFragment homeFragment;
    public static PaiSheFragment paiSheFragment;
    private static ZhiBoFragment zhiBoFragment;
    private static WoDeFragment woDeFragment;
    private static final int DEFAULT_EXIT = 1;
    private static int DEFAULT = 0;

    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;

    /**
     * 提供广播更新ui
     */
    private IntentFilter intentFilter;
    private MyReBacReceiver receiver;

    /**
     * 更新背景
     */
    private class MyReBacReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            resetCutBac();
        }
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        String pingTai = getIntent().getStringExtra("title") + "-" + "现场新闻生产";
        txtTitle.setText(pingTai);
        BaseActivity.REMOTE_SERVER_PATH = "123.56.6.126";
        BaseActivity.USER_NAME = MainApplication.getInstance().getUser().getUsername();
        BaseActivity.USER_PASSWORD = MainApplication.getInstance().getUser().getPassword();
        initReceiver();
        initImgTitle();
        initView();
    }

    private void initImgTitle() {
        Glide.with(this).load(MainApplication.getInstance().getChannelBean().getApplogo()).into(imgLogle);
    }

    private void initReceiver() {
        intentFilter = new IntentFilter(HomeFragment.ACTION_PRGRESS);
        receiver = new MyReBacReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("----------------------destryed");
        unregisterReceiver(receiver);
    }

    /**
     * 初始化
     */
    private void initView() {
        resetBac();
        imgCut.setImageDrawable(getResources().getDrawable(R.mipmap.ic_cut_press));
        replaceHomeFragment();
    }

    /**
     * 监听
     *
     * @param view
     */
    @Event(value = {R.id.rlCut, R.id.rlPaiShe, R.id.rlZhiBo, R.id.rlWoDe, R.id.imgTitle})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlCut:
                resetBac();
                imgCut.setImageDrawable(getResources().getDrawable(R.mipmap.ic_cut_press));
                replaceHomeFragment();
                break;
            case R.id.rlPaiShe:
                resetBac();
                rl_first.setVisibility(View.GONE);
                imbPaiShe.setImageDrawable(getResources().getDrawable(R.mipmap.ic_photo_press));
                replacePaiSheFragment();
                break;
            case R.id.rlZhiBo:
                resetBac();
                rl_first.setVisibility(View.GONE);
                imgZhiBo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_zhibo_press));
                replaceZhiBoFragment();
                break;
            case R.id.rlWoDe:
                resetBac();
                rl_first.setVisibility(View.GONE);
                imgWoDe.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mine_press));
                replaceWoDeFragment();
                break;
            case R.id.imgTitle:
                Intent intent = new Intent(this, CloudSearchActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * 监听
     *
     * @param response
     */
    @Override
    public void onSuccess(Response response) {
        exit();

    }

    @Override
    public void onFailure(Response error) {
        exit();
    }

    /**
     * 替换抽图列表
     */
    public static void replaceHomeFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            transaction.add(R.id.contaner, homeFragment);
        } else {
            transaction.show(homeFragment);
        }
        transaction.commitAllowingStateLoss();

    }

    /**
     * 替换文稿列表
     */
    public static void replacePaiSheFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        if (paiSheFragment == null) {
            paiSheFragment = new PaiSheFragment();
            transaction.add(R.id.contaner, paiSheFragment);
        } else {
            transaction.show(paiSheFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 替换合成发布列表
     */
    public static void replaceZhiBoFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        if (zhiBoFragment == null) {
            zhiBoFragment = new ZhiBoFragment();
            transaction.add(R.id.contaner, zhiBoFragment);
        } else {
            transaction.show(zhiBoFragment);
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 替换合成发布列表
     */
    public static void replaceWoDeFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        if (woDeFragment == null) {
            woDeFragment = new WoDeFragment();
            transaction.add(R.id.contaner, woDeFragment);
        } else {
            transaction.show(woDeFragment);
        }
        transaction.commitAllowingStateLoss();
    }


    /**
     * 隐藏所有的fragment
     */
    private static void hideFragment(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (paiSheFragment != null) {
            transaction.hide(paiSheFragment);
        }
        if (zhiBoFragment != null) {
            transaction.hide(zhiBoFragment);
        }
        if (woDeFragment != null) {
            transaction.hide(woDeFragment);
        }

    }

    /**
     * 监听返回键
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                sendExitRequest();
                LogUtil.d("执行了当前的退出请求");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //发送退出请求
    private void sendExitRequest() {
        DEFAULT = DEFAULT_EXIT;
        showProgressDialog(this);
        NewsExitBiz biz = new NewsExitBiz();
        biz.setNetCallBack(this);
        biz.requestExitMsg(ServerConfig.GET);
    }

    /**
     * 退出登录
     */
    private void exit() {
        dismissProgressDialog();
        if (DEFAULT == DEFAULT_EXIT) {
            try {
//                MainApplication.getDbManager().dropDb();
                android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                AppManager.getAppManager().finishAllActivity();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                LogUtil.d("当前执行了退出接口");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重置背景
     */
    public void resetBac() {
        rl_first.setVisibility(View.VISIBLE);
        imgCut.setImageDrawable(getResources().getDrawable(R.mipmap.ic_cut_normal));
        imbPaiShe.setImageDrawable(getResources().getDrawable(R.mipmap.ic_photo_normal));
        imgZhiBo.setImageDrawable(getResources().getDrawable(R.mipmap.ic_zhibo_normal));
        imgWoDe.setImageDrawable(getResources().getDrawable(R.mipmap.ic_mine_normal));

    }

    public void resetCutBac() {
        resetBac();
        imgCut.setImageDrawable(getResources().getDrawable(R.mipmap.ic_cut_press));
    }


}
