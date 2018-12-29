package com.example.admin.learningenglish.models;

/**
 * Created by admin on 10/25/2017.
 */

public class DapAn {
    private String id;
    private String dapAn;
    private int dapAnDung;

    public DapAn() {
    }

    public DapAn(String id, String dapAn, int dapAnDung) {
        this.id = id;
        this.dapAn = dapAn;
        this.dapAnDung = dapAnDung;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDapAn() {
        return dapAn;
    }

    public void setDapAn(String dapAn) {
        this.dapAn = dapAn;
    }

    public int getDapAnDung() {
        return dapAnDung;
    }

    public void setDapAnDung(int dapAnDung) {
        this.dapAnDung = dapAnDung;
    }
}
