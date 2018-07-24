package movi.ui.bean;

import java.io.Serializable;

/**
 * Created by chjj on 2016/12/7.
 */
public class ThreeLoginBean implements Serializable {
    //    {"loginId":"12345678", "loginName":"admin","platform":"cjy"}
    private String loginId;
    private String loginName;
    private String platform;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
