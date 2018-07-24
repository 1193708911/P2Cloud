package movi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.widget.ViewUtils;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VideoUtils {
    private static Context context;
    private static Bitmap bitmap;

    public VideoUtils(Context context) {
        super();
        this.context = context;

    }


    public Bitmap getFrams(String viedioPath, int number) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(viedioPath, new HashMap<String, String>());
        // 获取的是微秒
        Bitmap bitmap = retriever.getFrameAtTime(number * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        return bitmap;

    }

    /**
     * 获取视频缩略图
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap mBitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            mBitmap = retriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return mBitmap;

    }

    /**
     * 获取视频相关
     * 信息通过传进来的参数
     * 进行重新组装数据
     */
    public static Map<String, String> getVideoInfo(Context context, Map<String, String> params, String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        /**
         * 截取时间字符串
         */
        //获取时间搓
        String timeInMills = videoPath.substring(videoPath.lastIndexOf("/") + 1).split("\\.")[0];
        //转换为  yyyy-mm-dd hh:mm:ss模式
        try {
            String YMD_SMS = Constants.DATE_TIME_FORMAT.format(new Date());
            String YearMonthDay = YMD_SMS.split(" ")[0];
            String HourMinuteSecond = YMD_SMS.split(" ")[1];
            //同时将时分秒.mp4进行进一步截取
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒

            String frame = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE) + ""; // 播放时长单位为毫秒

            //将时长化为帧数
            int hDuration = Integer.parseInt(duration);
            String zhenDuration = String.valueOf(hDuration % 40 == 0 ? hDuration / 40 : hDuration / 40 + 1);
            params.put("Duration", zhenDuration);

            //开始组装数据
            params.put("Duration", zhenDuration);
            params.put("StartDate", YearMonthDay + "T" + HourMinuteSecond + "+0800");
            params.put("EndDate", YearMonthDay + "T" + HourMinuteSecond + "+0800");
            //视频创建时间  更新时间
            params.put("CreationDate", YearMonthDay + "T" + HourMinuteSecond + "+0800");
            params.put("LastUpdateDate", YearMonthDay + "T" + HourMinuteSecond + "+0800");
            String GlobalClipID = CommonUtils.get64RandomStr();
            params.put("GlobalClipID", GlobalClipID);
            //拼接deviceId
//            String deviceId = "# PTMDL 1.0 \nmedia:\n  type: androidid\n  id:" + " " + CommonUtils.getDeviceId(context) + "\n";
            String deviceId = "# PTMDL 1.0 \nmedia:\n  type: androidid\n  id:" + " " + "ANROIDCLOD" + "\n";
            params.put("deviceId", deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;

    }

    /**
     * 获取视频时长
     */
    public static String getTimeLeanth(Context context, String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);


        try {
            //同时将时分秒.mp4进行进一步截取
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
            LogUtil.d("+++++++++++时长" + duration);
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }


    public static String getVideoDuration(String viedioPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(viedioPath, new HashMap<String, String>());
        // 获取的是微秒
        String timeLeanth = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        LogUtil.d("当前视频的总的时长为"+timeLeanth);
        return timeLeanth;

    }


}
