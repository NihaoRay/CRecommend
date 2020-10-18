package com.marsdl.recommand.entity;

import java.util.List;

public class UserItemList extends BaseEntity{

    private String userId;
    private List<String> itemList;

    public UserItemList() {
    }

    public UserItemList(String userId, List<String> itemList) {
        this.userId = userId;
        this.itemList = itemList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getItemList() {
        return itemList;
    }

    public void setItemList(List<String> itemList) {
        this.itemList = itemList;
    }
}
