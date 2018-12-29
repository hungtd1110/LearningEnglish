package com.example.admin.learningenglish.models;

import java.util.ArrayList;

/**
 * Created by admin on 10/27/2017.
 */

public class NgheTraLoi {
    private String id;
    private String ten;
    private ArrayList<Bai> dsBai;

    public NgheTraLoi() {
    }

    public NgheTraLoi(String id, String ten, ArrayList<Bai> dsBai) {
        this.id = id;
        this.ten = ten;
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

    public ArrayList<Bai> getDsBai() {
        return dsBai;
    }

    public void setDsBai(ArrayList<Bai> dsBai) {
        this.dsBai = dsBai;
    }
}
