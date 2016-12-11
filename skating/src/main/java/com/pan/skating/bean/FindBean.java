package com.pan.skating.bean;

/**
 * Created by 潘洲涛 on 2016/10/18.
 */

public class FindBean {
    private int head;
    private String name;

    public FindBean() {
        super();
    }

    public FindBean(int head, String name) {
        this.head = head;
        this.name = name;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FindBean{" +
                "head=" + head +
                ", name='" + name + '\'' +
                '}';
    }
}
