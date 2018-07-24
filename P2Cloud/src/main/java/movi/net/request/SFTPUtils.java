package movi.net.request;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by chjj on 2016/8/5.
 */
public class SFTPUtils {
    private OnFileSizeMaxListenner listenner;
    private String TAG = "SFTPUtils";
    private String host;
    private String username;
    private String password;
    private int port = 22;
    private ChannelSftp sftp = null;
    private Session sshSession = null;
    FileInputStream in = null;

    public SFTPUtils(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * connect server via sftp
     */
    public ChannelSftp connect() {
        JSch jsch = new JSch();
        try {
            sshSession = jsch.getSession(username, host, port);
            LogUtil.d("0000000当前要连接的用户名   host  port  为" + username + host + port);
            if (sshSession != null) {
                sshSession.setPassword(password);
                LogUtil.d("0000000当前的密码为" + password);
                Properties sshConfig = new Properties();
                sshConfig.put("StrictHostKeyChecking", "no");
                sshSession.setConfig(sshConfig);
                sshSession.connect();
                Channel channel = sshSession.openChannel("sftp");
                if (channel != null) {
                    channel.connect();
                    Log.e("当前的连接状态为", String.valueOf(channel.isConnected()));
                } else {
                    Log.e(TAG, "channel connecting failed.");
                }
                sftp = (ChannelSftp) channel;
                return sftp;
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 鏂紑鏈嶅姟鍣�
     */
    public void disconnect() {
        if (this.sftp != null) {
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
                Log.d(TAG, "sftp is closed already");
            }
        }
        if (this.sshSession != null) {
            if (this.sshSession.isConnected()) {
                this.sshSession.disconnect();
                Log.d(TAG, "sshSession is closed already");
            }
        }
    }

    /**
     * 鍗曚釜鏂囦欢涓婁紶
     *
     * @param remotePath
     * @param remoteFileName
     * @param localPath
     * @param localFileName
     * @return
     */
    public boolean uploadFile(String remotePath, String remoteFileName,
                              String localPath, String localFileName, SftpProgressMonitor lSMonitor) {
        try {
            File file = new File(localPath);
            if (file.exists()) {
                if (listenner != null) {
                    listenner.onFileMax(file.length());
                }
                in = new FileInputStream(file);
                if (in != null) {
                    LogUtil.d("-----------" + localPath);
                    sftp.put(in, remotePath + localFileName, lSMonitor);
                    System.out.println(sftp);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 鎵归噺涓婁紶
     *
     * @param remotePath
     * @param localPath
     * @param del
     * @return
     */
    public boolean bacthUploadFile(String remotePath, String localPath,
                                   boolean del, SftpProgressMonitor lMonitor) {
        try {
            File file = new File(localPath);
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()
                        && files[i].getName().indexOf("bak") == -1) {
                    synchronized (remotePath) {
                        if (this.uploadFile(remotePath, files[i].getName(),
                                localPath, files[i].getName(), lMonitor)
                                && del) {
                            deleteFile(localPath + files[i].getName());
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return false;

    }

    /**
     * 鎵归噺涓嬭浇鏂囦欢
     *
     * @param remotPath  杩滅▼涓嬭浇鐩綍(浠ヨ矾寰勭鍙风粨鏉�)
     * @param localPath  鏈湴淇濆瓨鐩綍(浠ヨ矾寰勭鍙风粨鏉�)
     * @param fileFormat 涓嬭浇鏂囦欢鏍煎紡(浠ョ壒瀹氬瓧绗﹀紑澶�,涓虹┖涓嶅仛妫�楠�)
     * @param del        涓嬭浇鍚庢槸鍚﹀垹闄ftp鏂囦欢
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean batchDownLoadFile(String remotPath, String localPath,
                                     String fileFormat, boolean del) {
        try {
            connect();
            Vector v = listFiles(remotPath);
            if (v.size() > 0) {

                Iterator it = v.iterator();
                while (it.hasNext()) {
                    ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) it.next();
                    String filename = entry.getFilename();
                    SftpATTRS attrs = entry.getAttrs();
                    if (!attrs.isDir()) {
                        if (fileFormat != null && !"".equals(fileFormat.trim())) {
                            if (filename.startsWith(fileFormat)) {
                                if (this.downloadFile(remotPath, filename,
                                        localPath, filename)
                                        && del) {
                                    deleteSFTP(remotPath, filename);
                                }
                            }
                        } else {
                            if (this.downloadFile(remotPath, filename,
                                    localPath, filename)
                                    && del) {
                                deleteSFTP(remotPath, filename);
                            }
                        }
                    }
                }
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            this.disconnect();
        }
        return false;
    }

    /**
     * 鍗曚釜鏂囦欢涓嬭浇
     *
     * @param remotePath
     * @param remoteFileName
     * @param localPath
     * @param localFileName
     * @return
     */
    public boolean downloadFile(String remotePath, String remoteFileName,
                                String localPath, String localFileName) {
        try {
            sftp.cd(remotePath);
            File file = new File(localPath + localFileName);
            mkdirs(localPath + localFileName);
            sftp.get(remoteFileName, new FileOutputStream(file));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 鍒犻櫎鏂囦欢
     *
     * @param filePath
     * @return
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            return false;
        }
        return file.delete();
    }

    public boolean createDir(String createpath) {
        try {
            if (isDirExist(createpath)) {
                this.sftp.cd("/yyyy/");
                Log.e(TAG, createpath);
                Log.e("------", createpath);
                return true;
            }

            Log.e("------------", createpath);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 鍒ゆ柇鐩綍鏄惁瀛樺湪
     *
     * @param directory
     * @return
     */
    @SuppressLint("DefaultLocale")
    public boolean isDirExist(String directory) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    public void deleteSFTP(String directory, String deleteFile) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 鍒涘缓鐩綍
     *
     * @param path
     */
    public void mkdirs(String path) {
        File f = new File(path);
        String fs = f.getParent();
        f = new File(fs);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 鍒楀嚭鐩綍鏂囦欢
     *
     * @param directory
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }


    /**
     * 对外提供一个接口用于回调返回
     * 相关的文件的大小
     */
    public interface OnFileSizeMaxListenner {
        void onFileMax(long fileSize);
    }

    public void setOnFileSizeMaxListenner(OnFileSizeMaxListenner listenner) {
        this.listenner = listenner;
    }

    /**
     * 判断是否sdcard能被读取
     *
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}