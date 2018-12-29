package com.example.admin.learningenglish.dictionary.adapters;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.learningenglish.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by admin on 12/1/2017.
 */

public class WordMarkAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> ten;
    private ArrayList<String> ngay;
    private TextToSpeech tts;

    public WordMarkAdapter(Context context, ArrayList<String> ten, ArrayList<String> ngay) {
        this.context = context;
        this.ten = ten;
        this.ngay = ngay;
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
        view = layoutInflater.inflate(R.layout.listview_workmark_row,null);

        final TextView txtTen = (TextView) view.findViewById(R.id.txtTen);
        ImageView imgSpeak = (ImageView) view.findViewById(R.id.imgSpeak);
        TextView txtDate = (TextView) view.findViewById(R.id.txtDate);

        txtTen.setText(ten.get(i));
        txtDate.setText(ngay.get(i));

        imgSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i != TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.UK);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(txtTen.getText().toString(),TextToSpeech.QUEUE_ADD,null,null);
                            }
                        }
                    }
                });
            }
        });

        return view;
    }
}
