package com.marsdl.recommand.entity;

public class RelateCountMatrixEntity implements Comparable<RelateCountMatrixEntity> {

    private String xAxis;

    private String yAxis;

    private Integer count;

    private double relateScore;

    public RelateCountMatrixEntity() {
    }

    public RelateCountMatrixEntity(double relateScore) {
        this.relateScore = relateScore;
    }

    public RelateCountMatrixEntity(String xAxis, String yAxis, Integer count) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.count = count;
    }

    public RelateCountMatrixEntity(String xAxis, String yAxis, Integer count, double relateScore) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.count = count;
        this.relateScore = relateScore;
    }

//    public RelateCountMatrixEntity(String xAxis, String yAxis, double relateScore) {
//        this.xAxis = xAxis;
//        this.yAxis = yAxis;
//        this.count = count;
//        this.relateScore = relateScore;
//    }

    public String getxAxis() {
        return xAxis;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getyAxis() {
        return yAxis;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public double getRelateScore() {
        return relateScore;
    }

    public void setRelateScore(double relateScore) {
        this.relateScore = relateScore;
    }

    @Override
    public int compareTo(RelateCountMatrixEntity o) {
        if (this.relateScore > o.relateScore) {
            return 1;
        } else if (this.relateScore < o.relateScore) {
            return -1;
        }
        return 0;
    }
}
