package com.kevin.chap7.digester;

import org.apache.commons.digester3.annotations.rules.BeanPropertySetter;
import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetProperty;

/**
 * @类名: Contact
 * @包名：com.kevin.chap7
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/26 15:47
 * @版本：1.0
 * @描述：
 */
@ObjectCreate(pattern = "address-book/contact")
public class Contact {

    @SetProperty(pattern = "address-book/contact")
    private String type;
    @BeanPropertySetter(pattern = "address-book/contact/name")
    private String name;
    @BeanPropertySetter(pattern = "address-book/contact/address")
    private String address;
    @BeanPropertySetter(pattern = "address-book/contact/city")
    private String city;
    @BeanPropertySetter(pattern = "address-book/contact/province")
    private String province;
    @BeanPropertySetter(pattern = "address-book/contact/postalcode")
    private String postalcode;
    @BeanPropertySetter(pattern = "address-book/contact/country")
    private String country;
    @BeanPropertySetter(pattern = "address-book/contact/telephone")
    private String telephone;

    public Contact() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
