package movi.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

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
public class VideoFragmentActivity4 extends BaseActivity implements View.OnTouchListener {
    @ViewInject(R.id.txtNewsStatus)
    private TextView txtNewsStatus;
    @ViewInject(R.id.s_view)
    private VideoView videoView;
    @ViewInject(R.id.sb)
    private SeekBar sb;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
    @ViewInject(R.id.btn_start_pause)
    private ImageButton btn_start_pause;
    @ViewInject(R.id.txtStart)
    private TextView txtStart;
    @ViewInject(R.id.txtDuration)
    private TextView txtDuration;
    private String path = "";
    private static final String TAG = "VideoFragment1";
    private NewsPicListBean.NewsPicBean picBean;
    //声明一个boolean类型的值判断一下是否已经播放完毕
    private boolean isComplet = true;

    private boolean isShow = true;

    private boolean isNoSchedule = true;

    private static final int DELAY_TIME = 5000;
    Handler videoHandler = new Handler();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //转换相应的时间
            int currentPosition = (int) videoView.getCurrentPosition();
            String time = TimeUtils.formatDuration(currentPosition / 40);
            sb.setProgress((int) currentPosition);
            txtStart.setText(time);

        }
    };


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

   /* *//**
     * 初始化当前的数据
     *//*
    private void initBundle() {
        picBean = (NewsPicListBean.NewsPicBean) getIntent().getSerializableExtra("bean");
        if (picBean != null) {
            this.path = picBean.getMp4();
            setData();
            if (path.equals("0")) {
                btn_start_pause.setClickable(false);
                btn_start_pause.setEnabled(false);
            } else {
                btn_start_pause.setClickable(true);
                btn_start_pause.setEnabled(true);

            }
        }
    }*/

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
                } else if (what == android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    mp.start();
                    pg_iv.setVisibility(View.GONE);
                    btn_start_pause.setImageResource(R.mipmap.imb_play);
                }
                return true;
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeMessages(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (isComplet) {
//                    start();
//                    isComplet = false;
//                }
//                videoView.seekTo(sb.getProgress());
                handler.sendEmptyMessage(1);
            }
        });

        btn_start_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (isComplet) {
                    isComplet = false;
                    reStart();
                } else {
                    if (!videoView.isPlaying()) {
                        playVideo();
                    } else {
                        pauseVideo();
                    }
                }
            }
        });
        videoView.setOnTouchListener(this);


    }

    /**
     * 初始化并播放
     */
    private void initAndPlay() {
        //第一次和重新播放
        reStart();
    }


    //播放视频
    private void playVideo() {
        videoView.start();
    }

    private void removeHandlerDelay() {
        isNoSchedule = false;
        videoHandler.postDelayed(MyTask, DELAY_TIME);
        btn_start_pause.setImageResource(R.mipmap.imb_play);

    }

    /**
     * 暂停或者停止
     */
    private void pauseVideo() {
        removePauseHandler();
        isNoSchedule = true;
    }

    /**
     * 移除定时任务
     */
    private void removePauseHandler() {
        videoHandler.removeCallbacks(MyTask);
        videoView.pause();
        btn_start_pause.setImageResource(R.mipmap.imb_play_normal);
        btn_start_pause.setVisibility(View.VISIBLE);
        videoView.setOnTouchListener(null);
        isShow = true;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isShow) {
                    btn_start_pause.setVisibility(View.GONE);
                    videoHandler.removeCallbacks(MyTask);
                    isShow = false;

                } else {
                    btn_start_pause.setVisibility(View.VISIBLE);
                    videoHandler.postDelayed(MyTask, DELAY_TIME);
                    isShow = true;
                }
                break;

        }

        return true;
    }

    //定时任务
    Runnable MyTask = new Runnable() {

        @Override
        public void run() {

            btn_start_pause.setVisibility(View.GONE);
            isNoSchedule = true;
            isShow = false;
            videoHandler.removeCallbacks(MyTask);


        }

    };
    private boolean isPlaying = false;

    private void reStart() {
        if (path.equals("0")) {
            ToastUtils.showToast(this, "视频地址无效");
            return;
        }
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isComplet = true;
                removePauseHandler();
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setPlaybackSpeed(1.0f);
                int duration = (int) videoView.getDuration();
                sb.setMax(duration);
                String timeAllLeanth = TimeUtils.formatDuration(duration / 40);
                txtDuration.setText(timeAllLeanth);
                removeHandlerDelay();
                isComplet = false;
                isPlaying = true;
                new Thread() {
                    public void run() {
                        isPlaying = true;
                        while (isPlaying) {

                            handler.sendEmptyMessage(1);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }

                    ;

                }.start();

            }
        });
        videoView.setOnTouchListener(VideoFragmentActivity4.this);
    }


    /**
     * 开始
     */
    private void start() {


    }

    /**
     * 暂停
     */
    private void pause() {
        btn_start_pause.setImageResource(R.mipmap.imb_play_normal);
        videoView.pause();
    }


    @Event({R.id.txtBack, R.id.btn_start_pause})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtBack:
                this.finish();
                break;
            case R.id.btn_start_pause:
                if (isComplet) {
                    isComplet = false;
                    reStart();
                } else {
                    if (!videoView.isPlaying()) {
                        isNoSchedule = false;
                        btn_start_pause.setImageResource(R.mipmap.imb_play);
                        videoView.start();
                        videoView.setOnTouchListener(VideoFragmentActivity4.this);
                    } else {
                        isNoSchedule = true;
                        btn_start_pause.setImageResource(R.mipmap.imb_play_normal);
                        videoView.pause();
                        videoView.setOnTouchListener(null);

                    }
                }
                break;

        }
    }


    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();

    }
}
