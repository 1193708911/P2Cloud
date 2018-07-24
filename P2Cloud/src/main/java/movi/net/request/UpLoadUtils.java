package movi.net.request;

import android.util.Log;

import com.jcraft.jsch.SftpProgressMonitor;

import org.xutils.common.util.LogUtil;

import movi.MainApplication;
import movi.base.BaseActivity;

/**
 * Created by chjj on 2016/8/5.
 */
public class UpLoadUtils implements SFTPUtils.OnFileSizeMaxListenner {
    private final String TAG = "UpLoadService";
    public SFTPUtils sftp;
    private long fileMaxSize;
    private String videoPath;

    /**
     * 初始化
     *
     * @param videoPath
     */
    public void initSftp(String videoPath) {
        this.videoPath = videoPath;
        //获取控件对象
        BaseActivity.REMOTE_SERVER_PATH = MainApplication.getInstance().getChannelBean().getSftpip();
        BaseActivity.USER_NAME = MainApplication.getInstance().getUser().getUsername();
        BaseActivity.USER_PASSWORD = MainApplication.getInstance().getUser().getPassword();
        LogUtil.d("aaaaaaaaaaaaaa" + BaseActivity.USER_NAME);
        LogUtil.d("aaaaaaaaaaaaaa" + BaseActivity.USER_PASSWORD);
        LogUtil.d("aaaaaaaaaaaaaa" + BaseActivity.REMOTE_SERVER_PATH);
//        sftp = new SFTPUtils(RemotePathConfigs.REMOTE_SERVER_PATH, RemotePathConfigs.USER_NAME, RemotePathConfigs.USER_PASSWORD);
        //测试
        sftp = new SFTPUtils(BaseActivity.REMOTE_SERVER_PATH, BaseActivity.USER_NAME, BaseActivity.USER_PASSWORD);
        sftp.setOnFileSizeMaxListenner(this);
    }


    public void upLoad(final String remotePath, final String remoteFileName,
                       final String localPath, final String localFileName, final SftpProgressMonitor listener) {
        // TODO Auto-generated method stub
        new Thread() {
            @Override
            public void run() {
                //这里写入子线程需要做的工作
                try {
                    Log.e(TAG, "上传文件");
                    if (sftp != null) {
                        sftp.connect();
                        Log.e(TAG, "连接成功");
                        boolean isUpLoadSuccess = sftp.uploadFile(remotePath, remoteFileName,
                                localPath, localFileName, listener);
                        Log.e(TAG, "上传成功");
                        sftp.disconnect();
                        Log.e(TAG, "断开连接");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.start();

    }

    @Override
    public void onFileMax(long fileSize) {
        this.fileMaxSize = fileSize;
        if (listener != null) {
            listener.onFileMax((int) fileMaxSize);
        }

    }

    /**
     * 提供接口获取文件大小
     */
    public interface OnFileMaxListener {
        void onFileMax(long filesize);
    }

    private OnFileMaxListener listener;

    public void setOnFileMaxListener(OnFileMaxListener listener) {
        this.listener = listener;
    }
}
