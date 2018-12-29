package com.example.admin.learningenglish.models;

import java.util.ArrayList;

/**
 * Created by admin on 10/30/2017.
 */

public class NguPhap {
    private String id;
    private String ten;
    private String noiDung;
    private ArrayList<Bai> dsBai;

    public NguPhap() {
    }

    public NguPhap(String id, String ten, String noiDung, ArrayList<Bai> dsBai) {
        this.id = id;
        this.ten = ten;
        this.noiDung = noiDung;
        this.dsBai = dsBai;
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

    public ArrayList<Bai> getDsBai() {
        return dsBai;
    }

    public void setDsBai(ArrayList<Bai> dsBai) {
        this.dsBai = dsBai;
    }
}
