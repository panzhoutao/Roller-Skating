package com.pan.skating.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 潘洲涛 on 2016/10/12.
 */

public class VideoBean extends BmobObject{

    private BmobFile video;     //视频
    private String introduce;   //介绍
    private User author;        //发布者
    private Integer PV;         //播放量

    public VideoBean() {
        super();
    }

    public VideoBean(String tableName, BmobFile video, String introduce, User author, Integer PV) {
        super(tableName);
        this.video = video;
        this.introduce = introduce;
        this.author = author;
        this.PV = PV;
    }

    public BmobFile getVideo() {
        return video;
    }

    public void setVideo(BmobFile video) {
        this.video = video;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
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
        return "VideoBean{" +
                "video=" + video +
                ", introduce='" + introduce + '\'' +
                ", author=" + author +
                ", PV=" + PV +
                '}';
    }
}
