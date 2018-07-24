package movi.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctvit.p2cloud.R;
import com.tikou.mylibrary.UiSeeKBar;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import movi.base.BaseActivity;
import movi.ui.bean.NewsPicListBean;
import movi.utils.AppManager;
import movi.utils.CommonUtils;
import movi.utils.TimeUtils;
import movi.utils.ToastUtils;

/**
 * Created by chjj on 2016/6/17.
 */
@ContentView(R.layout.activity_fr_video)
public class VideoFragmentActivity1 extends BaseActivity {
    @ViewInject(R.id.txtNewsStatus)
    private TextView txtNewsStatus;
    @ViewInject(R.id.s_view)
    private VideoView videoView;
    @ViewInject(R.id.sb)
    private UiSeeKBar sb;
    @ViewInject(R.id.imgPlays)
    private ImageButton imgPlay;
    @ViewInject(R.id.imgReset)
    private ImageButton imgReset;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
//    @ViewInject(R.id.txtNewsTimeLeanth)
//    private TextView txtNewsTimeLeanth;
    private String path = "";
    private static final int UPDATAPROGRESS = 1;
    private static final String TAG = "VideoFragment1";
    private NewsPicListBean.NewsPicBean picBean;
    //声明一个boolean类型的值判断一下是否已经播放完毕
    private boolean isComplet = true;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATAPROGRESS) {
                //转换相应的时间
                sb.setProgress((int) videoView.getCurrentPosition());
            }
            handler.removeMessages(UPDATAPROGRESS);
            handler.sendEmptyMessage(UPDATAPROGRESS);
        }
    };

    public void setPicBean(NewsPicListBean.NewsPicBean picBean) {
        this.picBean = picBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
//        initBundle();

        //初始化并播放
        initAndPlay();

        //设置所有监听
        setListener();

    }

    //绑定相关数据显示状态
    public void setData() {
        if (picBean == null) {
            return;
        }
        txtNewsStatus.clearComposingText();
        txtNewsStatus.setText("");
        if (picBean.getClip_status() != null) {
            for (int i = 0; i < picBean.getClip_status().size(); i++) {
                txtNewsStatus.append(CommonUtils.checkStateColor(String.valueOf(picBean.getClip_status().get(i))));
                txtNewsStatus.append("-");
            }
//            txtNewsStatus.append(CommonUtils.checkStateColor(picBean.getClip_status_end()));
        }


    }

    /**
     * 设置所有监听
     */
    private void setListener() {
        videoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                Log.d(TAG, "onSeekComplete() called with: " + "mp = [" + mp + "]");
            }
        });


        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.d("当前what值", what + "");
                if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    pause();
                    pg_iv.setVisibility(View.VISIBLE);
                    handler.removeMessages(UPDATAPROGRESS);
                } else if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    mp.start();
                    pg_iv.setVisibility(View.GONE);
                    handler.removeMessages(UPDATAPROGRESS);
                    handler.sendEmptyMessage(UPDATAPROGRESS);
                    imgPlay.setImageResource(R.mipmap.imb_play);
                }
                return true;
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(isComplet){
                        start();
                        isComplet=false;
                    }
                    videoView.seekTo(sb.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });


        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放
                if (videoView.isPlaying()) {
                    isComplet = false;
                    pause();
                } else {
                    if (isComplet) {
                        reStart();
                    } else {
                        start();
                    }
                    isComplet = false;

                }
            }
        });

        imgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新播放
                reStart();
            }
        });

    }

    /**
     * 初始化并播放
     */
    private void initAndPlay() {
        //第一次和重新播放
        reStart();
    }

    private void reStart() {
        LogUtil.d("当前的  ----------------- 视频  播放路径=" + path);
        if (path.equals("0")) {
            ToastUtils.showToast(this, "当前无视频");
            return;
        }
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isComplet = true;
//                videoView.stopPlayback();
                imgPlay.setImageResource(R.mipmap.imb_play_normal);

            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setPlaybackSpeed(1.0f);
                int duration = (int) videoView.getDuration();
                sb.setMax(duration);
                String timeAllLeanth = TimeUtils.formatDuration(duration / 40);
//                txtNewsTimeLeanth.setText("总时长:" + timeAllLeanth);
                handler.sendEmptyMessage(UPDATAPROGRESS);
            }
        });

    }


    /**
     * 开始
     */
    private void start() {
        imgPlay.setImageResource(R.mipmap.imb_play);
        videoView.start();
        handler.removeMessages(UPDATAPROGRESS);
        handler.sendEmptyMessage(UPDATAPROGRESS);
    }

    /**
     * 暂停
     */
    private void pause() {
        imgPlay.setImageResource(R.mipmap.imb_play_normal);
        videoView.pause();
        handler.removeMessages(UPDATAPROGRESS);
    }

    private void seekTo(long position) {
        videoView.seekTo(position);
    }

    /**
     * seek到相应的位置同时暂停
     *
     * @param position
     */
    public void setMediaPosition(int position) {
        pause();
        seekTo(position);
    }


   /* *//**
     * 初始化当前的数据
     *//*
    private void initBundle() {
        picBean = (NewsPicListBean.NewsPicBean) getIntent().getSerializableExtra("bean");
        if (picBean != null) {
            this.path = picBean.getMp4();
            setData();
          *//*  if (path.equals("0")) {
                imgPlay.setClickable(false);
                imgReset.setClickable(false);
                imgPlay.setEnabled(false);
                imgReset.setEnabled(false);
            } else {
                imgPlay.setClickable(true);
                imgReset.setClickable(true);
                imgPlay.setEnabled(true);
                imgReset.setEnabled(true);

            }*//*
            LogUtil.d("当前的path路径为" + path);
        }
    }*/

    @Event(R.id.txtBack)
    private void onClick(View view) {
        this.finish();
    }


    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();

    }
}
