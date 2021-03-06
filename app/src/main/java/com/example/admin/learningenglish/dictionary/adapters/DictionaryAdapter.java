package com.example.admin.learningenglish.dictionary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.learningenglish.R;

/**
 * Created by admin on 10/9/2017.
 */

public class DictionaryAdapter extends BaseAdapter {
    private Context context;
    private String[] ten;
    private int[] hinh;

    public DictionaryAdapter(Context context, String[] ten, int[] hinh) {
        this.context = context;
        this.ten = ten;
        this.hinh = hinh;
    }

    @Override
    public int getCount() {
        return ten.length;
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
        view = layoutInflater.inflate(R.layout.listview_dictionary_row,null);

        //Ánh xạ
        ImageView imgHinh = (ImageView) view.findViewById(R.id.imgHinh);
        TextView txtTen = (TextView) view.findViewById(R.id.txtTen);

        //xét giá trị
        imgHinh.setImageResource(hinh[i]);
        txtTen.setText(ten[i]);

        return view;
    }
}
