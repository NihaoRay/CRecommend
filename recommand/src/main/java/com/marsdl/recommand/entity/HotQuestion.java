package com.marsdl.recommand.entity;

public class HotQuestion extends BaseEntity {

    /**
     * 题目id
     */
    private String titleId;

    /**
     * 热度值
     */
    private Double hotValue;

    /**
     * 题目内容
     */
    private String titleContent;
    /**
     * 科目
     */
    private String course;

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public Double getHotValue() {
        return hotValue;
    }

    public void setHotValue(Double hotValue) {
        this.hotValue = hotValue;
    }

    public String getTitleContent() {
        return titleContent;
    }

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
