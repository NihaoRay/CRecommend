package com.marsdl.recommand.entity;

import java.util.List;

public class ItemUserList extends BaseEntity {

    private String itemId;
    private List<String> userList;

    public ItemUserList() {
    }

    public ItemUserList(String itemId, List<String> userList) {
        this.itemId = itemId;
        this.userList = userList;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
