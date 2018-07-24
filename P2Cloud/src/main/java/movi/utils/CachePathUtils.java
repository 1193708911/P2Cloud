package movi.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.widget.ExpandableListView;

import org.xutils.common.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chjj on 2016/5/19.
 */
public class CachePathUtils {
    private Context context;
    public static final String MOBILE_GALERY_PATH = Environment.getExternalStorageDirectory() + "/p2ccloud" + "/";

    public static String compressBit(Bitmap bitmap, Context context) {
        File file = new File(MOBILE_GALERY_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File fullFile = new File(file, System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(fullFile);
            String fullFilePath = fullFile.getAbsolutePath();
            LogUtil.d("当前文件的路径为" + fullFile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
                if (options <= 0) {
                    break;
                }
            }
            outputStream.write(baos.toByteArray());
            return fullFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }


    public static String compressPicWH(String imgPath, float pixelW, float pixelH) {
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
            newOpts.inJustDecodeBounds = true;
            newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
            // Get bitmap info, but notice that bitmap is null now
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int h = newOpts.outHeight;
            // 想要缩放的目标尺寸
            float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
            float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;//be=1表示不缩放
            if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                be = (int) (newOpts.outWidth / ww);
            } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                be = (int) (newOpts.outHeight / hh);
            }
            if (be <= 0) be = 1;
            newOpts.inSampleSize = be;//设置缩放比例
            // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
            // 压缩好比例大小后再进行质量压缩
            File file = new File(MOBILE_GALERY_PATH);
            if (!file.exists()) {
                file.mkdir();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            File fullFile = new File(file, System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(fullFile);
            String fullFilePath = fullFile.getAbsolutePath();
            LogUtil.d("当前文件的路径为" + fullFile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            outputStream.write(baos.toByteArray());
            return fullFilePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
