package com.example.admin.learningenglish.reading.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.reading.adapters.ExerciseReadingAdapter;
import com.example.admin.learningenglish.databases.Database;
import com.example.admin.learningenglish.models.Bai;
import com.example.admin.learningenglish.models.CauHoi;
import com.example.admin.learningenglish.models.DapAn;
import com.example.admin.learningenglish.models.Doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ExerciseReadingActivity extends AppCompatActivity {
    private Doc doc;

    private TextView txtTitle, txtScore;
    private ImageView imgNext, imgPre, imgPlay;
    private ListView lvExcerciseReading;
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
        setContentView(R.layout.activity_exercise_reading);
        addControl();
        getBaiHoc();
        addActionBar();
        getDatabase();
        khoiTao();
        addEvent();
    }

    private void getBaiHoc() {
        baiHoc = getIntent().getStringExtra("baihoc");
    }

    private void getDatabase() {
        Database database = new Database(this);

        Cursor cursorReading = database.getData("select ten from doc where id = " + baiHoc);
        while (cursorReading.moveToNext()) {
            doc = new Doc();
            doc.setTen(cursorReading.getString(0));

            //thêm danh sách bài
            String sqlBai = "select bai.id, bai.noidung from doc, bai where doc.id = " + baiHoc + " and doc.id = bai.iddoc";
            Cursor cursorBai = database.getData(sqlBai);
            ArrayList<Bai> dsBai = new ArrayList<>();
            while (cursorBai.moveToNext()) {
                Bai bai = new Bai();
                bai.setId(cursorBai.getString(0));
                bai.setNoiDung(cursorBai.getString(1));

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

            doc.setDsBai(dsBai);
        }

    }

    private void khoiTao() {
        ArrayList<CauHoi> dsCauHoi = doc.getDsBai().get(bai).getDsCauHoi();
        String noiDung = doc.getDsBai().get(bai).getNoiDung();
        ExerciseReadingAdapter exerciseReadingAdapter = new ExerciseReadingAdapter(this,dsCauHoi,cauHoiDau,baiHoc,status,noiDung);
        lvExcerciseReading.setAdapter(exerciseReadingAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reading,menu);
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
        int size = doc.getDsBai().size();
        String[] dsCauHoi = new String[size];
        for (int i = 0 ; i < size ; i ++) {
            dsCauHoi[i] = "Question " + (i*3 + 1) + " - " + (i*3 + 3);
        }
        return dsCauHoi;
    }

    private void addActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_custom_4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtScore = (TextView) findViewById(R.id.txtScore);
    }

    private void addEvent() {
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bai == doc.getDsBai().size() - 1) {
                    Toast.makeText(ExerciseReadingActivity.this, "Bạn đang ở cuối danh sách sách", Toast.LENGTH_SHORT).show();
                }
                else {
                    //kiểm tra đã submit trước đó chưa, nếu chưa refresh lại điểm các câu hỏi trước đó
                    if (status == 0) {
                        refreshScoreQuestion();
                    }

                    cauHoiDau += doc.getDsBai().get(bai).getDsCauHoi().size();

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
                    txtTitle.setText("Question " + cauHoiDau + " - " + (cauHoiDau + doc.getDsBai().get(bai).getDsCauHoi().size() - 1));
                    khoiTao();
                }
            }
        });

        imgPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bai == 0) {
                    Toast.makeText(ExerciseReadingActivity.this, "Bạn đang ở đầu danh sách sách", Toast.LENGTH_SHORT).show();
                }
                else {
                    //kiểm tra đã submit trước đó chưa, nếu chưa refresh lại điểm các câu hỏi trước đó
                    if (status == 0) {
                        refreshScoreQuestion();
                    }

                    bai --;
                    cauHoiDau -= doc.getDsBai().get(bai).getDsCauHoi().size();

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

                    txtTitle.setText("Question " + cauHoiDau + " - " + (cauHoiDau + doc.getDsBai().get(bai).getDsCauHoi().size() - 1));
                    khoiTao();
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status = 1;
                checkSumit = 1;
                khoiTao();
                addCauHoiDaLam();
                saveQuestionFinish();
                setScore();
            }
        });
    }

    private void setScore() {
        Database database = new Database(this);

        //lấy điểm
        String sqlDiem = "select sum(cauhoi.diem) from doc, bai, cauhoi where doc.id = " + baiHoc + " and doc.id = bai.iddoc and " +
                "bai.id = cauhoi.idbai group by doc.id";
        Cursor cursorDiem = database.getData(sqlDiem);
        int diem  = 0;
        while (cursorDiem.moveToNext()) {
            diem = Integer.parseInt(cursorDiem.getString(0));
        }

        //lấy tổng điểm
        String sqlTongDiem = "select count(cauhoi.diem) from doc, bai, cauhoi where doc.id = " + baiHoc + " and doc.id = bai.iddoc and " +
                "bai.id = cauhoi.idbai group by doc.id";
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
        for (int i = 0 ; i < doc.getDsBai().get(bai).getDsCauHoi().size() ; i++) {
            String sql = "update cauhoi set diem = 0 where id = " + doc.getDsBai().get(bai).getDsCauHoi().get(i).getId();
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

        File myFile = new File(Environment.getExternalStorageDirectory() + "/LearningEnglish/Reading/QuestionFinish");
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
        File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Reading/QuestionFinish");
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

    private void addControl() {
        lvExcerciseReading = (ListView) findViewById(R.id.lvExerciseReding);
        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgPre = (ImageView) findViewById(R.id.imgPre);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
    }

    protected void onPause() {
        super.onPause();

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
        String baiDangHoc = doc.getTen();

        String path = Environment.getExternalStorageDirectory() + "/LearningEnglish/Reading/Status";

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

        String sqlRead = "select sum(cauhoi.diem) from doc, bai, cauhoi where doc.id = " + baiHoc + " and doc.id = bai.iddoc and " +
                "bai.id = cauhoi.idbai group by doc.id";
        Cursor cursorRead = database.getData(sqlRead);
        String diem ="0";
        while (cursorRead.moveToNext()) {
            diem = cursorRead.getString(0);
        }

        //lưu điểm
        String sqlSave = "update doc set diem = " + diem + " where id = " + baiHoc;
        database.queryData(sqlSave);
    }

    private void refreshScore() {
        Database database = new Database(this);
        String sql = "update cauhoi set diem = 0 where cauhoi.id in (select cauhoi.id from doc, bai, cauhoi where " +
                "doc.id = " + baiHoc + " and doc.id = bai.iddoc and bai.id = cauhoi.idbai)";
        database.queryData(sql);
    }
}
