package movi.net.config;

import movi.MainApplication;

/**
 * Created by admin on 2016/12/9.
 */

public class RemoteServerPath {
//    /**
//     * /data/hubei001/P2PROXY/CONTENTS/PROXY  mp4上传路径
//     * /data/hubei001/P2PROXY/CONTENTS/ICON/   bmp上传路径
//     * /data/hubei001/P2PROXY/CONTENTS/CLIP/     xml 上传路径
//     */
//
//    public static String REMOTE_SERVER_PATH = "10.7.0.58";
//    public static String USER_NAME = "root";
//    public static String USER_PASSWORD = "123456";
////    10.7.0.58  root  123456
//    /**
//     * 确定上传路径
//     */
//    //MP4上传路径
    public static final String REMOTE_MP4_PATH = "/data/" + MainApplication.getInstance().getUser().getUsername() + "/P2PROXY/CONTENTS/PROXY/";
    //图片上传路径
    public static final String REMOTE_ICON_PATH = "/data/" + MainApplication.getInstance().getUser().getUsername() + "/P2PROXY/CONTENTS/ICON/";
    //xml上传路径
    public static final String REMOTE_XML_PATH = "/data/" + MainApplication.getInstance().getUser().getUsername() + "/P2PROXY/CONTENTS/CLIP/";
}
