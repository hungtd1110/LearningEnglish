package com.example.admin.learningenglish.dictionary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.learningenglish.R;

import java.util.ArrayList;

/**
 * Created by admin on 12/1/2017.
 */

public class SuggestAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> dsTen;

    public SuggestAdapter(Context context, ArrayList<String> dsTen) {
        this.context = context;
        this.dsTen = dsTen;
    }

    @Override
    public int getCount() {
        return dsTen.size();
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
        view = layoutInflater.inflate(R.layout.listview_suggest_row,null);

        TextView txtTen = (TextView) view.findViewById(R.id.txtTen);
        txtTen.setText(dsTen.get(i));

        return view;
    }
}
