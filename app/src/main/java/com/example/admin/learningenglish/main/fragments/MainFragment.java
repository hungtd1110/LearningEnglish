package com.example.admin.learningenglish.main.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.grammar.fragments.GrammarFragment;
import com.example.admin.learningenglish.listen.fragments.ListenFragment;
import com.example.admin.learningenglish.listen_response.fragments.ListenResponseFragment;
import com.example.admin.learningenglish.reading.fragments.ReadingFragment;
import com.example.admin.learningenglish.main.adapters.MainAdapter;
import com.example.admin.learningenglish.main.activities.MainActivity;
import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.dictionary.fragments.DictionaryFragment;
import com.example.admin.learningenglish.vocabulary.fragments.VocabularyFragment;

import java.util.ArrayList;

/**
 * Created by admin on 10/7/2017.
 */

public class MainFragment extends Fragment {
    private FragmentTransaction transaction;
    private GridView gvTrangChu;
    private ArrayList<Integer> dsDiem = new ArrayList<>();
    private ArrayList<Integer> dsTongDiem = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        addControl(view);
        addEvent();
        khoiTao();
//        getScore();

        return view;
    }

    private void getScore() {
        Database database = new Database(getActivity());

        //lấy điểm
        dsDiem.add(getScoreFromDatabase(database,"nghetraloi"));
        dsDiem.add(getScoreFromDatabase(database,"nguphap"));
        dsDiem.add(getScoreFromDatabase(database,"doc"));

        //lấy tổng điểm
        dsTongDiem.add(getSumScoreFromDatabase(database,"nghetraloi"));
        dsTongDiem.add(getSumScoreFromDatabase(database,"nguphap"));
        dsTongDiem.add(getSumScoreFromDatabase(database,"doc"));

//        String sql = "select sum(diem) from (select count(cauhoi.diem) as diem from nghetraloi, bai, cauhoi where nghetraloi.id = bai.idnghetraloi and " +
//                "bai.id = cauhoi.idbai group by nghetraloi.id)";
//        Cursor cursor = database.getData(sql);
//        dsTongDiem = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            dsTongDiem.add(Integer.parseInt(cursor.getString(0)));
//        }
    }

    private int getScoreFromDatabase(Database database, String table) {
        int score = 0;
        String sql = "select sum(diem) from " + table;
        Cursor cursor = database.getData(sql);
        while (cursor.moveToNext()) {
            score = cursor.getInt(0);
        }
        return score;
    }

    private int getSumScoreFromDatabase(Database database, String table) {
        int score = 0;
        String sql = "select sum(diem) from (select count(cauhoi.diem) as diem from " + table + ", bai, cauhoi where "+table+".id = bai.id"+table+" and " +
                "bai.id = cauhoi.idbai group by "+table+".id)";
        Cursor cursor = database.getData(sql);
        while (cursor.moveToNext()) {
            score = cursor.getInt(0);
        }
        return score;
    }

    private void khoiTao() {
        String[] ten = {
                "Dictionary",
                "Vocabulary",
                "Listen",
                "Listen - Response",
                "Grammar",
                "Reading"
        };

        int[] hinh = {
                R.drawable.dictionary,
                R.drawable.vocabulary,
                R.drawable.listen_sub,
                R.drawable.listen_response,
                R.drawable.grammar,
                R.drawable.reading
        };

        MainAdapter mainAdapter = new MainAdapter(getActivity(),ten,hinh);
        gvTrangChu.setAdapter(mainAdapter);
    }

    private void addEvent() {
        gvTrangChu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0 :
                        Fragment dictionaryFragment = new DictionaryFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, dictionaryFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 1:
                        Fragment vocabularyFragment = new VocabularyFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, vocabularyFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 2:
                        Fragment listenFragment = new ListenFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, listenFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 3:
                        Fragment listenResponseFragment = new ListenResponseFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, listenResponseFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 4:
                        Fragment grammarFragment = new GrammarFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, grammarFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case 5:
                        Fragment readingFragment = new ReadingFragment();
                        transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, readingFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                }
            }
        });
    }

    private void addControl(View view) {
        gvTrangChu = (GridView) view.findViewById(R.id.gvTrangChu);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.txtTitle.setText("Learning English");
        MainActivity.rlActionbarBot.setVisibility(View.GONE);
        MainActivity.imgTimKiem.setVisibility(View.GONE);
    }
}
