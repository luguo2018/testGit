package com.jmhy.sdk.bean;

import java.util.List;

public class InitInfo {

    /**
     * is_sdk_float_on : 1
     * is_user_float_on : 1
     * is_logout_float_on : 1
     * is_reg_login_on : 1
     * is_visitor_on : 1
     * is_visitor_on_phone : 1
     * is_auto_login_on : 1
     * is_log_on : 0
     * is_shm : 0
     * switch_login : 0
     * skin : 9
     * h5_game_url : aHR0cHM6Ly9hcGkuNDl5b3UuY29tL0FwaS9DaGFubmVsaGYvTG9naW4vcGxhdGZvcm1pZC85L3B0Z2lkLzM4OS9wZi9qbXB0
     * is_service_float_on : 1
     * channel_sdk_list : {"test":[],"jrtt":{"app_id":"186554","app_name":"ztxj08","app_name_cn":"择天仙诀08","package_name":"com.jmhy.game.ztxj"}}
     * code_area_list : ["+86"]
     * reg_flow_type : 1
     * expired : 86400
     * online_report_interval : 60
     * show_url_after_init :
     * access_token : 102933_1600757356cac7c2c8570564377000824c63eadaaad526b98443a0901e
     * user_agreement_url : aHR0cHM6Ly9hcGkudGVzdC5pam02LmNvbS9zZGtfY29tbW9uL2Rpc3QvYWdyZWVtZW50Lmh0bWw/ZnJvbUdhbWU9MSZzZGtTdGF0ZT0x
     * forget_password_url : aHR0cHM6Ly9hcGkudGVzdC5pam02LmNvbS9jb21tdW5pdHkvZGlzdC9wYXNzd29yZF9pZGVudGl0eV9uZXcuaHRtbD9mcm9tR2FtZT0xJnNka1N0YXRlPTE=
     * add_global_script_url : P3Nka1N0YXRlPTE=
     * float_icon_url : aHR0cHM6Ly9zLjV0YzUuY29tL2MvMjAyMDA5MjEvMTUvODQzMWVhMmIzMmE4MjljZi5wbmc=
     * web_loading_url : aHR0cHM6Ly9zLjV0YzUuY29tL2MvMjAyMDA5MjEvMTUvODQzMWVhMmIzMmE4MjljZi5wbmc=
     * u_p_init : 0
     * customer_service_url : aHR0cHM6Ly9hcGkudGVzdC5pam02LmNvbS9nby90bz9hY2Nlc3NfdG9rZW49MTAyOTMzXzE2MDA3NTczNTZjYWM3YzJjODU3MDU2NDM3NzAwMDgyNGM2M2VhZGFhYWQ1MjZiOTg0NDNhMDkwMWUmcmVkaXJlY3RfdXJsPWh0dHBzJTNBJTJGJTJGYXBpLnRlc3QuaWptNi5jb20lMkZzZGtfY29tbW9uJTJGZGlzdCUyRnNlcnZpY2UuaHRtbCUzRmZyb21HYW1lJTNEMSUyNnNka1N0YXRlJTNEMQ==
     * sk : d3M6Ly82aGc4LndzLjl0b3YuY29tOjk1MDI=
     * moblie_direct_login : {"type":"aliyun","clientSecret":"dYG3R2817Btq+mHwklkVO30+xhyxc8ZWQh5wIFYe9ebf6I4wltzmh7KVlSIpG5n5s1QHvsTFM1TEHpQYSWaRgmQMSpsyDQAUEBx3CZb0QABmNRe71rw/Q5DFzJBuoxXo4c2R3vPVIB961dTvNCgfR6VZtGQLgkovX7vYAlnWhc+C0bv4/leKaZyW5jgogmEwX6aHTprM/HsqB3dzs+dqt3M+n7ZejUb2ZevVlc31hizQm2sP7J+Jggg9vRFATHrnb+dNIYFAoIctj3ho9sJ8BkTz2Qr/1s7Q"}
     * icon_skin : 1
     * hgu : aHR0cHM6Ly9hcGkuNDl5b3UuY29tL0FwaS9DaGFubmVsaGYvTG9naW4vcGxhdGZvcm1pZC85L3B0Z2lkLzM4OS9wZi9qbXB0
     * ali_hot_fix:0
     */

