package com.example.admin.learningenglish.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by admin on 10/19/2017.
 */

public class Database extends SQLiteOpenHelper {
    private static final String databaseName = "dblearningenglish.sqlite";
    private Context context;
    private String duongDanDatabase;

    public Database(Context context) {
        super(context, databaseName, null, 1);
        this.context = context;
        duongDanDatabase = context.getFilesDir().getParent() + "/databases/" + databaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Truy vấn không trả về kết quả : create, insert,...
    public void queryData(String sql) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(sql);
    }

    //Truy vấn trả về kết quả select
    public Cursor getData(String sql) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery(sql,null);
    }

    //Tạo database
    public void createDatabase() {
        boolean kiemTra = kiemTraDatabase();
        if (kiemTra != true) {
            this.getWritableDatabase();
            copyDatabase();
        }
    }

    //Kiểm tra database đã tồn tại hay chưa
    private boolean kiemTraDatabase() {
        boolean kiemTra;
        try {
            //Mở database
            SQLiteDatabase.openDatabase(duongDanDatabase, null, SQLiteDatabase.OPEN_READWRITE);
            kiemTra = true;
        }
        catch (Exception e) {
            kiemTra = false;
        }
        return kiemTra;
    }

    //Copy database từ assets vào android
    public void copyDatabase() {
        try {
            InputStream is = context.getAssets().open(databaseName);
            OutputStream os = new FileOutputStream(duongDanDatabase);
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer,0,length);
            }
            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
