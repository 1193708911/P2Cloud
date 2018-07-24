package movi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import org.xutils.common.util.LogUtil;

import java.io.File;


/**
 * Created by chjj on 2016/5/26.
 */
public class IntentUtils {
    //发起广播通知更改图库状态
    public  static void sendBroadCastReceiver(Context context) {
        Intent intent = new Intent();
        // 重新挂载的动作
        intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
        // 要重新挂载的路径
        intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
        context.sendBroadcast(intent);
    }


    /**
     * 选择相机
     *//*
    public static  void showCamera(Activity context,File mTmpFile ,int TAKE_PHOTO) {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = OtherUtils.createFile(context);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            context.startActivityForResult(cameraIntent, TAKE_PHOTO);
        } else {
            Toast.makeText(context,
                    R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }

    }
*/

    /**
     * 跳转自动更新
     */
    public static  void openFile(Context context, File file) {
        // TODO Auto-generated method stub
        LogUtil.e("OpenFile=" + file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);

    }
}
