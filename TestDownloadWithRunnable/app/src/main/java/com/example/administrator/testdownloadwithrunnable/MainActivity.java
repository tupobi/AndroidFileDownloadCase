package com.example.administrator.testdownloadwithrunnable;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String urlStr = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
    String path = "file11111111";
    String fileName = "eclipse11111111";
    OutputStream outputStream = null;

    private Handler handler;
    private TextView tvPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnStartDownload).setOnClickListener(new DownloadOnClickListener());
        tvPrompt = (TextView) findViewById(R.id.tvPrompt);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads().detectDiskWrites().detectNetwork()
//                .penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//                .penaltyLog().penaltyDeath().build());
        //让主线程支持下载操作
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 01:
                        tvPrompt.setText("文件已存在！");
                        break;
                    case 02:
                        tvPrompt.setText("下载失败！");
                        break;
                    case 03:
                        tvPrompt.setText("下载成功！");
                        break;
                    case 05:
                        tvPrompt.setText("正在下载！");
                        break;
                }
            }
        };
    }

    class DownloadOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this, "开始下载", Toast.LENGTH_SHORT).show();


//                URL url = new URL(urlStr);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                InputStream inputStream = connection.getInputStream();
//                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
//                String line = null;
//                StringBuffer sb = new StringBuffer();
//                while ((line=in.readLine())!=null){
//                    sb.append(line);
//                }//这是在下载文本文件！！！！！！读取文本
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    try {
                        URL url = new URL(urlStr);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        String SDCard = Environment.getExternalStorageDirectory() + "";
                        String pathName = SDCard + "/" + path + "/" + fileName;
                        File file = new File(pathName);
                        InputStream inputStream = connection.getInputStream();
                        if (file.exists()) {
//                            Toast.makeText(MainActivity.this, "已存在", Toast.LENGTH_SHORT).show();
                            message.what = 01;
                            handler.sendMessage(message);
                            return;
                        }else {
                            message.what = 05;
                            handler.sendMessage(message);
                            String dir = SDCard + "/" + path;
                            new File(dir).mkdir();//新建文件夹
                            file.createNewFile();//新建文件
                            outputStream = new FileOutputStream(file);
                            byte[] buffer = new byte[4 * 1024];
                            while (inputStream.read(buffer) != -1) {
                                outputStream.write(buffer);
                            }
                            outputStream.flush();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
//                        Toast.makeText(MainActivity.this, "下载失败！", Toast.LENGTH_SHORT).show();
                        Message message1 = new Message();
                        message1.what = 02;
                        handler.sendMessage(message1);
                    } finally {
                        try {
                            if (outputStream!=null) {
                                outputStream.close();
//                            Toast.makeText(MainActivity.this, "下载成功！", Toast.LENGTH_SHORT).show();
                                Message message3 = new Message();
                                message3.what = 03;
                                handler.sendMessage(message3);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
//                            Toast.makeText(MainActivity.this, "关闭失败！", Toast.LENGTH_SHORT).show();
                            Message message4 = new Message();
                            message4.what = 04;
                            handler.sendMessage(message4);
                        }
                    }
                }
            }).start();

            ;

        }
    }
}

