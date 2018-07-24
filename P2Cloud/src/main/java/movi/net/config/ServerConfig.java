package movi.net.config;

/**
 * Created by chjj on 2016/5/9.
 * 后台接口配置信息
 */
public class ServerConfig {
    public static final String GET = "Get";
    public static final String POST = "Post";
    //公司测试机
    public static String SERVER_PATH = "http://123.56.6.125/";
    public static final String SERVER_URL_PATH = SERVER_PATH + "wap/";
    //公司生产机
//    public  static  final  String  SERVER_PATH="http://123.56.31.62/";
//    public static final String SERVER_URL_PATH = SERVER_PATH+"wap/";
    //登录接口
    public static final String SERVER_PATH_LOGIN = "login/";
    //新闻文稿修改
    public static final String SERVER_PATH_WebGao = "ncedit";
    //首页信息处理
    //http://123.56.6.125/wap/clip/1/3/2015-05-03
    public static final String SERVER_PATH_CLOUD = "clip/";
    //修改密码
    public static final String SERVER_PATH_UPDATEPS = "/user/updatePassword/";
    //修改用户基本信息
    public static final String SERVER_PATH_UPDATAINFO = "user/updateUserInfo/";
    //退出
    public static final String SERVER_PATH_EXIT = "logout";
    //新闻文稿信息
    public static final String SERVER_PATH_PIC = "ncshow/";
    //顶部图片地址
    public static final String SERVER_PATH_HEADPIC = "listshow/";

    //剪辑列表中上传图片
    public static final String SERVER_PATH_PLAYADD = "playlistadd";
    //视频合成 素材状态
    public static final String SERVER_PATH_PICTOGE = "liststatus/";
    //视频合成剪辑状态
    public static final String SERVER_PATH_PICCUTSTATUS = "clipstatus";
    //  抽图   视频  视频地址
    public static final String SERVER_PATH_SHEET_MOVIE = "listshowall/";
    //请求图片列表
    public static final String SERVER_PATH_SHEET_PIC = "photoshow/";
    //流量查詢
    public static final String SERVER_PATH_SELECT_LIULIANG = "flow/";

    //上传图片
    public static final String SERVER_PATH_PHOTOUPLOAD = "photoadd";

    //删除图片
    public static final String SERVER_PATH_PHOTODELETE = "photodel/";

    public static final String SERVER_PATH_APP = "http://p2cloudweb.ctvit.tv/app/javaflow.php?verify=";
    //剪辑列表删除
    public static final String SERVER_PATH_CUT_DELETE = "playlistdel/";
    //删除全部
    public static final String SERVER_PATH_CUT_DELETEALL = "playlistdelall/";//参数playlist_id
    //意见反馈
    public  static final String  SERVER_PATH_ADVISE="http://123.56.6.125/app/idea.php";
    //上传完成后通知后台上传成功
    public  static final String  SERVER_PATH_NOTIFY_UPLOAD= "phone";
    //修改素材名称接口
//    http://123.56.6.125/wap/
    public  static  final String SERVER_PATH_UPDATE_SUCAINAME="phoneups";

    //合成发布上一个下一个接口
    public   static  final String  SERVER_PATH_CLIP_STATUS="new_clip_status";
}
