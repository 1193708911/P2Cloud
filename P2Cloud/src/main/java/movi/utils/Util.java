package movi.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import movi.MainApplication;

public class Util {

    public final static File SD_CARD_PATH = Environment.getExternalStorageDirectory();

    public static boolean isBlank(CharSequence str) {
        if (str == null || str.toString().trim().length() == 0)
            return true;
        else
            return false;
    }

    public static boolean isEmpty(Collection<?> c) {
        if (c == null || c.size() == 0)
            return true;
        else
            return false;
    }

    /**
     * SD card path
     */

    public static String querySDCardPath() {
        File file = SD_CARD_PATH;
        return file.getAbsolutePath();
    }

    /**
     * Check whether there is SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMobile(String str) {
        if (str == null)
            return false;
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static boolean isTelephone(String str) {
        if (str == null)
            return false;
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^\\d{3}-?\\d{8}|\\d{4}-?\\d{8}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 检测邮箱地址是否合法
     *
     * @param email
     * @return true合法 false不合法
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email))
            return false;
        //        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static void showToast(int resId) {
        Toast.makeText(MainApplication.getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(CharSequence text) {
        Toast.makeText(MainApplication.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog showProgressDialog(Context context) {
        return showProgressDialog(context, true);
    }

    //检测当前有无可用网络
    public static boolean isNetworkConnected() {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            Context context = MainApplication.getInstance();
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static ProgressDialog showProgressDialog(Context context, boolean cancelable) {
        ProgressDialog pd = ProgressDialog.show(context, null, "数据拼命加载中，请耐心等待...", true, cancelable);
        if (!isNetworkConnected()) {
            Util.showToast("网络连接失败，请检查网络连接！");
            pd.dismiss();
        }
        return pd;
    }


    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getAppVersion() {
        try {
            PackageManager manager = MainApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MainApplication.getInstance().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static long parseLong(String num) {
        try {
            return Long.parseLong(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static double parseDouble(String num) {
        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void hideSoftInput(Activity act) {
        try {
            ((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(act
                    .getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static Calendar clearCalendarHMSS(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }

    /**
     * 获取加密后的字符串
     *
     * @param
     * @return
     */
    @SuppressLint("NewApi")
    public static String stringMD5(String pw) {
        try {

            // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = pw.getBytes(Charset.forName("UTF-8"));
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {

        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'9', '8', '7', '6', '5', '4', '3', '2', '1', '0', 'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }


    public boolean isEmpty(EditText editText, String msg) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            showToast(msg);
            return false;
        }
        return true;

    }

    /**
     * 过滤特殊字符
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String filterString(Context context, String str) throws PatternSyntaxException {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.matches()) {
            ToastUtils.showToast1(context, "不能输入特殊符号").show();
        }
        return m.replaceAll("").trim();
    }


}
