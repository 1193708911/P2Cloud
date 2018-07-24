package movi.utils;


import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;

import movi.MainApplication;
import movi.ui.bean.LoginListBean;
import movi.ui.db.GaleryPicBean;
import movi.ui.db.User;

/**
 * Created by ZHXG on 2016/3/31.
 * 数据库处理工具类
 */
public class DbUtils {
    //把文件路径保存到数据库
    public static void savePicFIlePath(String fileName) {
        try {
            DbManager  manager=MainApplication.getDbManager();
            GaleryPicBean  picBean=new GaleryPicBean();
            picBean.setPath(fileName);
            manager.saveBindingId(picBean);
            LogUtil.d("保存文件成功");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserInfo(LoginListBean.LoginBean login) {
        try {
        User user=new User();
        user.setMember_id(login.getMember_id());
        user.setCompany_id(login.getCompany_id());
        user.setCompany_name(login.getCompany_name());
        user.setFamily_name(login.getFamily_name());
        user.setGive_name(login.getGive_name());
        user.setGroup_id(login.getGroup_id());
        user.setPassword(login.getPassword());
        user.setRequestTraceId(login.getRequestTraceId());
        user.setSessionTraceId(login.getSessionTraceId());
        user.setTimezone(login.getTimezone());
        user.setUsername(login.getUsername());
        MainApplication.getDbManager().saveOrUpdate(user);
        MainApplication.setUser(user);
            LogUtil.d("数据保存成功"+user.getUsername());
        } catch (DbException e) {
            e.printStackTrace();
        }

    }
}
