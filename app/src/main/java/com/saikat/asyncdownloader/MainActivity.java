package com.saikat.asyncdownloader;


import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText selectionText;
    private String url5mb = "https://firebasestorage.googleapis.com/v0/b/myblog-18d9c.appspot.com/o/Handwashing.pdf?alt=media&token=4f29f8e1-7e51-42a2-ae40-3449be1b7ced";
    private String url33mb = "https://firebasestorage.googleapis.com/v0/b/myblog-18d9c.appspot.com/o/2019.pdf?alt=media&token=947408ea-6731-4350-81ef-6ce4e2a52c96";
    private ProgressBar downloadImagesProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectionText = findViewById(R.id.downloadURL);
        downloadImagesProgress = findViewById(R.id.downloadProgress);
        selectionText.setText(url33mb);
    }


    public void downloadImage(View view){
        if(selectionText.getText().toString() != null
            && selectionText.getText().toString().length() >0){
            String url = selectionText.getText().toString();
            MyTask myTask = new MyTask();
            myTask.execute(url);
        }

    }



    private class MyTask extends AsyncTask<String ,Integer, Boolean>{

        private int contentLength = -1;
        private int counter = 0;
        private int calculatedProgress = 0;

        @Override
        protected void onPreExecute() {
            downloadImagesProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... urls) {

            boolean successful = false;
            URL downloadURL = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            File file = null;
            try {
                downloadURL = new URL(urls[0]);
                connection = (HttpURLConnection) downloadURL.openConnection();
                contentLength = connection.getContentLength();
                inputStream = connection.getInputStream();
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath()+"/abcd.pdf");
                fileOutputStream = new FileOutputStream(file);
                int read = -1;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer)) != -1){
                    Log.d("XXXMainActivity", "0 - "+read);
                    fileOutputStream.write(buffer,0,read);
                    counter += read;
                    publishProgress(counter);
                }
                successful = true;
            } catch (MalformedURLException e) {
                Log.d("XXXMainActivity", "1 - "+e.getMessage());
            } catch (IOException e) {
                Log.d("XXXMainActivity", "2 - "+e.getMessage());
            }
            finally {
                if(connection != null){
                    connection.disconnect();
                }
                if(inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {

                    }
                }
                if(fileOutputStream != null){
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {

                    }
                }
            }

            return successful;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            calculatedProgress = (int)(((double)values[0]/contentLength)*100);
            downloadImagesProgress.setProgress(calculatedProgress);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            downloadImagesProgress.setVisibility(View.GONE);
        }
    }


}

