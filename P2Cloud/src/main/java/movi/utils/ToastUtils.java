package movi.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import movi.MainApplication;

/**
 * Created by Administrator on 2016/3/8.
 */
public class ToastUtils {

    private static Toast toast;
    private Context context;
    public static void showToast(Context context,String string) {
        if (toast ==null) {
           toast = Toast.makeText(MainApplication.getContext(),string,Toast.LENGTH_SHORT);
        }
        toast.setText(string);
        toast.show();
    }

    public static Toast showToast1(Context context,String string) {
        if (toast ==null) {
            toast = Toast.makeText(MainApplication.getContext(),string,Toast.LENGTH_SHORT);
        }
        toast.setText(string);
        toast.setGravity(Gravity.CENTER,0,0);
        return toast;
    }

}
