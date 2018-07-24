package movi.ui.bean;

import java.io.Serializable;
import java.util.ArrayList;

import movi.base.Response;

/**
 * 获取相关的电台信息
 */
public class ChannelListBean extends Response implements Serializable {
    private ArrayList<ChannelBean> data;

    public ArrayList<ChannelBean> getData() {
        return data;
    }

    public void setData(ArrayList<ChannelBean> data) {
        this.data = data;
    }

    public class ChannelBean implements Serializable {
        private String title;
        private String ip;
        private String id;
        private String applogo;
        private String platform;

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        private String applogobig;//"http:\/\/123.56.6.125\/app\/applogo\/sichuanbig.png",
        private String corporatemsg;//": "\u56db\u5ddd\u5e7f\u64ad\u7535\u89c6\u603b\u53f0",
        private String versionsios;// "1.0.0",
        private String versionsanz;//": "1.0.0",
        private String iosurl;//": null,
        private String anzurl;//": null

        private String sftpip;

        public String getSftpip() {
            return sftpip;
        }

        public void setSftpip(String sftpip) {
            this.sftpip = sftpip;
        }

        public String getApplogobig() {
            return applogobig;
        }

        public void setApplogobig(String applogobig) {
            this.applogobig = applogobig;
        }

        public String getCorporatemsg() {
            return corporatemsg;
        }

        public void setCorporatemsg(String corporatemsg) {
            this.corporatemsg = corporatemsg;
        }

        public String getVersionsios() {
            return versionsios;
        }

        public void setVersionsios(String versionsios) {
            this.versionsios = versionsios;
        }

        public String getVersionsanz() {
            return versionsanz;
        }

        public void setVersionsanz(String versionsanz) {
            this.versionsanz = versionsanz;
        }

        public String getIosurl() {
            return iosurl;
        }

        public void setIosurl(String iosurl) {
            this.iosurl = iosurl;
        }

        public String getAnzurl() {
            return anzurl;
        }

        public void setAnzurl(String anzurl) {
            this.anzurl = anzurl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getApplogo() {
            return applogo;
        }

        public void setApplogo(String applogo) {
            this.applogo = applogo;
        }
    }
}
