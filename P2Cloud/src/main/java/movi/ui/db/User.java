package movi.ui.db;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 登录信息用户名密码
 */
@Table(name = "User" )
public class User implements Serializable{
    @Column(name = "Id",isId = true)
    private String id;
    @Column(name = "member_id")
    private String member_id;
    @Column(name = "give_name")
    private String give_name;
    @Column(name = "family_name")
    private String family_name;
    @Column(name = "username")
    private String  username;
    @Column(name = "requestTraceId")
    private String requestTraceId;
    @Column(name = "sessionTraceId")
    private String  sessionTraceId;
    @Column(name = "company_id")
    private String   company_id;
    @Column(name = "company_name")
    private String  company_name;
    @Column(name = "group_id")
    private String   group_id;
    @Column(name = "password")
    private String   password;
    @Column(name = "timezone")
    private String   timezone;

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getGive_name() {
        return give_name;
    }

    public void setGive_name(String give_name) {
        this.give_name = give_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRequestTraceId() {
        return requestTraceId;
    }

    public void setRequestTraceId(String requestTraceId) {
        this.requestTraceId = requestTraceId;
    }

    public String getSessionTraceId() {
        return sessionTraceId;
    }

    public void setSessionTraceId(String sessionTraceId) {
        this.sessionTraceId = sessionTraceId;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
