package movi.utils;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import movi.MainApplication;


/**
 * Created by chjj on 2016/5/10.
 * 判断当前的EditText是否为空
 */
public class MyTextUtils {
    public static boolean isEmpty(EditText et, String text) {
        if (TextUtils.isEmpty(et.getText().toString().trim())) {
            Toast.makeText(MainApplication.getContext(), text, Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }

    }
}
