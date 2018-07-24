package movi.ui.bean;

import java.io.Serializable;

import movi.base.Response;

/**
 * Created by chjj on 2016/5/9.
 * 登录
 */
public class LoginListBean extends Response implements  Serializable{
        public  LoginBean  data;
        public LoginBean getData() {
                return data;
        }
        public void setData(LoginBean data) {
                this.data = data;
        }
        public class LoginBean implements Serializable{
        private String member_id;
        private String give_name;
        private String family_name;
        private String  username;
        private String requestTraceId;
        private String  sessionTraceId;
        private String   company_id;
        private String  company_name;
        private String   group_id;
        private String   password;
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
}
