package com.example.admin.learningenglish.listen_response.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.listen_response.adapters.ExerciseListenResponseAdapter;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.models.Bai;
import com.example.admin.learningenglish.models.CauHoi;
import com.example.admin.learningenglish.models.DapAn;
import com.example.admin.learningenglish.models.NgheTraLoi;
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

public class ExerciseListenResponseActivity extends AppCompatActivity {
    private MediaPlayer media = new MediaPlayer();
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");   //định dạng hiển thị thời gian
    private int timeChange = 0; //thời gian tua
    private boolean audioLocal = false;
    private SeekBar skbAudio;
    private ProgressBar pbPlay;
    private TextView txtTimeCurrent, txtTime;
    private String urlAudio = "";
    private NgheTraLoi ngheTraLoi;

    private TextView txtTitle, txtScore;
    private ImageView imgNext, imgPre, imgPlay;
    private ListView lvNgheTraLoi;
    private Button btnSubmit;

    private String baiHoc;
    private ArrayList<String> dsCauDaLam = new ArrayList<>();
    private int bai = 0;
    private int cauHoiDau = 1;
    private int status = 0;
    private int checkSumit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_listen_response);
        addControl();
        getBaiHoc();
        addActionBar();
        getDatabase();
        checkAudio();
        getAudio();
        khoiTao();
        addEvent();
    }

    private void checkAudio() {
        String bai = "bai" + (cauHoiDau / 3 + 1);
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/Audio/" + ngheTraLoi.getTen() + "/" + bai + ".mp3");

        if (myFile.exists()) {
            audioLocal = true;
        }
        else {
            audioLocal = false;
        }
    }

    private void getAudio() {
        media = new MediaPlayer();  //có tạo mới ở đây thì stop mới không bị lỗi

        String bai = "bai" + (cauHoiDau / 3 + 1);
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/Audio/" + ngheTraLoi.getTen() + "/" + bai + ".mp3");

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
            imgPlay.setVisibility(View.INVISIBLE);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("nghetraloi").child("lesson" + baiHoc).child(bai).addValueEventListener(new ValueEventListener() {
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

    private void setTime() {
        txtTime.setText(sdf.format(media.getDuration()));
    }

    private void getBaiHoc() {
        baiHoc = getIntent().getStringExtra("baihoc");
    }

    private void getDatabase() {
        Database database = new Database(this);

        Cursor cursorListenResponse = database.getData("select ten from nghetraloi where id = " + baiHoc);
        while (cursorListenResponse.moveToNext()) {
            ngheTraLoi = new NgheTraLoi();
            ngheTraLoi.setTen(cursorListenResponse.getString(0));

            //thêm danh sách bài
            String sqlBai = "select bai.id from bai where idnghetraloi = " + baiHoc;
            Cursor cursorBai = database.getData(sqlBai);
            ArrayList<Bai> dsBai = new ArrayList<>();
            while (cursorBai.moveToNext()) {
                Bai bai = new Bai();
                bai.setId(cursorBai.getString(0));

                //thêm danh sách câu hỏi
                String sqlCauHoi = "select * from cauhoi where idbai = " + bai.getId();
                Cursor cursorCauHoi = database.getData(sqlCauHoi);
                ArrayList<CauHoi> dsCauHoi = new ArrayList<>();
                while (cursorCauHoi.moveToNext()) {
                    CauHoi cauHoi = new CauHoi();
                    cauHoi.setId(cursorCauHoi.getString(0));
                    cauHoi.setDeBai(cursorCauHoi.getString(1));

                    //thêm danh sách đáp án
                    String sqlDapAn = "select * from dapan where idcauhoi = " + cauHoi.getId();
                    Cursor cursorDapAn = database.getData(sqlDapAn);
                    ArrayList<DapAn> dsDapAn = new ArrayList<>();
                    while (cursorDapAn.moveToNext()) {
                        DapAn dapAn = new DapAn();
                        dapAn.setId(cursorDapAn.getString(0));
                        dapAn.setDapAn(cursorDapAn.getString(1));
                        dapAn.setDapAnDung(Integer.parseInt(cursorDapAn.getString(2)));
                        dsDapAn.add(dapAn);
                    }

                    cauHoi.setDsDapAn(dsDapAn);
                    dsCauHoi.add(cauHoi);
                }

                bai.setDsCauHoi(dsCauHoi);
                dsBai.add(bai);
            }

            ngheTraLoi.setDsBai(dsBai);
        }

    }

    private void khoiTao() {
        ArrayList<CauHoi> dsCauHoi = ngheTraLoi.getDsBai().get(bai).getDsCauHoi();
        ExerciseListenResponseAdapter exerciseListenResponseAdapter = new ExerciseListenResponseAdapter(this,dsCauHoi,cauHoiDau,baiHoc,status);
        lvNgheTraLoi.setAdapter(exerciseListenResponseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listen_response,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuDanhSach:
                xuLyDanhSach();
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

    private void xuLyDanhSach() {
        String[] dsCauHoi = getDanhSach();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("List question")
                .setItems(dsCauHoi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //kiểm tra đã submit trước đó chưa, nếu chưa refresh lại điểm các câu hỏi trước đó
                        if (status == 0) {
                            refreshScoreQuestion();
                        }

                        cauHoiDau = i*3 + 1;
                        int count = 0;
                        for (String cauHoi : dsCauDaLam) {
                            if (cauHoi.equals(cauHoiDau + "")) {
                                count ++;
                                status = 1;
                                break;
                            }
                        }
                        if (count == 0) {
                            status = 0;
                        }

                        bai = i;
                        txtTitle.setText("Question " + cauHoiDau + " - " + (cauHoiDau + 2));

                        khoiTao();

                        media.stop();
                        media.release();
                        imgPlay.setImageResource(R.drawable.play);
                        checkAudio();
                        getAudio();
                    }
                })
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private String[] getDanhSach() {
        int size = ngheTraLoi.getDsBai().size();
        String[] dsCauHoi = new String[size];
        for (int i = 0 ; i < size ; i ++) {
            dsCauHoi[i] = "Question " + (i*3 + 1) + " - " + (i*3 + 3);
        }
        return dsCauHoi;
    }

    private void addEvent() {
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bai == ngheTraLoi.getDsBai().size() - 1) {
                    Toast.makeText(ExerciseListenResponseActivity.this, "Bạn đang ở cuối danh sách sách", Toast.LENGTH_SHORT).show();
                }
                else {
                    //kiểm tra đã submit trước đó chưa, nếu chưa refresh lại điểm các câu hỏi trước đó
                    if (status == 0) {
                        refreshScoreQuestion();
                    }

                    cauHoiDau += ngheTraLoi.getDsBai().get(bai).getDsCauHoi().size();

                    int count = 0;
                    for (String cauHoi : dsCauDaLam) {
                        if (cauHoi.equals(cauHoiDau + "")) {
                            count ++;
                            status = 1;
                            break;
                        }
                    }
                    if (count == 0) {
                        status = 0;
                    }

                    bai ++;
                    txtTitle.setText("Question " + cauHoiDau + " - " + (cauHoiDau + ngheTraLoi.getDsBai().get(bai).getDsCauHoi().size() - 1));
                    khoiTao();

                    if (media.isPlaying()) {
                        media.stop();
                        media.release();
                    }
                    imgPlay.setImageResource(R.drawable.play);
                    getAudio();
                }
            }
        });

        imgPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bai == 0) {
                    Toast.makeText(ExerciseListenResponseActivity.this, "Bạn đang ở đầu danh sách sách", Toast.LENGTH_SHORT).show();
                }
                else {
                    //kiểm tra đã submit trước đó chưa, nếu chưa refresh lại điểm các câu hỏi trước đó
                    if (status == 0) {
                        refreshScoreQuestion();
                    }

                    bai --;
                    cauHoiDau -= ngheTraLoi.getDsBai().get(bai).getDsCauHoi().size();

                    int count = 0;
                    for (String cauHoi : dsCauDaLam) {
                        if (cauHoi.equals(cauHoiDau + "")) {
                            count ++;
                            status = 1;
                            break;
                        }
                    }
                    if (count == 0) {
                        status = 0;
                    }

                    txtTitle.setText("Question " + cauHoiDau + " - " + (cauHoiDau + ngheTraLoi.getDsBai().get(bai).getDsCauHoi().size() - 1));
                    khoiTao();

                    if (media.isPlaying()) {
                        media.stop();
                        media.release();
                    }
                    imgPlay.setImageResource(R.drawable.play);
                    getAudio();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isPlaying()) {
                    media.stop();
                    media.release();
                }
                imgPlay.setImageResource(R.drawable.play);
                getAudio();

                status = 1;
                checkSumit = 1;
                khoiTao();
                addCauHoiDaLam();
                saveQuestionFinish();
                setScore();
            }
        });

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

    private void setScore() {
        Database database = new Database(this);

        //lấy điểm
        String sqlDiem = "select sum(cauhoi.diem) from nghetraloi, bai, cauhoi where nghetraloi.id = " + baiHoc + " and nghetraloi.id = bai.idnghetraloi and " +
                "bai.id = cauhoi.idbai group by nghetraloi.id";
        Cursor cursorDiem = database.getData(sqlDiem);
        int diem  = 0;
        while (cursorDiem.moveToNext()) {
            diem = Integer.parseInt(cursorDiem.getString(0));
        }

        //lấy tổng điểm
        String sqlTongDiem = "select count(cauhoi.diem) from nghetraloi, bai, cauhoi where nghetraloi.id = " + baiHoc + " and nghetraloi.id = bai.idnghetraloi and " +
                "bai.id = cauhoi.idbai group by nghetraloi.id";
        Cursor cursorTongDiem = database.getData(sqlTongDiem);
        int tongDiem = 1;
        while (cursorTongDiem.moveToNext()) {
            tongDiem = Integer.parseInt(cursorTongDiem.getString(0));
        }

        int phanTram = (diem * 100)/tongDiem;
        txtScore.setText("Score : " + phanTram + "%");
    }

    private void refreshScoreQuestion() {
        Database database = new Database(this);
        for (int i = 0 ; i < ngheTraLoi.getDsBai().get(bai).getDsCauHoi().size() ; i++) {
            String sql = "update cauhoi set diem = 0 where id = " + ngheTraLoi.getDsBai().get(bai).getDsCauHoi().get(i).getId();
            database.queryData(sql);
        }
    }

    private void addCauHoiDaLam() {
        readQuestionFinish();
        int count = 0;
        for (String cauHoi : dsCauDaLam) {
            if (cauHoi.equals(cauHoiDau + "")) {
                count ++;
                break;
            }
        }
        if (count == 0) {
            dsCauDaLam.add(cauHoiDau + "");
        }
    }

    private void readQuestionFinish() {
        dsCauDaLam = new ArrayList<>();

        File myFile = new File(Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/QuestionFinish");
        if (!myFile.exists()) {
            myFile.mkdirs();
        }

        try {
            FileInputStream fis = new FileInputStream(myFile.getAbsolutePath() + "/Lesson" + baiHoc);
            ObjectInputStream ois = new ObjectInputStream(fis);
            dsCauDaLam = (ArrayList<String>) ois.readObject();

            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveQuestionFinish() {
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/QuestionFinish");
        if (!myFile.exists()){
            myFile.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(myFile.getAbsolutePath() + "/Lesson" + baiHoc,false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dsCauDaLam);

            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtScore = (TextView) findViewById(R.id.txtScore);
    }

    private void addControl() {
        lvNgheTraLoi = (ListView) findViewById(R.id.lvNgheTraLoi);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgPre = (ImageView) findViewById(R.id.imgPre);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        skbAudio = (SeekBar) findViewById(R.id.skbAudio);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtTimeCurrent = (TextView) findViewById(R.id.txtTimeCurrent);
        pbPlay = (ProgressBar) findViewById(R.id.pbPlay);

        pbPlay.setVisibility(View.GONE);
        pbPlay.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (media.isPlaying()) {
            media.stop();
            media.release();
        }

        dsCauDaLam = new ArrayList<>();
        saveQuestionFinish();

        //kiểm tra đã submit trước đó chưa, nếu chưa refresh lại điểm các câu hỏi trước đó
        if (status == 0) {
            refreshScoreQuestion();
        }

        //kiểm tra phải submit thì mới lưu điểm
        if (checkSumit == 1) {
            saveScore();
        }
        refreshScore();
        saveStatus();
    }

    private void saveStatus() {
        //lấy tên bài học
        String baiDangHoc = ngheTraLoi.getTen();

        String path = Environment.getExternalStorageDirectory() + "/LearningEnglish/ListenResponse/Status";

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

    private void saveScore() {
        //lấy điểm
        Database database = new Database(this);

        String sqlRead = "select sum(cauhoi.diem) from nghetraloi, bai, cauhoi where nghetraloi.id = " + baiHoc + " and nghetraloi.id = bai.idnghetraloi and " +
                "bai.id = cauhoi.idbai group by nghetraloi.id";
        Cursor cursorRead = database.getData(sqlRead);
        String diem ="0";
        while (cursorRead.moveToNext()) {
            diem = cursorRead.getString(0);
        }

        //lưu điểm
        String sqlSave = "update nghetraloi set diem = " + diem + " where id = " + baiHoc;
        database.queryData(sqlSave);
    }

    private void refreshScore() {
        Database database = new Database(this);
        String sql = "update cauhoi set diem = 0 where cauhoi.id in (select cauhoi.id from nghetraloi, bai, cauhoi where " +
                "nghetraloi.id = " + baiHoc + " and nghetraloi.id = bai.idnghetraloi and bai.id = cauhoi.idbai)";
        database.queryData(sql);
    }
}
