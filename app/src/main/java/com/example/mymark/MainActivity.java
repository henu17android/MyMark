package com.example.mymark;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mymark.watermark.WatermarkUtil;
import com.example.mymark.watermark.util.FileUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button_single);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WatermarkActivity.class);
                startActivity(intent);
            }
        });
        new ExtractFromAssetsToDataDataAsyncTask(false).execute();
    }

    //解压水印包，将assets下的水印压缩包复制到内存中
    private class ExtractFromAssetsToDataDataAsyncTask extends AsyncTask<Void, Void, Integer> {

        private boolean extract;
        private long startTime;

        public ExtractFromAssetsToDataDataAsyncTask(boolean extract) {
            this.extract = extract;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        protected void onPreExecute() {
            checkAndRequestPermissions();
        }

        @Override
        protected void onPostExecute(Integer integer) {

        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (extract) {
                try {
                    FileUtil.copyDirFromAssetsToDataData(getApplicationContext(), WatermarkUtil.WATERMARK_ROOT_DIR, false);
                    WatermarkUtil.extractWatermark(getApplicationContext().getFilesDir() + "/" + WatermarkUtil.WATERMARK_ROOT_DIR + "/" +
                            WatermarkUtil.WATERMARK_VIP_FILE_NAME + WatermarkUtil.WATERMARK_FILE_ZIP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void checkAndRequestPermissions() {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }

}
