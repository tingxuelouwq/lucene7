package com.kevin.chap7.digester;

import org.apache.commons.digester3.annotations.rules.ObjectCreate;
import org.apache.commons.digester3.annotations.rules.SetNext;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名: AddressBook
 * @包名：com.kevin.chap7.digester
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/31 18:15
 * @版本：1.0
 * @描述：
 */
@ObjectCreate(pattern = "address-book")
public class AddressBook {

    private List<Contact> contacts = new ArrayList<>();

    @SetNext
    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
