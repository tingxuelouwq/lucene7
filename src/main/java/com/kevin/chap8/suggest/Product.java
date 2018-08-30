package com.kevin.chap8.suggest;

import java.io.Serializable;

/**
 * 类名: Product<br/>
 * 包名：com.kevin.chap8.suggest<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/30 10:49<br/>
 * 版本：1.0<br/>
 * 描述：产品类<br/>
 */
public class Product implements Serializable {

    private static final long serialVersionUID = -3390163976903796306L;
    /** 产品名称 **/
    private String name;
    /** 产品图片 **/
    private String image;
    /** 产品销售地区 **/
    private String[] regions;
    /** 产品销售量 **/
    private int numberSold;

    public Product(String name, String image, String[] regions, int numberSold) {
        this.name = name;
        this.image = image;
        this.regions = regions;
        this.numberSold = numberSold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getRegions() {
        return regions;
    }

    public void setRegions(String[] regions) {
        this.regions = regions;
    }

    public int getNumberSold() {
        return numberSold;
    }

    public void setNumberSold(int numberSold) {
        this.numberSold = numberSold;
    }
}
