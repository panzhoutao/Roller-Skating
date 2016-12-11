package com.pan.skating.bean;

import java.io.Serializable;
import java.util.List;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by 潘洲涛 on 2016/9/14.
 */
public class ActBean extends BmobObject implements Serializable{
    private String name;          //活动名称
    private String introduce;     //活动介绍
    private String starttime;     //开始时间
    private String endtime;       //结束时间
    private BmobGeoPoint point;   //坐标
    private String location;      //地点
    private List<BmobFile> pic;   //图片
    private String xuzhi;         //活动须知
    private String contact;       //联系方式
    private User author;          //发布者
    private Integer PV;           //浏览量

    public ActBean() {
        super();
    }

    public ActBean(String name, String introduce, String starttime, String endtime
            , BmobGeoPoint point, String location, List<BmobFile> pic, String xuzhi
            , String contact, User author) {
        this.name = name;
        this.introduce = introduce;
        this.starttime = starttime;
        this.endtime = endtime;
        this.point = point;
        this.location = location;
        this.pic = pic;
        this.xuzhi = xuzhi;
        this.contact = contact;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public BmobGeoPoint getPoint() {
        return point;
    }

    public void setPoint(BmobGeoPoint point) {
        this.point = point;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<BmobFile> getPic() {
        return pic;
    }

    public void setPic(List<BmobFile> pic) {
        this.pic = pic;
    }

    public String getXuzhi() {
        return xuzhi;
    }

    public void setXuzhi(String xuzhi) {
        this.xuzhi = xuzhi;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Integer getPV() {
        return PV;
    }

    public void setPV(Integer PV) {
        this.PV = PV;
    }

    @Override
    public String toString() {
        return "ActBean{" +
                "name='" + name + '\'' +
                ", introduce='" + introduce + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", point=" + point +
                ", location='" + location + '\'' +
                ", pic=" + pic +
                ", xuzhi='" + xuzhi + '\'' +
                ", contact='" + contact + '\'' +
                ", author=" + author +
                '}';
    }
}
