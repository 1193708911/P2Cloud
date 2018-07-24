package movi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import movi.ui.db.VideoInfo;

/**
 * Created by chjj on 2016/8/12.
 * 拼接xml文件
 * 动态生成xml文件
 */
public class XmlUtils {
    //默认进度
    private static final int PROGRESS = 0;
    private static String mCurrentTime;
    private static String fileIconXmlName;

    public static String getXmlFile(Map<String, String> params) {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"  ?>" + "\n" +
                "<P2Main xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:schemas-Professional-Plug-in:P2:ClipMetadata:v3.2\">"
                + "\n" +
                "<ClipContent>" +
                "\n" +
                "<ClipName>" + params.get("ClipName") + "</ClipName> <!-- 素材名字-->" + "\n" +
                "<GlobalClipID>" + params.get("GlobalClipID") + "</GlobalClipID>" + "\n" + "<!-- ID不重复-->" + "\n" +
                "<Duration>" + params.get("Duration") + "</Duration>  <!--视频帧数-->" + "\n" +
                "<EditUnit>1/25</EditUnit>" + "\n" +
                "<EssenceList>" + "\n" +
                "<Video ValidAudioFlag=\"false\">" + "\n" +
                "<VideoFormat>MXF</VideoFormat>" + "\n" +
                "<Codec Class=\"100\">AVC-I_1080/50i</Codec>" + "\n" +
                "<FrameRate>25p</FrameRate>" + "\n" +
                "<StartTimecode>" + params.get("StartTimecode") + "</StartTimecode><!--视频开始时间00：00：00-->" + "\n" +
                "<StartBinaryGroup>00000000</StartBinaryGroup>" + "\n" +
                "<AspectRatio>" + params.get("AspectRatio") + "</AspectRatio><!--视频分辨率-->" + "\n" +
                "<VideoIndex>" + "\n" +
                "<StartByteOffset>32768</StartByteOffset><!--   -->" + "\n" +
                "<DataSize>" + params.get("DataSize") + "</DataSize><!--视频大小 k-->" + "\n" +
                "</VideoIndex>" + "\n" +
                "</Video>" + "\n" +
                "<Audio>" + "\n" +
                "<AudioFormat>MXF</AudioFormat>" + "\n" +
                "<SamplingRate>48000</SamplingRate>" + "\n" +
                "<BitsPerSample>16</BitsPerSample>" + "\n" +
                "<AudioIndex>" + "\n" +
                "<StartByteOffset>32768</StartByteOffset>" + "\n" +
                "<DataSize>1501440</DataSize>" + "\n" +
                "</AudioIndex>" + "\n" +
                "</Audio>" + "\n" +
                "<Audio>" + "\n" +
                "<AudioFormat>MXF</AudioFormat>" + "\n" +
                "<SamplingRate>48000</SamplingRate>" + "\n" +
                "<BitsPerSample>16</BitsPerSample>" + "\n" +
                "<AudioIndex>" + "\n" +
                "<StartByteOffset>32768</StartByteOffset>" + "\n" +
                "<DataSize>1501440</DataSize>" + "\n" +
                "</AudioIndex>" + "\n" +
                "</Audio>" + "\n" +
                "<Audio>" + "\n" +
                "<AudioFormat>MXF</AudioFormat>" + "\n" +
                "<SamplingRate>48000</SamplingRate>" + "\n" +
                "<BitsPerSample>16</BitsPerSample>" + "\n" +
                "<AudioIndex>" + "\n" +
                "<StartByteOffset>32768</StartByteOffset>" + "\n" +
                "<DataSize>1501440</DataSize>" + "\n" +
                "</AudioIndex>" + "\n" +
                "</Audio>" + "\n" +
                "<Audio>" + "\n" +
                " <AudioFormat>MXF</AudioFormat>" + "\n" +
                " <SamplingRate>48000</SamplingRate>" + "\n" +
                "<BitsPerSample>16</BitsPerSample>" + "\n" +
                "<AudioIndex>" + "\n" +
                " <StartByteOffset>32768</StartByteOffset>" + "\n" +
                "<DataSize>1501440</DataSize>" + "\n" +
                "</AudioIndex>" + "\n" +
                "</Audio>" + "\n" +
                "</EssenceList>" + "\n" +
                "<ClipMetadata>" + "\n" +
                "<UserClipName>" + params.get("GlobalClipID") + "</UserClipName> <!-- ID不重复 上边一样-->" + "\n" +
                "<DataSource>SHOOTING</DataSource>" + "\n" +
                "<Access>" + "\n" +
                "<CreationDate>" + params.get("CreationDate") + "</CreationDate> <!-- 创建时间-->" + "\n" +
                "<LastUpdateDate>" + params.get("LastUpdateDate") + "</LastUpdateDate> <!-- 更新时间-->" + "\n" +
                "</Access>" + "\n" +
                "<Device>" + "\n" +
                "<Manufacturer>Android</Manufacturer>" + "\n" +
                "<SerialNo.>C4TCA01361</SerialNo.>" + "\n" +
                "<ModelName>Android</ModelName>" + "\n" +
                "</Device>" + "\n" +
                "<Shoot>" + "\n" +
                "<StartDate>" + params.get("StartDate") + "</StartDate> <!-- 视频开始时间-->" + "\n" +
                "<EndDate>" + params.get("EndDate") + "</EndDate>" + "\n" +
                "</Shoot>" + "\n"+
                "<Scenario>"+
                "<ProgramName>"+params.get("programName")+"</ProgramName>"+
                "</Scenario>"+
                "<MemoList>" + "\n" +
                "<Memo MemoID=\"0\">" + "\n" +
                "<Text>" + params.get("deviceId") + "</Text>" + "\n" +
                "</Memo>" +
                "</MemoList>" + "\n" +
                "<Thumbnail>" + "\n" +
                "<FrameOffset>0</FrameOffset>" + "\n" +
                "<ThumbnailFormat>bmp</ThumbnailFormat>" + "\n" +
                "<Width>80</Width>" + "\n" +
                "<Height>60</Height>" + "\n" +
                "</Thumbnail>" + "\n" +
                "<Proxy>" + "\n" +
                "<ProxyFormat>mp4</ProxyFormat>" + "\n" +
                "<ProxyElementList>" + "\n" +
                "<ProxyVideo>" + "\n" +
                "<ProxyVideoCodec>AVC</ProxyVideoCodec>" + "\n" +
                "<ProxyVideoBitRate>1500000</ProxyVideoBitRate>" + "\n" +
                "<ProxyFrameRate>25</ProxyFrameRate>" + "\n" +
                "<ProxyResolution>640x360</ProxyResolution>" + "\n" +
                "<AspectRatio>16:9</AspectRatio>" + "\n" +
                "<OnScreenTimecode>OFF</OnScreenTimecode>" + "\n" +
                "</ProxyVideo>" + "\n" +
                "<ProxyAudio>" + "\n" +
                "<ProxyAudioCodec>AAC</ProxyAudioCodec>" + "\n" +
                "<ProxyAudioBitRate>64000</ProxyAudioBitRate>" + "\n" +
                "<ProxySamplingRate>48000</ProxySamplingRate>" + "\n" +
                "<OriginalChannel>0</OriginalChannel>" + "\n" +
                "</ProxyAudio>" + "\n" +
                "<ProxyAudio>" + "\n" +
                "<ProxyAudioCodec>AAC</ProxyAudioCodec>" + "\n" +
                "<ProxyAudioBitRate>64000</ProxyAudioBitRate>" + "\n" +
                " <ProxySamplingRate>48000</ProxySamplingRate>" + "\n" +
                "<OriginalChannel>1</OriginalChannel>" + "\n" +
                "</ProxyAudio>" + "\n" +
                "</ProxyElementList>" + "\n" +
                "</Proxy>" + "\n" +
                "</ClipMetadata>" + "\n" +
                "</ClipContent>" + "\n" +
                "</P2Main>";
        LogUtil.d(xml);
        return xml;
    }

