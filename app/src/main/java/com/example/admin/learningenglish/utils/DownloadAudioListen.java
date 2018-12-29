package com.example.admin.learningenglish.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

/**
 * Created by admin on 10/20/2017.
 */

public class DownloadAudioListen {
    public void download(Context context, String downloadlink, String tenBaiHoc) {
        try {
            //tạo folder chua file
            File myFile = new File (Environment.getExternalStorageDirectory() + "/LearningEnglish/Listen/Audio");
            if (!myFile.exists()){
                myFile.mkdirs();
            }

            //yêu cầu download file và set các thuộc tính
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadlink));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir("/LearningEnglish/Listen/Audio", tenBaiHoc + ".mp3");    //đường dẫn file tải về

            //download file
            DownloadManager downloadmanager= (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadmanager.enqueue(request);
        }
        catch (Exception e) {
            Toast.makeText(context, "Lỗi " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
