package com.marsdl.recommand.entity;

public class UserAndUserItemList {

    private String userId;

    private UserItemList userItemList;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserItemList getUserItemList() {
        return userItemList;
    }

    public void setUserItemList(UserItemList userItemList) {
        this.userItemList = userItemList;
    }
}
