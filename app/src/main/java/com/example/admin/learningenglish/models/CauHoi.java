package com.example.admin.learningenglish.models;

import java.util.ArrayList;

/**
 * Created by admin on 10/25/2017.
 */

public class CauHoi {
    private String id;
    private String deBai;
    private ArrayList<DapAn> dsDapAn;

    public CauHoi() {
    }

    public CauHoi(String id, String deBai, ArrayList<DapAn> dsDapAn) {
        this.id = id;
        this.deBai = deBai;
        this.dsDapAn = dsDapAn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeBai() {
        return deBai;
    }

    public void setDeBai(String deBai) {
        this.deBai = deBai;
    }

    public ArrayList<DapAn> getDsDapAn() {
        return dsDapAn;
    }

    public void setDsDapAn(ArrayList<DapAn> dsDapAn) {
        this.dsDapAn = dsDapAn;
    }
}
