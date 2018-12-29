package com.example.admin.learningenglish.listen.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.dictionary.activities.TranslateImageActivity;
import com.example.admin.learningenglish.models.NgheSub;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ContentListenActivity extends AppCompatActivity {
    private MediaPlayer media = new MediaPlayer();
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");   //định dạng hiển thị thời gian
    private int timeChange = 0; //thời gian tua
    private int dich = 0;

    private String baiHoc;
    private String urlAudio;
    private boolean audioLocal;
    private String noiDung = "";
    private String baiDich = "";

    private NgheSub ngheSub;

    private TextView txtTitle;

    private ImageView imgPre, imgNext, imgPlay, imgStop;
    private TextView txtTimeCurrent, txtTime;
    private WebView wvContenListen;
    private SeekBar skbAudio;
    private ProgressBar pbPlay;

    private  ArrayList<String> dsBaiHoc = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_listen);
        addControl();
        getBaiHoc();
        getDatabase();
        addActionBar();
        checkAudio();
        getAudio();
        khoiTao();
        addEvent();
    }

    private void checkAudio() {
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Listen/Audio/"+ngheSub.getTen() + ".mp3");

        if (myFile.exists()) {
            audioLocal = true;
        }
        else {
            audioLocal = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listen,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuDich:
                if (dich == 0) {
                    wvContenListen.loadDataWithBaseURL("",baiDich,"text/html","UTF-8","");
                    item.setTitle("Stop translating");
                    dich = 1;
                }
                else {
                    wvContenListen.loadDataWithBaseURL("",noiDung,"text/html","UTF-8","");
                    item.setTitle("Translate");
                    dich = 0;
                }
                return true;
            case R.id.menu0_50:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    media.setPlaybackParams(media.getPlaybackParams().setSpeed(0.50f));
                }
                Toast.makeText(this, "Đã thay đổi tốc độ x0.5", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu0_75:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    media.setPlaybackParams(media.getPlaybackParams().setSpeed(0.75f));
                }
                Toast.makeText(this, "Đã thay đổi tốc độ x0.75", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu1_00:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    media.setPlaybackParams(media.getPlaybackParams().setSpeed(1.00f));
                }
                Toast.makeText(this, "Đã thay đổi tốc độ mặc định", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu1_25:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    media.setPlaybackParams(media.getPlaybackParams().setSpeed(1.25f));
                }
                Toast.makeText(this, "Đã thay đổi tốc độ x1.25", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu1_50:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    media.setPlaybackParams(media.getPlaybackParams().setSpeed(1.50f));
                }
                Toast.makeText(this, "Đã thay đổi tốc độ x1.5", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void khoiTao() {
        String[] arrNoiDung = ngheSub.getNoiDung().split("\n");
        String[] arrBaiDich = ngheSub.getBaiDich().split("\n");
        for (int i = 0 ; i < arrNoiDung.length ; i++) {
            noiDung += "<span style=\"color:#1C1C1C\">" + arrNoiDung[i] +"</span>";
            noiDung += "<br>";

            baiDich += "<span style=\"color:#1C1C1C\">" + arrNoiDung[i] +"</span>";
            baiDich += "<br>";
            baiDich += "<span style=\"color:#2E9AFE\">" + arrBaiDich[i] +"</span>";
            baiDich += "<br>";
        }
        wvContenListen.getSettings().setDefaultFontSize(18);
        wvContenListen.loadDataWithBaseURL("",noiDung,"text/html","UTF-8","");
    }

    private void getBaiHoc() {
        baiHoc = getIntent().getStringExtra("baihoc");
    }

    private void getDatabase() {
        Database database = new Database(this);
        Cursor cursorListen = database.getData("select * from nghesub where id = " + baiHoc);
        while (cursorListen.moveToNext()) {
            ngheSub = new NgheSub();
            ngheSub.setTen(cursorListen.getString(1));
            ngheSub.setNoiDung(cursorListen.getString(2));
            ngheSub.setBaiDich(cursorListen.getString(3));
        }
    }

    private void getAudio() {
        media = new MediaPlayer();  //có tạo mới ở đây thì stop mới không bị lỗi

        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Listen/Audio/"+ngheSub.getTen() + ".mp3");

        if (audioLocal == true) {
            media = MediaPlayer.create(this, Uri.parse(myFile.getAbsolutePath()));
            setTime();

            //gán max của seekbar = max của audio để set di chuyển seekbar đơn giản hơn
            skbAudio.setMax(media.getDuration());

            pbPlay.setVisibility(View.GONE);
            imgPlay.setVisibility(View.VISIBLE);

        }
        else {
            pbPlay.setVisibility(View.VISIBLE);
            imgPlay.setVisibility(View.GONE);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("nghesub").child("bai" + baiHoc).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    urlAudio = dataSnapshot.getValue().toString();
                    khoiTaoAudio();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void khoiTaoAudio() {
        try {
            media.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //load dữ liệu
            media.setDataSource(urlAudio);
            media.prepareAsync();

            //kiểm tra xem khi nào dữ liệu được load xong
            media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    //tắt progressbar khi đã load xong
                    pbPlay.setVisibility(View.GONE);
                    imgPlay.setVisibility(View.VISIBLE);

                    //gán max của seekbar = max của audio để set di chuyển seekbar đơn giản hơn
                    skbAudio.setMax(mediaPlayer.getDuration());

                    setTime();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText(ngheSub.getTen());
    }

    private void addEvent() {
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isPlaying()) {
                    media.pause();
                    imgPlay.setImageResource(R.drawable.play);
                }
                else {
                    media.start();
                    imgPlay.setImageResource(R.drawable.pause);
                    setTimeCurrent();
                }
            }
        });

        skbAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                media.seekTo(seekBar.getProgress());
            }
        });

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeChange = 10000;
            }
        });

        imgPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeChange = -10000;
            }
        });

        imgStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media.stop();
                media.release();
                imgPlay.setImageResource(R.drawable.play);
                getAudio();
            }
        });

    }

    private void setTimeCurrent() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtTimeCurrent.setText(sdf.format(media.getCurrentPosition() + timeChange));

                //set di chuyển cho seekbar
                skbAudio.setProgress(media.getCurrentPosition() + timeChange);

                if (timeChange != 0) {
                    //set lời audio trùng với trên seekbar
                    media.seekTo(skbAudio.getProgress());
                }

                //set lai thời gian tua = 0
                timeChange = 0;

                handler.postDelayed(this, 500);
            }
        },100);
    }

    private void setTime() {
        txtTime.setText(sdf.format(media.getDuration()));
    }

    private void addControl() {
        imgPre = (ImageView) findViewById(R.id.imgPre);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgStop = (ImageView) findViewById(R.id.imgStop);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtTimeCurrent = (TextView) findViewById(R.id.txtTimeCurrent);
        wvContenListen = (WebView) findViewById(R.id.wvContenListen);
        wvContenListen.getSettings().setJavaScriptEnabled(true);

        skbAudio = (SeekBar) findViewById(R.id.skbAudio);
        pbPlay = (ProgressBar) findViewById(R.id.pbPlay);

        pbPlay.setVisibility(View.GONE);
        pbPlay.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        mode.getMenu().clear();
        Menu menus = mode.getMenu();
        mode.getMenuInflater().inflate(R.menu.menu_webview,menus);
    }

    public void onItemWebViewClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnuDich:
                wvContenListen.evaluateJavascript("(function(){return window.getSelection().toString()})()",
                        new ValueCallback<String>()
                        {
                            @Override
                            public void onReceiveValue(String value)
                            {
                                value = value.replace("\"","").trim();
                                String data = translate(value);
                                hienThi(data,value);
                            }
                        });
                break;
            default:
                // ...
                break;
        }
    }

    private void hienThi(String data,String value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(value);
        LinearLayout linearLayout = new LinearLayout(ContentListenActivity.this);
        WebView wvContent = new WebView(ContentListenActivity.this);
        wvContent.loadDataWithBaseURL("",data,"text/html","UTF-8","");
        linearLayout.addView(wvContent);
        linearLayout.setPadding(40,0,10,0);
        builder.setView(linearLayout);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private String translate(String value) {
        String data = value;
        Database database = new Database(ContentListenActivity.this);
        Cursor cursor = database.getData("select noidung from tudien where tu = '" + data.toLowerCase() + "'");
        while (cursor.moveToNext()) {
            data = cursor.getString(0);
        }
        return data;
    }

    @Override
    protected void onResume() {
        super.onResume();
        readStatus();
    }

    private void readStatus() {
        String path = Environment.getExternalStorageDirectory() + "/LearningEnglish/Listen/Status";

        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            dsBaiHoc = (ArrayList<String>) ois.readObject();

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

    @Override
    protected void onPause() {
        super.onPause();
        media.stop();
        saveStatus();
    }

    private void saveStatus() {
        //kiểm tra dữ liệu trạng thái
        checkDanhSach();

        String path = Environment.getExternalStorageDirectory() + "/LearningEnglish/Listen/Status";

        try {
            FileOutputStream fos = new FileOutputStream(path,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dsBaiHoc);
            oos.writeObject(ngheSub.getTen());

            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkDanhSach() {
        int count = 0;
        for (String baiHoc : dsBaiHoc) {
            if (baiHoc.equals(ngheSub.getTen())) {
                count ++;
                break;
            }
        }
        if (count == 0) {
            dsBaiHoc.add(ngheSub.getTen());
        }
    }
}
