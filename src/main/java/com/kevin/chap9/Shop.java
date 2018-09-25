package com.kevin.chap9;

import java.io.Serializable;

/**
 * 类名: Shop<br/>
 * 包名：com.kevin.chap9<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/25 14:44<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class Shop implements Serializable {

    private static final long serialVersionUID = 6452093065014373961L;
    private Integer id;
    private String name;
    private String address;
    private Double longitude;
    private Double latitude;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
