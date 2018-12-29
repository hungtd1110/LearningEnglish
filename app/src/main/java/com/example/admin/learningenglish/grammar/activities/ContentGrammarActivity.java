package com.example.admin.learningenglish.grammar.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.models.NguPhap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ContentGrammarActivity extends AppCompatActivity {
    private String baiHoc;
    private NguPhap nguPhap;

    TextView txtTitle;

    WebView wvContentGrammar;
    FloatingActionButton fabContentGrammar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_grammar);
        addControl();
        getBaiHoc();
        getDatabase();
        addActionBar();
        khoiTao();
        addEvent();
    }

    private void khoiTao() {
        String noiDung = nguPhap.getNoiDung();
        wvContentGrammar.getSettings().setDefaultFontSize(16);
        wvContentGrammar.loadDataWithBaseURL("",noiDung,"text/html","UTF-8","");
    }

    private void getBaiHoc() {
        baiHoc = getIntent().getStringExtra("baihoc");
    }

    private void getDatabase() {
        Database database = new Database(this);
        Cursor cursorGrammar = database.getData("select * from nguphap where id = " + baiHoc);
        while (cursorGrammar.moveToNext()) {
            nguPhap = new NguPhap();
            nguPhap.setTen(cursorGrammar.getString(1));
            nguPhap.setNoiDung(cursorGrammar.getString(2));
        }
    }

    private void addActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        txtTitle.setText(nguPhap.getTen());


    }

    private void addEvent() {
        fabContentGrammar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentGrammarActivity.this, ExerciseGrammarActivity.class);
                intent.putExtra("baihoc",baiHoc);
                startActivity(intent);
            }
        });
    }

    private void addControl() {
        wvContentGrammar = (WebView) findViewById(R.id.wvContentGrammar);
        fabContentGrammar = (FloatingActionButton) findViewById(R.id.fabContentGrammar);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveStatus();
    }

    private void saveStatus() {
        //lấy tên bài học
        String baiDangHoc = nguPhap.getTen();

        String path = Environment.getExternalStorageDirectory() + "/LearningEnglish/Grammar/Status";

        try {
            FileOutputStream fos = new FileOutputStream(path,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(baiDangHoc);

            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