    private int is_sdk_float_on;
    private int is_user_float_on;
    private int is_logout_float_on;
    private int is_reg_login_on;
    private int is_visitor_on;
    private int is_visitor_on_phone;
    private int is_auto_login_on;
    private int is_log_on;
    private int is_shm;
    private int switch_login;
    private int skin;
    private String h5_game_url;
    private int is_service_float_on;
    private ChannelSdkListBean channel_sdk_list;
    private int reg_flow_type;
    private int expired;
    private int online_report_interval;
    private String show_url_after_init;
    private String access_token;
    private String user_agreement_url;
    private String forget_password_url;
    private String add_global_script_url;
    private String float_icon_url;
    private String web_loading_url;
    private int u_p_init;
    private String customer_service_url;
    private String sk;
    private MoblieDirectLoginBean moblie_direct_login;
    private int icon_skin;
    private String hgu;
    private List<String> code_area_list;
    private int ali_hot_fix;

    public int getAli_hot_fix() {
        return ali_hot_fix;
    }

    public void setAli_hot_fix(int ali_hot_fix) {
        this.ali_hot_fix = ali_hot_fix;
    }

    public int getIs_sdk_float_on() {
        return is_sdk_float_on;
    }

    public void setIs_sdk_float_on(int is_sdk_float_on) {
        this.is_sdk_float_on = is_sdk_float_on;
    }

    public int getIs_user_float_on() {
        return is_user_float_on;
    }

    public void setIs_user_float_on(int is_user_float_on) {
        this.is_user_float_on = is_user_float_on;
    }

    public int getIs_logout_float_on() {
        return is_logout_float_on;
    }

    public void setIs_logout_float_on(int is_logout_float_on) {
        this.is_logout_float_on = is_logout_float_on;
    }

    public int getIs_reg_login_on() {
        return is_reg_login_on;
    }

    public void setIs_reg_login_on(int is_reg_login_on) {
        this.is_reg_login_on = is_reg_login_on;
    }

    public int getIs_visitor_on() {
        return is_visitor_on;
    }

    public void setIs_visitor_on(int is_visitor_on) {
        this.is_visitor_on = is_visitor_on;
    }

    public int getIs_visitor_on_phone() {
        return is_visitor_on_phone;
    }

    public void setIs_visitor_on_phone(int is_visitor_on_phone) {
        this.is_visitor_on_phone = is_visitor_on_phone;
    }

    public int getIs_auto_login_on() {
        return is_auto_login_on;
    }

    public void setIs_auto_login_on(int is_auto_login_on) {
        this.is_auto_login_on = is_auto_login_on;
    }

    public int getIs_log_on() {
        return is_log_on;
    }

    public void setIs_log_on(int is_log_on) {
        this.is_log_on = is_log_on;
    }

    public int getIs_shm() {
        return is_shm;
    }

    public void setIs_shm(int is_shm) {
        this.is_shm = is_shm;
    }

    public int getSwitch_login() {
        return switch_login;
    }

    public void setSwitch_login(int switch_login) {
        this.switch_login = switch_login;
    }

    public int getSkin() {
        return skin;
    }

    public void setSkin(int skin) {
        this.skin = skin;
    }

    public String getH5_game_url() {
        return h5_game_url;
    }

    public void setH5_game_url(String h5_game_url) {
        this.h5_game_url = h5_game_url;
    }

    public int getIs_service_float_on() {
        return is_service_float_on;
    }

    public void setIs_service_float_on(int is_service_float_on) {
        this.is_service_float_on = is_service_float_on;
    }

    public ChannelSdkListBean getChannel_sdk_list() {
        return channel_sdk_list;
    }

    public void setChannel_sdk_list(ChannelSdkListBean channel_sdk_list) {
        this.channel_sdk_list = channel_sdk_list;
    }

    public int getReg_flow_type() {
        return reg_flow_type;
    }

    public void setReg_flow_type(int reg_flow_type) {
        this.reg_flow_type = reg_flow_type;
    }

