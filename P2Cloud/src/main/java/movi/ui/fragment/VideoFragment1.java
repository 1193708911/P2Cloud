package movi.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import movi.base.BaseFragment;
import movi.ui.bean.NewsPicListBean;

/**
 * Created by chjj on 2016/6/17.
 */
@ContentView(R.layout.fr_video)
public class VideoFragment1 extends BaseFragment
//        implements
//        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener
{

    @ViewInject(R.id.txtNewsStatus)
    private TextView txtNewsStatus;
    @ViewInject(R.id.s_view)
    private VideoView videoView;
    @ViewInject(R.id.sb)
    private SeekBar sb;
    @ViewInject(R.id.imgPlays)
    private ImageButton imgPlay;
    @ViewInject(R.id.imgReset)
    private ImageButton imgReset;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
    private String path = "http://123.56.6.125/data/masongsong/P2PROXY/CONTENTS/PREVIEW/082IY3EO.MP4";
    private int first = 1;
    private boolean isPaus = true;
    private int position = 0;
    private static final int UPDATAPROGRESS = 1;
    private static final String TAG = "VideoFragment1";
    private NewsPicListBean.NewsPicBean picBean;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATAPROGRESS) {
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
    protected int getResource() {
        return R.layout.fr_video;
    }

    @Override
    protected void afterViews() {
        super.afterViews();

        //初始化数据
//        initBundle();

        //初始化并播放
        initAndPlay();

        //设置所有监听
        setListener();

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
                if(fromUser) {
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
                if(videoView.isPlaying()) {
                    pause();
                }else {
                    start();
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

    /**
     * 第一次播放和重新播放
     */
    private void reStart() {

        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgPlay.setImageResource(R.mipmap.imb_play_normal);
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                imgPlay.setImageResource(R.mipmap.imb_play);
                mp.setPlaybackSpeed(1.0f);
                int duration =  (int) videoView.getDuration();
                sb.setMax(duration);
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


























    //绑定相关数据显示状态
    public void setData() {
//        if (picBean == null) {
//            return;
//        }
//        txtNewsStatus.clearComposingText();
//        txtNewsStatus.setText("");
//        for (int i = 0; i < picBean.getClip_status().size(); i++) {
//            txtNewsStatus.append(CommonUtils.checkStateColor(String.valueOf(picBean.getClip_status().get(i))));
//            txtNewsStatus.append("-");
//        }
//        txtNewsStatus.append(CommonUtils.checkStateColor(picBean.getClip_status_end()));

    }


   /* *//**
     * 初始化当前的数据
     *//*
    private void initBundle() {
        if (picBean != null) {
            this.path = picBean.getMp4();
            setData();
            if (path.equals("0")) {
                imgPlay.setClickable(false);
                imgReset.setClickable(false);
                imgPlay.setEnabled(false);
                imgReset.setEnabled(false);
            } else {
                imgPlay.setClickable(true);
                imgReset.setClickable(true);
                imgPlay.setEnabled(true);
                imgReset.setEnabled(true);
            }
            LogUtil.d("当前的path路径为" + path);
        }
    }*/



 /*   @Event(value = {R.id.txtBack, R.id.imgPlays, R.id.imgReset})
    private void onClick(View view) {
//        initBundle();
        switch (view.getId()) {
            case R.id.txtBack:

                break;
            case R.id.imgPlay:
//                if (isPaus) {
//                    if (first == 1) {
//                        pg_iv.setVisibility(View.VISIBLE);
//                        position = 0;
//                        playUrl(path, 0);
//                        first++;
//                    } else {
//                        //不是第一次
//                        play();
//                    }
//                    imgPlay.setImageResource(R.mipmap.imb_play);
//                    isPaus = false;
//                } else {
//                    if (mVideoView.isPlaying()) {
//                        mVideoView.pause();
//                        isPaus = true;
//                        imgPlay.setImageResource(R.mipmap.imb_play_normal);
//                    }
//
//                }
                playUrl();
//                playUrl(null,0);
                break;
            case R.id.imgReset:
//                pg_iv.setVisibility(View.VISIBLE);
//                if (mVideoView.isPlaying()) {
//                    mVideoView.seekTo(0);
//                } else {
//                    if (first == 1) {
//                        playUrl(path, 0);
//                    } else {
//                        mVideoView.seekTo(0);
//                    }
//                    pg_iv.setVisibility(View.VISIBLE);
//                }
//                imgPlay.setImageResource(R.mipmap.imb_play);
//                first++;
//                isPaus = false;
//                pg_iv.setVisibility(View.GONE);
                break;
        }

    }
*/


    //播放url第一次
   /* public void playUrl(String url, final int position) {






        mVideoView.setVideoPath("http://123.56.6.125/data/masongsong/P2PROXY/CONTENTS/PREVIEW/082IY3EO.MP4");
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mVideoView.start();
                mp.setPlaybackSpeed(1.0f);

//                mVideoView.setTimedTextShown(true);

//                int duration = (int) mVideoView.getDuration();
//                sb.setMax(duration);
//                pg_iv.setVisibility(View.GONE);
//                if (position > 0) {
//                    mVideoView.seekTo(position);
//                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        while (true) {
//                            sb.setProgress((int) mVideoView.getCurrentPosition());
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
        });


    }
*/
    ;


    /**
     * 不是第一次播放
     */


//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        isPaus = true;
//        imgPlay.setImageResource(R.mipmap.imb_play_normal);
//        return false;
//
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        isPaus = true;
//        imgPlay.setImageResource(R.mipmap.imb_play_normal);
//
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (fromUser) {
//            mVideoView.seekTo(progress);
//        }
//
//
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//
//
//    }
}
