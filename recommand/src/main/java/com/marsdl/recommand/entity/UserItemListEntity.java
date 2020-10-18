package com.marsdl.recommand.entity;

public class UserItemListEntity implements Comparable<UserItemListEntity> {
    public String userId;
    public Double count;

    public UserItemListEntity() {
    }

    public UserItemListEntity(String userId, Double count) {
        this.userId = userId;
        this.count = count;
    }

    @Override
    public int compareTo(UserItemListEntity o) {
        if (this.count > o.count) {
            return 1;
        } else if (this.count < o.count) {
            return -1;
        }
        return 0;
    }
}
