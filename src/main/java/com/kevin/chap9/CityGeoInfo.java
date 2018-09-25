package com.kevin.chap9;

import java.io.Serializable;

/**
 * 类名: CityGeoInfo<br/>
 * 包名：com.kevin.chap9<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/25 15:09<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class CityGeoInfo implements Serializable {

    private static final long serialVersionUID = -1276641642625592730L;
    private Long id;
    private String name;
    private Double longitude;
    private Double latitude;

    public CityGeoInfo(Long id, String name, Double longitude, Double latitude) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
