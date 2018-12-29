package com.example.admin.learningenglish.listen_response.adapters;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.utils.DownloadAudioListenResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by admin on 10/21/2017.
 */

public class ListenResponseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> ten;
    private ArrayList<String> dsDiem;
    private ArrayList<String> dsTongDiem;

    private ImageView imgDownload;
    private TextView txtTen, txtScore;

    public ListenResponseAdapter(Context context, ArrayList<String> ten, ArrayList<String> dsDiem, ArrayList<String> dsTongDiem) {
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
        view = layoutInflater.inflate(R.layout.listview_listen_response_row,null);

        addControls(view, i);
        addEvents(i);

        return view;
    }

    private void addEvents(final int i) {
        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baiHoc = String.valueOf(i + 1);
                final String tenBaiHoc = ten.get(i);
                File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/Audio/"+tenBaiHoc);
                if (myFile.exists()) {
                    Toast.makeText(context, "Bài học đã được tải về", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    for (int j = 1; j <= 4 ; j++) {
                        final String bai = "bai" + j;
                        mDatabase.child("nghetraloi").child("lesson" + baiHoc).child(bai).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String downloadlink = dataSnapshot.getValue().toString();
                                DownloadAudioListenResponse downloadAudioListenResponse = new DownloadAudioListenResponse();
                                downloadAudioListenResponse.download(context, downloadlink, tenBaiHoc, bai);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });
    }

    private void addControls(View view, int i) {
        //Ánh xạ
        imgDownload = (ImageView) view.findViewById(R.id.imgDownload);
        txtTen = (TextView) view.findViewById(R.id.txtTen);
        txtScore = (TextView) view.findViewById(R.id.txtScore);

        //xét giá trị
        txtTen.setText(ten.get(i));

        if (i < dsTongDiem.size()) {
            txtScore.setText("Score : " + dsDiem.get(i) + "/" + dsTongDiem.get(i));
        }

        String tenBaiHoc = ten.get(i);
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/Audio/"+tenBaiHoc);
        if (myFile.exists()) {
            imgDownload.setImageResource(R.drawable.success);
        }
        else {
            imgDownload.setImageResource(R.drawable.download);
        }
    }
}
