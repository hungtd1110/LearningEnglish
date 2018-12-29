package com.example.admin.learningenglish.vocabulary.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.models.TuVung;

import java.util.ArrayList;
import java.util.Random;

public class PracticeVocabularyActivity extends AppCompatActivity {
    private String kindPractice, numberWord;
    private ArrayList<TuVung> listVocabulary = new ArrayList<>();
    private ArrayList<TuVung> listQuestion = new ArrayList<>();
    private ArrayList<TuVung> list = new ArrayList<>();

    private Button btnCheck;
    private TextView txtQuestion, txtAnswer;
    private EditText edtAnswer;

    private int question = 0;
    private int numberAnswer = 0;
    private int answerTrue = 0;
    private int answerFalse = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_vocabulary);
        addActionBar();
        getDatabase();
        getData();
        addControls();
        addEvents();
        init();
    }

    private void addActionBar() {
        getSupportActionBar().setTitle("Practice");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        Random random = new Random();
        for (int i = 0; i < Integer.parseInt(numberWord); i++) {
            int index = random.nextInt(listVocabulary.size());
            listQuestion.add(listVocabulary.get(index));
            list.add(listVocabulary.get(index));
            listVocabulary.remove(index);
        }

        if (kindPractice.equals("Việt - Anh")) {
            txtQuestion.setText("/" + listQuestion.get(0).getTuLoai() + "/ " + listQuestion.get(0).getNghia());
        }
        else {
            txtQuestion.setText("/" + listQuestion.get(0).getTuLoai() + "/ " + listQuestion.get(0).getTu());
        }
    }

    private void getDatabase() {
        Database database = new Database(this);
        String sql = "select * from tuvung";
        Cursor cursor = database.getData(sql);
        listVocabulary.clear();
        while (cursor.moveToNext()) {
            TuVung tuVung = new TuVung();
            tuVung.setId(cursor.getString(0));
            tuVung.setTu(cursor.getString(1));
            tuVung.setTuLoai(cursor.getString(2));
            tuVung.setNghia(cursor.getString(3));

            listVocabulary.add(tuVung);
        }
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        numberWord = bundle.getString("numberWord");
        kindPractice = bundle.getString("kindPractice");
    }

    private void addControls() {
        btnCheck = (Button) findViewById(R.id.btnCheck);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        txtAnswer = (TextView) findViewById(R.id.txtAnswer);
        edtAnswer = (EditText) findViewById(R.id.edtAnswer);
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
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xử lý kiểm tra
                boolean check = checkAnswer(edtAnswer.getText().toString());

                TuVung tuVung = listQuestion.get(question);
                if (kindPractice.equals("Việt - Anh")) {
                    txtAnswer.setText(tuVung.getTu());
                }
                else {
                    txtAnswer.setText(tuVung.getNghia());
                }
                txtAnswer.setVisibility(View.VISIBLE);

                if (check) {
                    //xử lý trả lời đúng
                    answerTrue();
                    answerTrue++;
                }
                else {
                    //xử lý trả lời sai
                    answerFalse();
                    answerFalse++;
                }
                

            }
        });
    }

    private void answerFalse() {
        txtAnswer.setTextColor(getResources().getColor(R.color.vocaularyFalse));
        Toast.makeText(this, "Trả lời sai", Toast.LENGTH_SHORT).show();
        numberAnswer++;
    }

    private void answerTrue() {
        txtAnswer.setTextColor(getResources().getColor(R.color.vocaularyTrue));
        Toast.makeText(this, "Trả lời chính xác", Toast.LENGTH_SHORT).show();

        if (numberAnswer == 0) {
            listQuestion.remove(question);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listQuestion.size() > 0) {
                    nextQuestion();
                }
                else {
                    //xử lý khi hoàn thành
                    Intent intent = new Intent(getApplicationContext(),FinishVocabularyActivity.class);
                    intent.putExtra("list", list);
                    intent.putExtra("answerTrue", answerTrue);
                    intent.putExtra("answerFalse", answerFalse);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2500);
    }

    private void nextQuestion() {
        numberAnswer = 0;
        txtAnswer.setVisibility(View.GONE);
        edtAnswer.setText("");

        Random random = new Random();
        question = random.nextInt(listQuestion.size());
        if (kindPractice.equals("Việt - Anh")) {
            txtQuestion.setText("/" + listQuestion.get(question).getTuLoai() + "/ " + listQuestion.get(question).getNghia());
        }
        else {
            txtQuestion.setText("/" + listQuestion.get(question).getTuLoai() + "/ " + listQuestion.get(question).getTu());
        }
    }

    private boolean checkAnswer(String s) {
        Database database = new Database(getApplicationContext());
        TuVung tuVung = listQuestion.get(question);
        String answer = "";
        String sql = "";

        if (kindPractice.equals("Việt - Anh")) {
            sql = "select tu from tuvung where nghia = '" + tuVung.getNghia() + "' and tuloai = '" + tuVung.getTuLoai() + "'";
        }
        else {
            sql = "select nghia from tuvung where tu = '" + tuVung.getTu() + "' and tuloai = '" + tuVung.getTuLoai() + "'";
        }

        Cursor cursor = database.getData(sql);
        while (cursor.moveToNext()) {
            answer = cursor.getString(0);
            break;
        }

        if (answer.equals(s)) {
            return true;
        }

        return false;
    }
}
