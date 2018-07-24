package movi.net.config;

import org.xutils.x;

import movi.MainApplication;
import movi.utils.PreferencesUtil;

/**
 * Created by admin on 2017/3/9.
 */

public class ConfigInitial {


    public   static   void   initConfig(MainApplication  instance){
        initPreFer();
        initXutils(instance);
    }
    //初始化录像机录制参数
    public  static  void initPreFer() {
        //每次开启只开启一次
        if (PreferencesUtil.getInt("FBL") == -1) {
            PreferencesUtil.putInt("FBL", 480);
        }
        if (PreferencesUtil.getInt("UL") == -1) {
            PreferencesUtil.putInt("UL", 2);
        }
        if (PreferencesUtil.getString("bite") == null) {
            PreferencesUtil.putString("bite", "2500k");
        }

    }
    //初始化Xutils+
    private static void initXutils(MainApplication  instance) {
        x.Ext.init(instance);
        x.Ext.setDebug(true);
    }

}
