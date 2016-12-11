package com.pan.skating.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Mr.潘 on 2016/8/9.
 */
public class User extends BmobUser{
    private BmobFile head;
    private String sex;
    private String city;
    private String date;
    private String sign;

    public User() {
        super();
    }

    public User(BmobFile head, String sex, String city, String date, String sign) {
        this.head = head;
        this.sex = sex;
        this.city = city;
        this.date = date;
        this.sign = sign;
    }

    public BmobFile getHead() {
        return head;
    }

    public void setHead(BmobFile head) {
        this.head = head;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "User{" +
                "head=" + head +
                ", sex='" + sex + '\'' +
                ", city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
