package com.example.admin.learningenglish.models;

import java.io.Serializable;

/**
 * Created by admin on 4/25/2018.
 */

public class TuVung implements Serializable{
    private String id;
    private String tu;
    private String tuLoai;
    private String nghia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTu() {
        return tu;
    }

    public void setTu(String tu) {
        this.tu = tu;
    }

    public String getTuLoai() {
        return tuLoai;
    }

    public void setTuLoai(String tuLoai) {
        this.tuLoai = tuLoai;
    }

    public String getNghia() {
        return nghia;
    }

    public void setNghia(String nghia) {
        this.nghia = nghia;
    }
}
