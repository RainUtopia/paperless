package com.pa.paperless.bean;


/**
 * Created by Administrator on 2017/11/1.
 * 签到状态的信息列表bean
 */

public class SigninBean {
    String signin_name;
    int id;
    String signin_num;
    String signin_date;
    int sign_in;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSignin_num() {
        return signin_num;
    }

    public void setSignin_num(String signin_num) {
        this.signin_num = signin_num;
    }

    public String getSignin_name() {
        return signin_name;
    }

    public void setSignin_name(String signin_name) {
        this.signin_name = signin_name;
    }

    public String getSignin_date() {
        return signin_date;
    }

    public void setSignin_date(String signin_date) {
        this.signin_date = signin_date;
    }

    public int getSign_in() {
        return sign_in;
    }

    public void setSign_in(int sign_in) {
        this.sign_in = sign_in;
    }

    public SigninBean(int id, String signin_num, String signin_name) {

        this.id = id;
        this.signin_num = signin_num;
        this.signin_name = signin_name;
    }

    public SigninBean(int id, String signin_num, String signin_name, String signin_date, int sign_in) {

        this.id = id;
        this.signin_num = signin_num;
        this.signin_name = signin_name;
        this.signin_date = signin_date;
        this.sign_in = sign_in;
    }
}
