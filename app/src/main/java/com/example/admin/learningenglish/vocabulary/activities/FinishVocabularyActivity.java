package com.example.admin.learningenglish.vocabulary.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.models.TuVung;
import com.example.admin.learningenglish.vocabulary.adapters.VocabularyAdapter;

import java.util.ArrayList;

public class FinishVocabularyActivity extends AppCompatActivity {
    private ArrayList<TuVung> listQuestion = new ArrayList<>();
    private ListView lvQuestion;
    private TextView txtPercent;

    private int answerTrue = 0;
    private int answerFalse = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_vocabulary);
        addActionBar();
        getData();
        addControls();
        addEvents();
        init();
    }

    private void addActionBar() {
        getSupportActionBar().setTitle("Finish");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        listQuestion = (ArrayList<TuVung>) bundle.getSerializable("list");
        answerTrue = bundle.getInt("answerTrue");
        answerFalse = bundle.getInt("answerFalse");
    }

    private void init() {
        VocabularyAdapter adapter = new VocabularyAdapter(getApplicationContext(), listQuestion, 0);
        lvQuestion.setAdapter(adapter);

        int percent = answerTrue*100/(answerTrue + answerFalse);
        txtPercent.setText(percent + "%");
    }

    private void addControls() {
        lvQuestion = (ListView) findViewById(R.id.lvQuestion);
        txtPercent = (TextView) findViewById(R.id.txtPercent);
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

    private void addEvents() {
    }
}
