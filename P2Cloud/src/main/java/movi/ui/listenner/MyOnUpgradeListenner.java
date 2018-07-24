package movi.ui.listenner;


import org.xutils.DbManager;

import movi.ui.db.GaleryPicBean;
import movi.ui.db.User;

/**
 * 数据库更新监听器
 */
public class MyOnUpgradeListenner implements DbManager.DbUpgradeListener {


	@Override
	public void onUpgrade(DbManager manage, int arg1, int arg2) {
		// TODO Auto-generated method stub
		try {
			manage.dropTable(User.class);
			manage.dropTable(GaleryPicBean.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
