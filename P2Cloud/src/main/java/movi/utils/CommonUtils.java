package movi.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import org.xutils.common.util.LogUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import movi.MainApplication;

/**
 * Created by chjj on 2016/5/27.
 */
public class CommonUtils {
    public static String HOME_ACTION = "com.civit.p2cloud.homeactivity";

    /**
     * 检查当前网络是否可用
     *
     * @param
     * @return
     */
    public static boolean isNetworkAvailable(Context ContextObj) {
        ConnectivityManager ConnMgr = (ConnectivityManager) ContextObj
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NetInfo = ConnMgr.getActiveNetworkInfo();

        if (NetInfo == null || !NetInfo.isConnected()) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前的文件状态
     * 获取相应的颜色
     *
     * @return
     */
    public static Spanned checkStateColor(String state) {
        Spanned spanned;
        if (state.equals("入库失败")) {
            spanned = fromHtml("#FF0000", state);
        } else if (state.equals("合成失败")) {
            spanned = fromHtml("#FF0000", state);
        } else if (state.equals("发布失败")) {
            spanned = fromHtml("#FF0000", state);
        } else if (state.equals("无状态")) {
            spanned = fromHtml("#FF0000", state);
        } else if (state.equals("失败")) {
            spanned = fromHtml("#FF0000", state);
        } else if (state.equals("成功")) {
            spanned = fromHtml("#DB100", state);
        }else {
            spanned = fromHtml("#DB100", state);
        }
        return spanned;
    }



    /**
     * 字符串格式化颜色
     */
    // StringtextStr1 = "<font color=\"#ffff00\">如果有一天，</font><br>";
    public static Spanned fromHtml(String color, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("<font color=").append(color).append(">").append(content).append("</font>");
        LogUtil.d(builder.toString());
        return Html.fromHtml(builder.toString());
    }


    /**
     * 获取加密后的字符
     *
     * @param
     * @return
     */
    public static String stringMD5(String pw) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = pw.getBytes();
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    /**
     * 获取系统版本号
     */


    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            LogUtil.e("VersionInfo" + "Exception", e);
        }
        return versionName;
    }

    /**
     * 随机生成一个六位字符的
     * 随机字符串
     */
    public static String getRandomStr() {
        int MAX = 8;
        String a = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MAX; i++) {
            int rand = (int) (Math.random() * a.length());
            builder.append(a.charAt(rand));
        }
        return builder.toString();
    }

    /**
     * 随机生成一个六位字符的
     * 随机字符串
     */
    public static String get64RandomStr() {
        int MAX = 64;
        String a = "0123456789qwertyuiopasdfghjklzxcvbnm";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < MAX; i++) {
            int rand = (int) (Math.random() * a.length());
            builder.append(a.charAt(rand));
        }
        return builder.toString();
    }

    /**
     * 获取设备id
     */
    public static String getDeviceId(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = TelephonyMgr.getDeviceId();
        return deviceId;
    }


    /**
     * 判断相应的版本号是不是相等
     */
    public static boolean isVersionEquals(String versionName, String netVersionName) {
        if (null != versionName && null != netVersionName) {
            String[] versonNames = versionName.split("\\.");
            String[] netVersionNames = netVersionName.split("\\.");
            String currentVersionName = "";
            String currentNetVersionNames = "";
            for (String v : versonNames) {
                currentVersionName += v;
            }
            for (String nv : netVersionNames) {
                currentNetVersionNames += nv;
            }
            if ((Integer.valueOf(currentVersionName) < Integer.valueOf(currentNetVersionNames))) {
                return true;

            }

        }
        return false;
    }


    /**
     * 阻止button按钮重复点击提交
     */
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