    public int getExpired() {
        return expired;
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    public int getOnline_report_interval() {
        return online_report_interval;
    }

    public void setOnline_report_interval(int online_report_interval) {
        this.online_report_interval = online_report_interval;
    }

    public String getShow_url_after_init() {
        return show_url_after_init;
    }

    public void setShow_url_after_init(String show_url_after_init) {
        this.show_url_after_init = show_url_after_init;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUser_agreement_url() {
        return user_agreement_url;
    }

    public void setUser_agreement_url(String user_agreement_url) {
        this.user_agreement_url = user_agreement_url;
    }

    public String getForget_password_url() {
        return forget_password_url;
    }

    public void setForget_password_url(String forget_password_url) {
        this.forget_password_url = forget_password_url;
    }

    public String getAdd_global_script_url() {
        return add_global_script_url;
    }

    public void setAdd_global_script_url(String add_global_script_url) {
        this.add_global_script_url = add_global_script_url;
    }

    public String getFloat_icon_url() {
        return float_icon_url;
    }

    public void setFloat_icon_url(String float_icon_url) {
        this.float_icon_url = float_icon_url;
    }

    public String getWeb_loading_url() {
        return web_loading_url;
    }

    public void setWeb_loading_url(String web_loading_url) {
        this.web_loading_url = web_loading_url;
    }

    public int getU_p_init() {
        return u_p_init;
    }

    public void setU_p_init(int u_p_init) {
        this.u_p_init = u_p_init;
    }

    public String getCustomer_service_url() {
        return customer_service_url;
    }

    public void setCustomer_service_url(String customer_service_url) {
        this.customer_service_url = customer_service_url;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public MoblieDirectLoginBean getMoblie_direct_login() {
        return moblie_direct_login;
    }

    public void setMoblie_direct_login(MoblieDirectLoginBean moblie_direct_login) {
        this.moblie_direct_login = moblie_direct_login;
    }

    public int getIcon_skin() {
        return icon_skin;
    }

    public void setIcon_skin(int icon_skin) {
        this.icon_skin = icon_skin;
    }

    public String getHgu() {
        return hgu;
    }

    public void setHgu(String hgu) {
        this.hgu = hgu;
    }

    public List<String> getCode_area_list() {
        return code_area_list;
    }

    public void setCode_area_list(List<String> code_area_list) {
        this.code_area_list = code_area_list;
    }

    public static class ChannelSdkListBean {
        /**
         * test : []
         * jrtt : {"app_id":"186554","app_name":"ztxj08","app_name_cn":"择天仙诀08","package_name":"com.jmhy.game.ztxj"}
         */

        private JrttBean jrtt;
        private List<?> test;

        public JrttBean getJrtt() {
            return jrtt;
        }

        public void setJrtt(JrttBean jrtt) {
            this.jrtt = jrtt;
        }

        public List<?> getTest() {
            return test;
        }

        public void setTest(List<?> test) {
            this.test = test;
        }

        public static class JrttBean {
            /**
             * app_id : 186554
             * app_name : ztxj08
             * app_name_cn : 择天仙诀08
             * package_name : com.jmhy.game.ztxj
             */

            private String app_id;
            private String app_name;
            private String app_name_cn;
            private String package_name;

            public String getApp_id() {
                return app_id;
            }

            public void setApp_id(String app_id) {
                this.app_id = app_id;
            }

            public String getApp_name() {
                return app_name;
            }

            public void setApp_name(String app_name) {
                this.app_name = app_name;
            }

            public String getApp_name_cn() {
                return app_name_cn;
            }

            public void setApp_name_cn(String app_name_cn) {
                this.app_name_cn = app_name_cn;
            }

            public String getPackage_name() {
                return package_name;
            }

            public void setPackage_name(String package_name) {
                this.package_name = package_name;
            }
        }
    }

    public static class MoblieDirectLoginBean {
        /**
         * type : aliyun
         * clientSecret : dYG3R2817Btq+mHwklkVO30+xhyxc8ZWQh5wIFYe9ebf6I4wltzmh7KVlSIpG5n5s1QHvsTFM1TEHpQYSWaRgmQMSpsyDQAUEBx3CZb0QABmNRe71rw/Q5DFzJBuoxXo4c2R3vPVIB961dTvNCgfR6VZtGQLgkovX7vYAlnWhc+C0bv4/leKaZyW5jgogmEwX6aHTprM/HsqB3dzs+dqt3M+n7ZejUb2ZevVlc31hizQm2sP7J+Jggg9vRFATHrnb+dNIYFAoIctj3ho9sJ8BkTz2Qr/1s7Q
         */

        private String type;
        private String clientSecret;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }
    }
}
