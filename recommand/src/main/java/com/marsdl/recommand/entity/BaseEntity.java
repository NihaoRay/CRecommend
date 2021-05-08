package com.marsdl.recommand.entity;

import org.springframework.data.annotation.Id;

public class BaseEntity {

    @Id
    protected String _id;
    /**
     * 编辑时间
     */
    protected long editTime;

    /**
     * 创建时间
     */
    protected long addTime;

    /**
     * 0 失效
     * 1 有效
     */
    protected int status;

    public BaseEntity() {
        this.addTime = System.currentTimeMillis();
        this.editTime = 0l;
        this.status = 1;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
