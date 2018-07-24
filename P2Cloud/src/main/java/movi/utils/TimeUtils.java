package movi.utils;

import org.xutils.common.util.LogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import movi.ui.bean.NewsSheetMovieListBean;


/**
 * 计算  字符串格式的算法
 */
public class TimeUtils {
    private static final long TIME_MILLS = 40;
    private static String[] duration;

    public static String format(long time) {
        return String.format("%02d", time);
    }

    public static int getVideoIndex(ArrayList<NewsSheetMovieListBean.NewsMovieBean> list, long currentPosition) {
        int index = 0;
        long currentDuration = 0;
        for (int i = 0; i < list.size(); i++) {
            long end = Long.parseLong(list.get(i).getOut_position());
            long start = Long.parseLong(list.get(i).getIn_position());
            long duration = end - start;
            currentDuration = currentDuration + duration;
            if (currentPosition <= currentDuration * 40) {
                return i;
            }

        }
        return 0;
    }

    /**
     * 获取几段视频的总的帧数
     *
     * @param list
     * @return
     */
    public static long getDuration(ArrayList<NewsSheetMovieListBean.NewsMovieBean> list) {
        long zhen = 0;
        for (int i = 0; i < list.size(); i++) {
            long end = Long.parseLong(list.get(i).getOut_position());
            long start = Long.parseLong(list.get(i).getIn_position());
            long duration = end - start;
            zhen = zhen + duration;
            LogUtil.d("当前总的帧数为" + zhen);
        }
        return zhen;
    }


    /**
     * 帧值 转化成 时:分:秒:帧
     *
     * @param frame 帧值
     * @return
     */
    public static String formatDuration(long frame) {
        if (frame < 0) {
            frame = 0;
        }
        StringBuilder duration = new StringBuilder();
        DecimalFormat decimal = new DecimalFormat("00");
        for (long rate = 3600, second = frame / 25; rate > 0; second %= rate, rate /= 60) {
            duration.append(decimal.format(second / rate)).append(":");
        }
        duration.append(decimal.format(frame % 25));
        return duration.toString();
    }

    /**
     * 00:00:00:00换算成毫秒值
     */

    public static long getZhenDuration(String timeMills) {
        if (timeMills == null) {
            return 0;
        }
        //00:00:00:00
        String[] times = timeMills.split(":");
        long zhen = 0;
        for (int i = 0; i < timeMills.length(); i++) {
            if (i == 0) {
                zhen = zhen + Integer.parseInt(times[i]) * 60 * 60 * 25;
            } else if (i == 1) {
                zhen = zhen + Integer.parseInt(times[i]) * 60 * 25;
            } else if (i == 2) {
                zhen = zhen + Integer.parseInt(times[i]) * 25;
            } else if (i == 3) {
                long mzhen = Integer.parseInt(times[i]);
                zhen = zhen + mzhen;
            }
        }
        return zhen;
    }

    /**
     * 获取 00:00格式的时间
     *
     * @param timeLeanth
     * @return
     */
    public static String getFenMiaoDuration(String timeLeanth) {
        int haoMiao = Integer.parseInt(timeLeanth);
        int miao = haoMiao / 1000;
        int fen = 0;
        if (miao % 60 == 0) {
            fen = miao / 60;
            miao = 0;
        } else {
            fen = miao / 60;
            miao = miao % 60;
        }
        String fenStr = String.format("%02d", fen);
        String miaoStr = String.format("%02d", miao);
        String mTimeLeanth = fenStr + ":" + miaoStr;
        return mTimeLeanth;
    }

    /**
     * 获取毫秒值
     * @param minVlue
     * @param maxValue
     * @return
     */
  public static long[] getTimeMill(String minVlue, String maxValue) {

        long[] times = new long[2];
        String[] minTimeValues = minVlue.split(":");
        long minTime = Integer.parseInt(minTimeValues[0]) * 60 * 60 * 1000 + Integer.parseInt(minTimeValues[1]) * 60 * 1000 + Integer.parseInt(minTimeValues[2]) * 1000 + Integer.parseInt(minTimeValues[3]) *40;
        String[] maxTimeValues = maxValue.split(":");
        long maxTime = Integer.parseInt(maxTimeValues[0]) * 60 * 60 * 1000 + Integer.parseInt(maxTimeValues[1]) * 60 * 1000 + Integer.parseInt(maxTimeValues[2]) * 1000 + Integer.parseInt(maxTimeValues[3]) * 40;
        times[0] = minTime;
        times[1] = maxTime;
        LogUtil.e("-------------minvalue"+minTime+"-------------"+maxTime);
        return times;
    }
   /* public static long getTimeMill(String duration) {

        String[] maxTimeValues = duration.split(":");
        long maxTime = Integer.parseInt(maxTimeValues[0]) * 60 * 60 * 1000 + Integer.parseInt(maxTimeValues[1]) * 60 * 1000 + Integer.parseInt(maxTimeValues[2]) * 1000 + Integer.parseInt(maxTimeValues[3]) * 40;

        LogUtil.e("-------------minvalue"+maxTime+"-------------"+maxTime);
        return maxTime;
    }*/
}
