package movi.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;
/**
 * Created by chjj on 2016/5/11.
 */
public class DenityUtils {
    private Context context;
    private DisplayMetrics metrics;
    public  DenityUtils(){};

    public DenityUtils(Context context) {
        this.context = context;
        initWindowParam(context);
    }

    //获取屏幕的宽
    public int getWidth() {
        return metrics.widthPixels;
    }

    //获取屏幕的高
    public int getHeight() {
        return metrics.heightPixels;
    }

    //初始化
    private void initWindowParam(Context context) {
        WindowManager manage = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        metrics = new DisplayMetrics();
        manage.getDefaultDisplay().getMetrics(metrics);

    }


    /**
     * Converts the given amount of pixels to a dp value.
     * @param pixels The pixel-based measurement
     * @return The measurement's value in dp, based on the device's screen density
     */
    public static float pxToDp(float pixels) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return pixels / (metrics.densityDpi / 160f);
    }

    /**
     * Converts the given dp measurement to pixels.
     * @param dp The measurement, in dp
     * @return The corresponding amount of pixels based on the device's screen density
     */
    public static float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }
}
