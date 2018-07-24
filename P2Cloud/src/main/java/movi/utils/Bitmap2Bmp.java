package movi.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Environment;

import org.xutils.common.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * bitmap转换为bmp格式的图片
 */
public class Bitmap2Bmp {

    private static String filename;



    /**
     * 保存相应的bmp文件
     * 以bmp结尾
     *
     * @param bitmap
     * @return
     */
    public static String saveBmpBitmap(Bitmap bitmap, String fileIconXmlName) {
        try {

            ByteArrayOutputStream outputstrem = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstrem);
            File file = new File(Environment.getExternalStorageDirectory(), fileIconXmlName + ".bmp");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            filename = file.getAbsolutePath();
            LogUtil.d("-----------当前的保存bmp图片地址为" + file.getAbsolutePath());
            FileOutputStream fileos = new FileOutputStream(filename);
            fileos.write(outputstrem.toByteArray());
            fileos.flush();
            return filename;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
