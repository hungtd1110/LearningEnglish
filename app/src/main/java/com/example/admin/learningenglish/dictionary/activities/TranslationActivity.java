package com.example.admin.learningenglish.dictionary.activities;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TranslationActivity extends AppCompatActivity {
    private TextView txtTitle;
    private ImageView imgChoose, imgSpeak;
    private int choose = 0;
    private WebView wvNghia;
    private TextView txtTu;
    private String word;
    private String content;
    private TextToSpeech tts;
    private ArrayList<String> dsTu = new ArrayList<>();
    private ArrayList<String> dsDate = new ArrayList<>();
    private ArrayList<String> dsChoose = new ArrayList<>();
    private ArrayList<String> dsDateChoose = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        addActionBar();
        getWord();
        addControl();
        khoiTao();
        addEvent();
    }

    private void khoiTao() {
        readListWord("WordChoosed");
        for (String w : dsChoose) {
            if (w.equals(word)) {
                imgChoose.setImageResource(R.drawable.star_yellow);
                choose = 1;
                break;
            }
        }

        wvNghia.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
        txtTu.setText(word);
    }

    private void getWord() {
        word = getIntent().getStringExtra("word");

        Database database = new Database(TranslationActivity.this);
        Cursor cursor = database.getData("select noidung from tudien where tu = '" + word + "'");
        while (cursor.moveToNext()) {
            content = cursor.getString(0);
        }
    }

    private void addActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imgChoose = (ImageView) findViewById(R.id.imgChoose);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Translation");
    }

    private void addEvent() {
        imgChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choose == 0) {
                    imgChoose.setImageResource(R.drawable.star_yellow);
                    getDataChoose();
                    choose = 1;
                }
                else {
                    imgChoose.setImageResource(R.drawable.star_white);
                    for (int i = 0 ; i < dsChoose.size() ; i++) {
                        if (dsChoose.get(i).equals(word)) {
                            dsChoose.remove(i);
                            dsDateChoose.remove(i);
                            break;
                        }
                    }
                    choose = 0;
                }

                saveListWord("WordChoosed",dsChoose,dsDateChoose);
            }
        });

        imgSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(TranslationActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i != TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.UK);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(txtTu.getText().toString(),TextToSpeech.QUEUE_ADD,null,null);
                            }
                        }
                    }
                });
            }
        });
    }

    private void getDataChoose() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());

        dsChoose.add("");
        dsDateChoose.add("");
        if (dsChoose.size() > 1) {
            for (int i = dsChoose.size() - 1; i > 0; i--) {
                dsChoose.set(i, dsChoose.get(i - 1));
                dsDateChoose.set(i, dsDateChoose.get(i - 1));
            }
        }
        dsChoose.set(0,word);
        dsDateChoose.set(0,date);
    }

    private void addControl() {
        wvNghia = (WebView) findViewById(R.id.wvNghia);
        txtTu = (TextView) findViewById(R.id.txtTu);
        imgSpeak = (ImageView) findViewById(R.id.imgSpeak);
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
    protected void onPause() {
        super.onPause();
        readListWord("WordTranslated");
        if (!checkWord()) {
            getData();
            saveListWord("WordTranslated",dsTu,dsDate);
        };
    }

    private void getData() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        String date = sdf.format(cal.getTime());

        if (dsTu.size() >= 30) {
            for (int i = 29 ; i > 0 ; i--) {
                dsTu.set(i,dsTu.get(i - 1));
                dsDate.set(i,dsDate.get(i - 1));
            }
            dsTu.set(0,word);
            dsDate.set(0,date);
        }
        else {
            dsTu.add("");
            dsDate.add("");
            if (dsTu.size() > 1) {
                for (int i = dsTu.size() - 1; i > 0; i--) {
                    dsTu.set(i, dsTu.get(i - 1));
                    dsDate.set(i, dsDate.get(i - 1));
                }
            }
            dsTu.set(0,word);
            dsDate.set(0,date);
        }
    }

    private void saveListWord(String fileName, ArrayList<String> Tu, ArrayList<String> Date) {
        String path = Environment.getExternalStorageDirectory() + "/LearningEnglish/Dictionary/" + fileName;

        try {
            FileOutputStream fos = new FileOutputStream(path,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(Tu);
            oos.writeObject(Date);

            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkWord() {
        for (String w : dsTu) {
            if (w.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private void readListWord(String fileName) {
        //tạo folder chua file
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Dictionary");
        if (!myFile.exists()){
            myFile.mkdirs();
        }
        try {
            FileInputStream fis = new FileInputStream(myFile.getAbsolutePath() + "/" + fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            if (fileName.equals("WordTranslated")) {
                dsTu = (ArrayList<String>) ois.readObject();
                dsDate = (ArrayList<String>) ois.readObject();
            }
            else if (fileName.equals("WordChoosed")) {
                dsChoose = (ArrayList<String>) ois.readObject();
                dsDateChoose = (ArrayList<String>) ois.readObject();
            }

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