    /**
     * 组装数据
     */
    public static Map<String, String> getAfterUploadVideoInfo(Context context, Map<String, String> params, String videoPath) {
        /**
         * 组装数据重新赋值
         */
        params = VideoUtils.getVideoInfo(context, params, videoPath);
        return params;
    }

    /**
     * 保存缩略图
     *
     * @return
     */
    public static String saveBitMap(String path) {
        Bitmap bitmap = VideoUtils.getVideoThumbnail(path);
        try {
            mCurrentTime = path.substring(path.lastIndexOf("/") + 1).split("\\.")[0];
            File mFile = new File(Environment.getExternalStorageDirectory(), mCurrentTime + ".jpg");
            if (mFile.exists()) {
                mFile.delete();
            }
            mFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(mFile);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                return mFile.getAbsolutePath();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取xml
     *
     * @return
     */
    public static VideoInfo getXmlMetaData(Context context, VideoInfo videoInfo, String videoPath, String picPath) {
        /**
         * 在这里进行上传完成之后的
         * 数据的组装
         */
        try {
            String fbl = "16:9";
            Map<String, String> params = new HashMap<>();
            if (PreferencesUtil.getInt("FBL") == 720) {
                fbl = "16:9";
            } else if (PreferencesUtil.getInt("FBL") == 480) {
                fbl = "3:2";
            } else if (PreferencesUtil.getInt("FBL") == 560) {
                fbl = "16:9";
            }

            mCurrentTime = videoPath.substring(videoPath.lastIndexOf("/") + 1).split("\\.")[0];
            videoInfo.setCurrentTime(mCurrentTime);
            LogUtil.d("----------" + mCurrentTime);
            //素材名称
            params.put("ClipName", mCurrentTime);
            //分辨率
            params.put("AspectRatio", fbl);
            //视频开始时间
            params.put("StartTimecode", "00:00:00:00");

            params.put("programName",videoInfo.getTitle());
            //视频大小
            File videFile = new File(videoPath);
            if (videFile.exists()) {
                params.put("DataSize", String.valueOf(videFile.length()));
            }
            videoInfo.setVideoFileLeanth((int) videFile.length());
            videoInfo.setPicPath(picPath);
            videoInfo.setPhotoPath(picPath);

            /**
             * 获取视频的开始时间
             * 创建时间以及更新时间
             * 视频帧数  组装数据重新赋值
             */
            params = XmlUtils.getAfterUploadVideoInfo(context, params, videoPath);
            String xml = XmlUtils.getXmlFile(params);
            LogUtil.i("------------" + xml);
            String indexPath = videoPath.substring(videoPath.lastIndexOf("/") + 1);
            fileIconXmlName = indexPath.split("\\.")[0];
            videoInfo.setFileIconXmlName(fileIconXmlName);
            File file = new File(Environment.getExternalStorageDirectory(), fileIconXmlName + ".xml");
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(xml.getBytes("utf-8"));
            outputStream.flush();
            videoInfo.setXmlFilePath(file.getAbsolutePath());
            return videoInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
