package com.marsdl.recommand.entity;

import java.util.List;

public class KeyMatrixEntity {

    private String key;
    private List<RelateCountMatrixEntity> matrixList;

    public KeyMatrixEntity() {
    }

    public KeyMatrixEntity(String key) {
        this.key = key;
    }

    public KeyMatrixEntity(String key, List<RelateCountMatrixEntity> matrixList) {
        this.key = key;
        this.matrixList = matrixList;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<RelateCountMatrixEntity> getMatrixList() {
        return matrixList;
    }

    public void setMatrixList(List<RelateCountMatrixEntity> matrixList) {
        this.matrixList = matrixList;
    }
}
