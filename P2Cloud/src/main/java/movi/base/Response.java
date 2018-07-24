package movi.base;

import java.io.Serializable;

/**
 * Created by chjj on 2016/5/9.
 * 基类
 */

public class Response  implements Serializable{
    protected int status;//:状态值（int 型的数字  如 0失败  1成功）,
    protected String msg;//:返回提示信息
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



}
