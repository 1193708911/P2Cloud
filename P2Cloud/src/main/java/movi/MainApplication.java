package movi;

import android.app.Application;
import android.content.Context;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;

import io.vov.vitamio.Vitamio;
import movi.net.config.LocationConfig;
import movi.ui.bean.ChannelListBean;
import movi.ui.bean.CloudListBean;
import movi.ui.bean.LoginListBean;
import movi.net.config.ConfigInitial;
import movi.net.config.DbConfig;
import movi.ui.db.User;
import movi.ui.listenner.MyOnUpgradeListenner;

/**
 * Created by chjj on 2016/5/9.
 * application类  用于初始化相关参数
 */
public class MainApplication extends Application {
    //7bfb02ae9c5820251b50a634ba0b8845
    private static MainApplication mainApp;
    private static Context mContext;
    private static DbManager.DaoConfig daoConfig;
    public static User user;

    public LocationConfig  config;



    public LoginListBean.LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginListBean.LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public LoginListBean.LoginBean loginBean;

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String serverPath;
    public ChannelListBean.ChannelBean channelBean;

    public ChannelListBean.ChannelBean getChannelBean() {
        return channelBean;
    }

    public void setChannelBean(ChannelListBean.ChannelBean channelBean) {
        this.channelBean = channelBean;
        LogUtil.d("当前设置的channelBean为" + channelBean.getTitle());
    }

    public CloudListBean.CloudBean getCloudBean() {
        return cloudBean;
    }

    public void setCloudBean(CloudListBean.CloudBean cloudBean) {
        this.cloudBean = cloudBean;
    }

    public CloudListBean.CloudBean cloudBean;

    public static User getUser() {
        return user;
    }

    public static void setUser(User u) {
        user = u;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = getApplicationContext();
        Vitamio.isInitialized(getApplicationContext());
        ConfigInitial.initConfig(this);
//        config=new LocationConfig(mContext);


    }

    //初始化数据库
    private void initDbConfig() {
        daoConfig = new DbManager.DaoConfig()
                .setDbName(DbConfig.DB_NAME)//创建数据库的名称
                .setDbVersion(DbConfig.DB_VERSION)//数据库版本号
                .setDbUpgradeListener(new MyOnUpgradeListenner());//数据库更新操作
    }

    //返回数据库管理器
    public static DbManager getDbManager() {
        return x.getDb(daoConfig);
    }

    //返回当前application实例
    public static MainApplication getInstance() {
        if (mainApp == null) {
            mainApp = new MainApplication();
        }
        return mainApp;
    }

    //返回当前context实例
    public static Context getContext() {
        return mContext;
    }



}
