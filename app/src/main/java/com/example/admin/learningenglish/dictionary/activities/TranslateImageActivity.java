package com.example.admin.learningenglish.dictionary.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.learningenglish.R;
import com.example.admin.learningenglish.databases.Database;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;

import static com.example.admin.learningenglish.R.id.txtNoiDung;

public class TranslateImageActivity extends AppCompatActivity {
    private WebView wvTranslate;
    private ImageView imgHinh, imgTranslation;
    private TextView txtWord;
    private Bitmap bitmap;
    private int REQUEST_CAMERA = 1;
    private int REQUEST_CROP = 2;
    private int REQUEST_GALLERY = 3;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_image);
        addActionBar();
        addControls();
        khoiTao();
        addEvents();
    }

    private void addEvents() {
        imgTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                    if (textRecognizer.isOperational()) {
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> sparseArray = textRecognizer.detect(frame);

                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < sparseArray.size(); i++) {
                            TextBlock textBlock = sparseArray.valueAt(i);
                            builder.append(textBlock.getValue());
                        }

                        txtWord.setText(builder.toString());
                        String data = getContent(builder.toString());
                        wvTranslate.loadDataWithBaseURL("", data, "text/html", "UTF-8", "");
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private String getContent(String s) {
        String data = s;
        Database database = new Database(TranslateImageActivity.this);
        Cursor cursor = database.getData("select noidung from tudien where tu = '" + data.toLowerCase() + "'");
        while (cursor.moveToNext()) {
            data = cursor.getString(0);
        }
        return data;
    }

    private void addActionBar() {
        getSupportActionBar().setTitle("Translate Image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void addControls() {
        wvTranslate = (WebView) findViewById(R.id.wvTranslate);
        imgHinh = (ImageView) findViewById(R.id.imgHinh);
        imgTranslation = (ImageView) findViewById(R.id.imgTranslation);
        txtWord = (TextView) findViewById(R.id.txtWord);
    }

    private void khoiTao() {
        imgHinh.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_translate_image,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.mnuCamera:
                openCamera();
                break;
            case R.id.menuGallery:
                openGallery();
                break;
        }
        return true;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void openCamera() {
        ActivityCompat.requestPermissions(TranslateImageActivity.this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(),"image.png");
                uri = Uri.fromFile(file.getAbsoluteFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, REQUEST_CAMERA);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            cropImage();
        }

        if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            bitmap = bundle.getParcelable("data");
            imgHinh.setImageBitmap(bitmap);
            imgHinh.setVisibility(View.VISIBLE);
        }

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            cropImage();
        }
}

    private void cropImage() {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CROP);
        }
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    }
