package com.example.admin.learningenglish.models;

import java.util.ArrayList;

/**
 * Created by admin on 10/26/2017.
 */

public class Bai {
    private String id;
    private String noiDung;
    private ArrayList<CauHoi> dsCauHoi;

    public Bai() {
    }

    public Bai(String id, String noiDung, ArrayList<CauHoi> dsCauHoi) {
        this.id = id;
        this.noiDung = noiDung;
        this.dsCauHoi = dsCauHoi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public ArrayList<CauHoi> getDsCauHoi() {
        return dsCauHoi;
    }

    public void setDsCauHoi(ArrayList<CauHoi> dsCauHoi) {
        this.dsCauHoi = dsCauHoi;
    }
}
