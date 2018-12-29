package com.example.admin.learningenglish.dictionary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.dictionary.adapters.WordMarkAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class WordMarkActivity extends AppCompatActivity {
    private ArrayList<String> dsTu = new ArrayList<>();
    private ArrayList<String> dsDate = new ArrayList<>();
    private ListView lvWordMark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_mark);
        addActionBar();
        addControl();
        khoiTao();
        addEvent();
    }

    private void khoiTao() {
        readListWord();
        WordMarkAdapter adapter = new WordMarkAdapter(WordMarkActivity.this,dsTu,dsDate);
        lvWordMark.setAdapter(adapter);
    }

    private void addActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Work Mark");
    }

    private void addEvent() {
        lvWordMark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WordMarkActivity.this,TranslationActivity.class);
                intent.putExtra("word",dsTu.get(i));
                startActivity(intent);
            }
        });
    }

    private void addControl() {
        lvWordMark = (ListView) findViewById(R.id.lvWordMark);
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

    @Override
    protected void onResume() {
        super.onResume();
        khoiTao();
    }

    private void readListWord() {
        //tạo folder chua file
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Dictionary");
        if (!myFile.exists()){
            myFile.mkdirs();
        }
        try {
            FileInputStream fis = new FileInputStream(myFile.getAbsolutePath() + "/WordChoosed");
            ObjectInputStream ois = new ObjectInputStream(fis);
            dsTu = (ArrayList<String>) ois.readObject();
            dsDate = (ArrayList<String>) ois.readObject();

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
