package com.example.admin.learningenglish.models;

/**
 * Created by admin on 10/19/2017.
 */

public class NgheSub {
    private String id;
    private String ten;
    private String noiDung;
    private String baiDich;

    public NgheSub() {
    }

    public NgheSub(String id, String ten, String noiDung, String baiDich) {
        this.id = id;
        this.ten = ten;
        this.noiDung = noiDung;
        this.baiDich = baiDich;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getBaiDich() {
        return baiDich;
    }

    public void setBaiDich(String baiDich) {
        this.baiDich = baiDich;
    }
}
