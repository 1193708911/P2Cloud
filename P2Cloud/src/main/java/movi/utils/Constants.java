package movi.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

/**
 * 时间转化格式的应用
 * @author Administrator
 */
@SuppressLint("SimpleDateFormat")
public class Constants {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_FORMAT_S = new SimpleDateFormat("yy-MM-dd");
	public static final SimpleDateFormat DATE_FORMAT_CN = new SimpleDateFormat("yyyy年MM月dd日");
	public static final SimpleDateFormat DATE_FORMAT_MD_CN = new SimpleDateFormat("MM月dd日");
	public static final SimpleDateFormat DATE_FORMAT_E = new SimpleDateFormat("yyyy/MM/dd E");
	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat DATE_TIME_FORMAT_YMDHM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat DATE_TIME_FORMAT_CN = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	public static final SimpleDateFormat DATE_TIME_FORMAT_YMDHM_CN = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
	public static final SimpleDateFormat DATE_TIME_FORMAT_MDHM = new SimpleDateFormat("MM月dd日 HH:mm");
	public static final SimpleDateFormat DATE_TIME_FORMAT_MDHM2 = new SimpleDateFormat("MM月dd日 HH时mm分");
	public static final SimpleDateFormat TIME_FORMAT_HM = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat TIME_FORMAT_HMA = new SimpleDateFormat("h:mm a");
	public static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("dd");

}
