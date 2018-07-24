package movi.lib.videocompress;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.crust87.ffmpegexecutor.FFmpegExecutor;
import com.crust87.videocropview.VideoCropView;
import com.crust87.videotrackview.VideoTrackView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;

/**
 * Created by ctivt on 2016/9/28.
 */
public class FFMPEGUtils {
    private VideoCropView mVideoCropView;
    private VideoTrackView mAnchorVideoTrackView;
    private AnchorOverlay mAnchorOverlay;
    private ProgressDialog mProgressDialog;
    private FFmpegExecutor mExecutor;
    private int mVideoSeek;
    private int mVideoDuration;
    private int mRatioWidth;
    private int mRatioHeight;
    private Context mContext;


    public FFMPEGUtils(Context mContext) {
        this.mContext = mContext;
        mRatioWidth = 1;
        mRatioHeight = 1;

        loadGUI();
        initFFmpeg();
        bindEvent();
    }


    private void loadGUI() {

        mVideoCropView = new VideoCropView(mContext);
        mAnchorVideoTrackView = new VideoTrackView(mContext);

        mAnchorOverlay = new AnchorOverlay(mContext.getApplicationContext());
        mAnchorVideoTrackView.setVideoTrackOverlay(mAnchorOverlay);
    }

    private void initFFmpeg() {
        try {
            InputStream ffmpegFileStream = mContext.getApplicationContext().getAssets().open("ffmpeg");
            mExecutor = new FFmpegExecutor(mContext.getApplicationContext(), ffmpegFileStream);
        } catch (Exception e) {
            Toast.makeText(mContext.getApplicationContext(), "Fail FFmpeg Setting", Toast.LENGTH_LONG).show();
        }
    }

    private void bindEvent() {
        mVideoCropView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoCropView.start();
            }
        });

        mExecutor.setOnReadProcessLineListener(new FFmpegExecutor.OnReadProcessLineListener() {
            @Override
            public void onReadProcessLine(String line) {
                Message message = Message.obtain();
                message.obj = line;
                message.setTarget(mMessageHandler);
                message.sendToTarget();
            }
        });

        mAnchorOverlay.setOnUpdateAnchorListener(new AnchorOverlay.OnUpdateAnchorListener() {
            @Override
            public void onUpdatePositionStart() {
                mVideoCropView.pause();
            }

            @Override
            public void onUpdatePosition(int seek, int duration) {
                mVideoSeek = seek;
                mVideoDuration = duration;
            }

            @Override
            public void onUpdatePositionEnd(int seek, int duration) {
                mVideoSeek = seek;
                mVideoDuration = duration;

                mVideoCropView.seekTo(mVideoSeek);
                mVideoCropView.start();
            }
        });
    }

    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String message = (String) msg.obj;
            if (mProgressDialog != null) {
                mProgressDialog.setMessage(message);
            }
        }
    };


    public void cropVideo(final String originalPath, final String outputPath) {
        mVideoCropView.pause();
        new AsyncTask<Void, Void, Void>() {

            float scale;
            int viewWidth;
            int viewHeight;
            int width;
            int height;
            int positionX;
            int positionY;
            int videoWidth;
            int videoHeight;
            int rotate;
            String start;
            String dur;

            @Override
            protected void onPreExecute() {
                mExecutor.init();
                mProgressDialog = ProgressDialog.show(mContext, null, "execute....", true);

                scale = mVideoCropView.getScale();
                viewWidth = mVideoCropView.getWidth();
                viewHeight = mVideoCropView.getHeight();
                width = (int) (viewWidth * scale);
                height = (int) (viewHeight * scale);
                positionX = (int) mVideoCropView.getRealPositionX();
                positionY = (int) mVideoCropView.getRealPositionY();
                videoWidth = mVideoCropView.getVideoWidth();
                videoHeight = mVideoCropView.getVideoHeight();
                rotate = mVideoCropView.getRotate();
                mVideoDuration = 7000;


                int startMinute = mVideoSeek / 60000;
                int startSeconds = mVideoSeek - startMinute * 60000;
                int durHour = mVideoDuration / (60000 * 60);
                int durMinute = (mVideoDuration - durHour * 60000 * 60) / 60000;
                int durSeconds = mVideoDuration - durHour * 60000 * 60 - durMinute * 60000;

                start = String.format("00:%02d:%02.2f", startMinute, startSeconds / 1000f);
                dur = String.format("%02d:%02d:%02.2f", durHour, durMinute, durSeconds / 1000f);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {//此顺序不能变，否则变得很慢
                   /* mExecutor.putCommand("-y")
                            .putCommand("-i")
                            .putCommand(originalPath)
                            .putCommand("-pix_fmt")//后加的
                            .putCommand("yuv420p")//后加的

                            .putCommand("-vcodec")
                            .putCommand("libx264")
                            .putCommand("-profile:v")
                            .putCommand("baseline")

                            .putCommand("-preset")//后加的
                            .putCommand("ultrafast")//后加的

//                            .putCommand("-b:v")
//                            .putCommand("4000k")
                                    //动态设置比特率
                            .putCommand("-b:v")
                            .putCommand(PreferencesUtil.getString("bite"))
                            .putCommand("-r")
                            .putCommand("25")
                            .putCommand("-f")
                            .putCommand("mp4")
                            .putCommand("-c:a")
                            .putCommand("copy")
                            .putCommand("-strict")
                            .putCommand("-2")
                            .putCommand(outputPath)
                            .executeCommand();*/


                    mExecutor.putCommand("-y")
                            .putCommand("-i")
                            .putCommand(originalPath)
                            .putCommand("-pix_fmt")//后加的
                            .putCommand("yuv420p")//后加的

                            .putCommand("-vcodec")
                            .putCommand("libx264")


                            .putCommand("-profile:v")
                            .putCommand("baseline")

                            .putCommand("-preset")//后加的
                            .putCommand("ultrafast")//后加的

//                            .putCommand("-b:v")
//                            .putCommand("4000k")
                            //动态设置比特率
                            .putCommand("-b:v")
                            .putCommand(PreferencesUtil.getString("bite"))
                            //没十帧增加一个关键帧
                            .putCommand("-x264opts")
                            .putCommand("keyint=1")
                            .putCommand("-r")
                            .putCommand("25")
                            .putCommand("-f")
                            .putCommand("mp4")
                            .putCommand("-c:a")
                            .putCommand("copy")
                            .putCommand("-strict")
                            .putCommand("-2")
                            .putCommand(outputPath)
                            .executeCommand();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
                ToastUtils.showToast(mContext, "转码完成");
//                deleteOriginalFile(originalPath);
                if (listener != null) {
                    listener.videoComplet();
                }
            }

        }.execute();
    }


    /**
     * 删除原有的
     * 视频文件
     */
    public void deleteOriginalFile(String originalPath) {

        if (originalPath != null) {
            File originalFile = new File(originalPath);
            if (originalFile.exists()) {
                originalFile.delete();
            }
        }

    }


    /**
     * 测试  提供一个接口用于视频的压缩后的回调
     */
    public interface OnVideoCompressCompletListener {

        public void videoComplet();
    }

    private OnVideoCompressCompletListener listener;

    public void setOnVideoCompletLitener(OnVideoCompressCompletListener listener) {

        this.listener = listener;
    }
}
