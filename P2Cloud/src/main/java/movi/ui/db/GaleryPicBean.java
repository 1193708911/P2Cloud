package movi.ui.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by chjj on 2016/5/19.
 * 保存图片路径数据库bean文件
 */
@Table(name = "GaleryPicBean" )
public class GaleryPicBean implements Serializable{
    @Column(name = "id",isId = true)
    private int id;
    @Column(name = "path")
    private String path;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
