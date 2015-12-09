package com.softtanck.hlsdownload;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void downFile(View view) {
//        new downLoadTsFile().execute("http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8");
        File file = new File(getApplicationContext().getExternalCacheDir().getAbsolutePath()+File.separator+"test");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Tanck", getApplicationContext().getExternalCacheDir().getAbsolutePath());
    }


    private class downLoadTsFile extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(String... params) {

            Log.d("Tanck", "start....");

            try {
                list = new ArrayList<>();
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");// 设置请求方法为post
                conn.setReadTimeout(5000);// 设置读取超时为5秒
                conn.setConnectTimeout(10000);// 设置连接网络超时为10秒
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.startsWith("#")) {
                            //这里是Metadata信息
                        } else if (line.length() > 0 && line.contains(".ts")) {
                            if (line.startsWith("http")) {//绝对地址
                                list.add(line);
                            } else { // 相对地址
                                String ts_url;
                                String m3u8_postfixname = params[0].substring(
                                        params[0].lastIndexOf("/") + 1, params[0].length());
                                if (m3u8_postfixname.equals("prog_index.m3u8")) {
                                    ts_url = params[0].replace("prog_index.m3u8", line);
                                    list.add(ts_url);
                                }
                            }
                        }
                    }
                    is.close();
                    bufferedReader.close();
                    Log.d("Tanck", "--->" + list);
                } else {
                    Log.i("Tanck", "访问失败" + responseCode);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return list;
        }

    }
}
