package com.marsdl.recommand.entity;

import org.springframework.data.annotation.Id;

public class BaseEntity {

    @Id
    protected String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
