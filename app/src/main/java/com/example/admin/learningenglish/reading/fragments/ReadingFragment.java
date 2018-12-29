package com.example.admin.learningenglish.reading.fragments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.admin.learningenglish.reading.adapters.ReadingAdapter;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.reading.activities.ExerciseReadingActivity;
import com.example.admin.learningenglish.main.activities.MainActivity;
import com.example.admin.learningenglish.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by admin on 10/7/2017.
 */

public class ReadingFragment extends Fragment {
    private ArrayList<Integer> dsId;
    private ArrayList<String> ten;
    private ListView lvReading;
    private ArrayList<String> dsDiem = new ArrayList<>();
    private ArrayList<String> dsTongDiem = new ArrayList<>();
    private String baiDangHoc = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reading,container,false);

        addControl(view);
        getDatabase();
        khoiTao();
        addEvent();

        return view;
    }

    private void getDatabase() {
        Database database = new Database(getActivity());
        Cursor cursorListenResponse = database.getData("select * from doc");
        ten = new ArrayList<>();
        dsId = new ArrayList<>();
        while (cursorListenResponse.moveToNext()) {
            dsId.add(Integer.valueOf(cursorListenResponse.getString(0)));
            ten.add(cursorListenResponse.getString(1));
        }
    }

    private void khoiTao() {
        getScore();
        ReadingAdapter readingAdapter = new ReadingAdapter(getActivity(),ten,dsDiem,dsTongDiem);
        lvReading.setAdapter(readingAdapter);
    }

    private void getScore() {
        Database database = new Database(getActivity());

        //lấy điểm
        String sqlDiem = "select diem from doc";
        Cursor cursorDiem = database.getData(sqlDiem);
        dsDiem = new ArrayList<>();
        while (cursorDiem.moveToNext()) {
            dsDiem.add(cursorDiem.getString(0));
        }

        //lấy tổng điểm
        String sqlTongDiem = "select count(cauhoi.diem) from doc, bai, cauhoi where doc.id = bai.iddoc and " +
                "bai.id = cauhoi.idbai group by doc.id";
        Cursor cursorTongDiem = database.getData(sqlTongDiem);
        dsTongDiem = new ArrayList<>();
        while (cursorTongDiem.moveToNext()) {
            dsTongDiem.add(cursorTongDiem.getString(0));
        }
    }

    private void addEvent() {
        lvReading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Intent intent = new Intent(getActivity(), ExerciseReadingActivity.class);
                if (checkSuggest(i)) {
                    //thông báo gợi ý
                    final int baiGoiY = getBaiGoiY();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Gợi ý");
                    String message = "Bài \"" + ten.get(i) + "\" bạn đã làm tốt. Bạn có muốn làm sang bài \"" + ten.get(baiGoiY) + "\" không ?";
                    builder.setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String baiHoc = String.valueOf(dsId.get(baiGoiY));
                                    intent.putExtra("baihoc",baiHoc);
                                    startActivity(intent);
                                }
                            })
                            .setNeutralButton("Tiếp tục", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int k) {
                                    String baiHoc = String.valueOf(dsId.get(i));
                                    intent.putExtra("baihoc",baiHoc);
                                    startActivity(intent);
                                }
                            })
                            .create()
                            .show();
                }
                else {
                    String baiHoc = String.valueOf(dsId.get(i));
                    intent.putExtra("baihoc",baiHoc);
                    startActivity(intent);
                }
            }
        });

        MainActivity.btnHocTiep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baiHoc = getBaiHoc();
                Intent intent = new Intent(getActivity(), ExerciseReadingActivity.class);
                intent.putExtra("baihoc",baiHoc);
                startActivity(intent);
            }
        });
    }

    private int getBaiGoiY() {
        int baiGoiY = 0;
        for (int i = 0 ; i < dsTongDiem.size() - 1 ; i++) {
            int score1 = (Integer.parseInt(dsDiem.get(baiGoiY)) * 100) / Integer.parseInt(dsTongDiem.get(baiGoiY));
            int score2 = (Integer.parseInt(dsDiem.get(i + 1)) * 100) / Integer.parseInt(dsTongDiem.get(i + 1));
            if (score1 > score2) {
                baiGoiY = i + 1;
            }
        }
        return baiGoiY;
    }

    private boolean checkSuggest(int i) {
        boolean check = false;
        int scoreClick = (Integer.parseInt(dsDiem.get(i)) * 100) / Integer.parseInt(dsTongDiem.get(i));
        if (scoreClick >= 80) {
            for (int j = 0; j < dsTongDiem.size(); j++) {
                int score = (Integer.parseInt(dsDiem.get(j)) * 100) / Integer.parseInt(dsTongDiem.get(j));
                if (score < 80) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    private String getBaiHoc() {
        String baiHoc = "";
        for (int i = 0 ; i < ten.size() ; i++) {
            if (ten.get(i).equals(baiDangHoc)) {
                baiHoc = String.valueOf(i + 1);
                break;
            }
        }

        return baiHoc;
    }

    private void addControl(View view) {
        lvReading = (ListView) view.findViewById(R.id.lvReading);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.txtTitle.setText("Reading");
        MainActivity.imgTimKiem.setVisibility(View.GONE);

        readStatus();

        if (!baiDangHoc.equals("")) {
            MainActivity.rlActionbarBot.setVisibility(View.VISIBLE);
        }
        else {
            MainActivity.rlActionbarBot.setVisibility(View.GONE);
        }

        MainActivity.txtBaiHoc.setText("Đang học : \"" + baiDangHoc + "\"");
        khoiTao();
    }

    private void readStatus() {
        //tạo folder chua file
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Reading");
        if (!myFile.exists()){
            myFile.mkdirs();
        }

        try {
            FileInputStream fis = new FileInputStream(myFile.getAbsolutePath() + "/Status");
            ObjectInputStream ois = new ObjectInputStream(fis);
            baiDangHoc = (String) ois.readObject();

            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
