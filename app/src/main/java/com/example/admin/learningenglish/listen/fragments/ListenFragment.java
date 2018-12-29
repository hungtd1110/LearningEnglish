package com.example.admin.learningenglish.listen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.admin.learningenglish.listen.adapters.ListenAdapter;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.listen.activities.ContentListenActivity;
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

public class ListenFragment extends Fragment {
    private ArrayList<String> ten;
    private ListView lvNghe;
    private ArrayList<String> dsBaiHoc = new ArrayList<>();
    private String baiDangHoc = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen,container,false);

        addControl(view);
        getDatabase();
        khoiTao();
        addEvent();

        return view;
    }

    private void getDatabase() {
        Database database = new Database(getActivity());
        Cursor cursorListen = database.getData("select ten from nghesub");
        ten = new ArrayList<>();
        while (cursorListen.moveToNext()) {
            ten.add(cursorListen.getString(0));
        }
    }

    private void khoiTao() {
        ListenAdapter listenAdapter = new ListenAdapter(getActivity(),ten,dsBaiHoc);
        lvNghe.setAdapter(listenAdapter);
    }

    private void addEvent() {
        lvNghe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ContentListenActivity.class);
                String baiHoc = String.valueOf(i + 1);
                intent.putExtra("baihoc",baiHoc);
                startActivity(intent);
            }
        });

        MainActivity.btnHocTiep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baiHoc = getBaiHoc();
                Intent intent = new Intent(getActivity(), ContentListenActivity.class);
                intent.putExtra("baihoc",baiHoc);
                startActivity(intent);
            }
        });
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
        lvNghe = (ListView) view.findViewById(R.id.lvNghe);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.txtTitle.setText("Listen");
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
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Listen");
        if (!myFile.exists()){
            myFile.mkdirs();
        }

        try {
            FileInputStream fis = new FileInputStream(myFile.getAbsolutePath() + "/Status");
            ObjectInputStream ois = new ObjectInputStream(fis);
            dsBaiHoc = (ArrayList<String>) ois.readObject();
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
