package movi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;

import org.xutils.common.util.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static movi.utils.CachePathUtils.MOBILE_GALERY_PATH;

public class BitmapUtils {
    //等比例压缩
    public static Bitmap getCompressBitmap(Bitmap image, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = image.getWidth();
        int height = image.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片   www.2cto.com
        Bitmap newbm = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
        return newbm;
    }


    public static String compressBitmapToString(Bitmap bitmap) {
        try {
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
