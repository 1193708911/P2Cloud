package movi.ui.bean;

import java.io.Serializable;

import movi.base.Response;

/**
 * Created by chjj on 2016/6/16.
 *
 * 查詢流量Bean
 */
public class NewsLiuLiangListBean extends Response implements Serializable{
    private NewsLiuLiangBean  data;

    public NewsLiuLiangBean getData() {
        return data;
    }

    public void setData(NewsLiuLiangBean data) {
        this.data = data;
    }

    public class NewsLiuLiangBean{
        private String id;
        private String       gid;
        private String       cid;
        private String       traffic;// 流量
        private String    storage;//,  存储
        private String   datemark;
        private String    Storagedate;//今日存储
        private String    created;//开始时间
        private String   modified;// 更新时间

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getTraffic() {
            return traffic;
        }

        public void setTraffic(String traffic) {
            this.traffic = traffic;
        }

        public String getStorage() {
            return storage;
        }

        public void setStorage(String storage) {
            this.storage = storage;
        }

        public String getDatemark() {
            return datemark;
        }

        public void setDatemark(String datemark) {
            this.datemark = datemark;
        }

        public String getStoragedate() {
            return Storagedate;
        }

        public void setStoragedate(String storagedate) {
            Storagedate = storagedate;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }

        public String getModified() {
            return modified;
        }

        public void setModified(String modified) {
            this.modified = modified;
        }
    }


}
