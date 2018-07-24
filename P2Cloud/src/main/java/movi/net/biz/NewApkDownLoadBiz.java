package movi.net.biz;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.ctvit.p2cloud.R;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.math.BigDecimal;

import movi.utils.InstallUtils;

/**
 * Created by chjj on 2016/7/13.
 */
public class NewApkDownLoadBiz {
    static NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;

    /**
     * 下载工具类
     *
     * @param url
     * @param path
     */
    public static void DownLoad(String url, final Context context) {


        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("版本更新")
                .setContentText("正在下载...")
                .setContentInfo("0%")
                .setSmallIcon(R.mipmap.logle);


        RequestParams params = new RequestParams(url);
        //设置断点续传
        params.setAutoResume(true);
        x.http().get(params, new Callback.ProgressCallback<File>() {


            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                Toast.makeText(x.app(), "开始下载", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                BigDecimal b = new BigDecimal((float) current / (float) total);
                float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                mBuilder.setProgress(100, (int) (f1 * 100), false);
                mBuilder.setContentInfo((int) (f1 * 100) + "%");
                LogUtil.d("--------------下载进度" + current + "/" + total);
                mNotifyManager.notify(1, mBuilder.build());
            }

            @Override
            public void onSuccess(File result) {
                mBuilder.setContentText("正在下载...")
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(1, mBuilder.build());
                mNotifyManager.cancel(1);
                Toast.makeText(x.app(), "下载成功", Toast.LENGTH_LONG).show();
                InstallUtils.installApp(context, result.getAbsolutePath());
                LogUtil.d("---------------------" + "下载成功" + result.getAbsolutePath());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                LogUtil.d("---------------下载完成");

            }
        });
    }
}
