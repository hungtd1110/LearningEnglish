package com.example.admin.learningenglish.listen_response.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.models.CauHoi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by admin on 10/25/2017.
 */

public class ExerciseListenResponseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CauHoi> dsCauHoi;
    private int cauHoiDau;
    private String baiHoc;
    private int status;

    private TextView txtDeBai;
    private RadioGroup rgListenResponse;
    private RadioButton rbDapAn1, rbDapAn2, rbDapAn3;

    private String dapAnChon = "";

    public ExerciseListenResponseAdapter(Context context, ArrayList<CauHoi> dsCauHoi, int cauHoiDau, String baiHoc, int status) {
        this.context = context;
        this.dsCauHoi = dsCauHoi;
        this.cauHoiDau = cauHoiDau;
        this.baiHoc = baiHoc;
        this.status = status;
    }

    @Override
    public int getCount() {
        return dsCauHoi.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.listview_exercise_listen_response_row,null);

        addControls(view, i);
        if (status == 0) {
            khoiTaoLamBai(i);
            addEvents(i);
        }
        else if (status == 1) {
            khoiTaoNopBai(i);
        }

        return view;
    }

    private void khoiTaoLamBai(int i) {
        rbDapAn1.setText(dsCauHoi.get(i).getDsDapAn().get(0).getDapAn());
        rbDapAn2.setText(dsCauHoi.get(i).getDsDapAn().get(1).getDapAn());
        rbDapAn3.setText(dsCauHoi.get(i).getDsDapAn().get(2).getDapAn());

        dapAnChon = "";
        saveAnswerSelected(i);
    }

    private void khoiTaoNopBai(int i) {
        rbDapAn1.setText(dsCauHoi.get(i).getDsDapAn().get(0).getDapAn());
        rbDapAn2.setText(dsCauHoi.get(i).getDsDapAn().get(1).getDapAn());
        rbDapAn3.setText(dsCauHoi.get(i).getDsDapAn().get(2).getDapAn());

        readAnswerSelected(i);

        String dapAnDung = "";
        for (int j = 0 ; j < dsCauHoi.get(i).getDsDapAn().size() ; j++) {
            if (dsCauHoi.get(i).getDsDapAn().get(j).getDapAnDung() == 1) {
                dapAnDung = dsCauHoi.get(i).getDsDapAn().get(j).getDapAn();
            }
        }

        //xét đáp án chọn
        if (rbDapAn1.getText().equals(dapAnChon)) {

            if (dapAnChon.equals(dapAnDung)) {
                rbDapAn1.setTextColor(Color.parseColor("#2E9AFE"));
                rbDapAn1.setButtonDrawable(R.drawable.dung);
            }
            else {
                rbDapAn1.setTextColor(Color.parseColor("#E2574C"));
                rbDapAn1.setButtonDrawable(R.drawable.sai);
            }

            rbDapAn1.setText("  " + dapAnChon);
        }

        if (rbDapAn2.getText().equals(dapAnChon)) {

            if (dapAnChon.equals(dapAnDung)) {
                rbDapAn2.setTextColor(Color.parseColor("#2E9AFE"));
                rbDapAn2.setButtonDrawable(R.drawable.dung);
            }
            else {
                rbDapAn2.setTextColor(Color.parseColor("#E2574C"));
                rbDapAn2.setButtonDrawable(R.drawable.sai);
            }

            rbDapAn2.setText("  " + dapAnChon);
        }

        if (rbDapAn3.getText().equals(dapAnChon)) {

            if (dapAnChon.equals(dapAnDung)) {
                rbDapAn3.setTextColor(Color.parseColor("#2E9AFE"));
                rbDapAn3.setButtonDrawable(R.drawable.dung);
            }
            else {
                rbDapAn3.setTextColor(Color.parseColor("#E2574C"));
                rbDapAn3.setButtonDrawable(R.drawable.sai);
            }

            rbDapAn3.setText("  " + dapAnChon);
        }

        //xét đáp án đúng
        if (rbDapAn1.getText().equals(dapAnDung)) {
            rbDapAn1.setTextColor(Color.parseColor("#2E9AFE"));
        }

        if (rbDapAn2.getText().equals(dapAnDung)) {
            rbDapAn2.setTextColor(Color.parseColor("#2E9AFE"));
        }

        if (rbDapAn3.getText().equals(dapAnDung)) {
            rbDapAn3.setTextColor(Color.parseColor("#2E9AFE"));
        }
    }

    private void readAnswerSelected(int i) {
        File myFile = new File(Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/AnswerSelected/Lesson" + baiHoc);
        if (!myFile.exists()) {
            myFile.mkdirs();
        }

        String cauHoi = String.valueOf(cauHoiDau + i);
        String path = myFile.getAbsolutePath() + "/Question" + cauHoi;

        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            dapAnChon = (String) ois.readObject();

            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addEvents(final int i) {
        for (int j = 0 ; j < dsCauHoi.get(i).getDsDapAn().size() ; j++) {
            final int finalJ = j;
            rgListenResponse.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dapAnChon = dsCauHoi.get(i).getDsDapAn().get(finalJ).getDapAn();
                    saveScore(view, i, dapAnChon);
                    saveAnswerSelected(i);
                }
            });
        }
    }

    private void saveScore(View view, int i, String dapAnChon) {
        int diem = 0;
        String dapAnDung = "";

        for (int j = 0 ; j < dsCauHoi.get(i).getDsDapAn().size() ; j++) {
            if (dsCauHoi.get(i).getDsDapAn().get(j).getDapAnDung() == 1) {
                dapAnDung = dsCauHoi.get(i).getDsDapAn().get(j).getDapAn();
            }
        }

        if (dapAnDung.equals(dapAnChon)) {
            diem ++;
        }

        Database database = new Database(view.getContext());
        String sql = "update cauhoi set diem = " + diem + " where id = " + dsCauHoi.get(i).getId();
        database.queryData(sql);
    }

    private void saveAnswerSelected(int i) {
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/AnswerSelected/Lesson" + baiHoc);
        if (!myFile.exists()){
            myFile.mkdirs();
        }

        String cauHoi = String.valueOf(cauHoiDau + i);
        String path = myFile.getAbsolutePath() + "/Question" + cauHoi;

        try {
            FileOutputStream fos = new FileOutputStream(path,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dapAnChon);

            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addControls(View view, int i) {
        //ánh xạ
        txtDeBai = (TextView) view.findViewById(R.id.txtDeBai);
        rgListenResponse = (RadioGroup) view.findViewById(R.id.rgListenResponse);
        rbDapAn1 = (RadioButton) view.findViewById(R.id.rbDapAn1);
        rbDapAn2 = (RadioButton) view.findViewById(R.id.rbDapAn2);
        rbDapAn3 = (RadioButton) view.findViewById(R.id.rbDapAn3);

        //xét giá trị
        txtDeBai.setText((cauHoiDau + i)+". " + dsCauHoi.get(i).getDeBai());
    }
}
