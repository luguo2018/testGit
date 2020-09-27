package com.jmhy.sdk.bean;

public class PayData {
    private int o_type;
    private String o_content;
    private String callback_url;
    private int real_name_needed;
    private String order_id;
    private String user_reg_date;
    private int target;

    public int getO_type() {
        return o_type;
    }

    public void setO_type(int o_type) {
        this.o_type = o_type;
    }

    public String getO_content() {
        return o_content;
    }

    public void setO_content(String o_content) {
        this.o_content = o_content;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }

    public int getReal_name_needed() {
        return real_name_needed;
    }

    public void setReal_name_needed(int real_name_needed) {
        this.real_name_needed = real_name_needed;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_reg_date() {
        return user_reg_date;
    }

    public void setUser_reg_date(String user_reg_date) {
        this.user_reg_date = user_reg_date;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "PayData{" +
                "o_type=" + o_type +
                ", o_content='" + o_content + '\'' +
                ", callback_url='" + callback_url + '\'' +
                ", real_name_needed=" + real_name_needed +
                ", order_id='" + order_id + '\'' +
                ", user_reg_date='" + user_reg_date + '\'' +
                ", target=" + target +
                '}';
    }
}