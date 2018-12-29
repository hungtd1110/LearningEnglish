package com.example.admin.learningenglish.reading.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.learningenglish.R;

import java.util.ArrayList;

/**
 * Created by admin on 10/10/2017.
 */

public class ReadingAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> ten;
    private ArrayList<String> dsDiem;
    private ArrayList<String> dsTongDiem;

    private TextView txtTen, txtScore;

    public ReadingAdapter(Context context, ArrayList<String> ten, ArrayList<String> dsDiem, ArrayList<String> dsTongDiem) {
        this.context = context;
        this.ten = ten;
        this.dsDiem = dsDiem;
        this.dsTongDiem = dsTongDiem;
    }

    @Override
    public int getCount() {
        return ten.size();
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
        view = layoutInflater.inflate(R.layout.listview_reading_row,null);

        addControls(view, i);
        addEvents(i);

        return view;
    }

    private void addEvents(int i) {
    }

    private void addControls(View view, int i) {
        //Ánh xạ
        txtTen = (TextView) view.findViewById(R.id.txtTen);
        txtScore = (TextView) view.findViewById(R.id.txtScore);

        //xét giá trị
        txtTen.setText(ten.get(i));

        if (i < dsTongDiem.size()) {
            txtScore.setText("Score : " + dsDiem.get(i) + "/" + dsTongDiem.get(i));
        }
    }
}
