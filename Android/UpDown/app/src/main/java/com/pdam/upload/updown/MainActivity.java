package com.pdam.upload.updown;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;

public class MainActivity extends Activity {
    public Button uploadFoto;
    public Button downloadDsml;
    public Button setting;
    public TextView jumlah;
    ProgressDialog prg;
    public String kode;
    public String tanggal;
    Alat alat;
    public boolean konek;
    public String fileDSML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        File f = new File(mediaStorageDir.getPath()+"/PDAM/");
        if (!f.exists()) {
            f.mkdir();
        }
        alat = new Alat(this);
        if (!alat.cekKoneksi(this, alat.getIP(MainActivity.this))){
            konek = false;
        }else{
            konek = true;
        }
        kode = alat.getSettingCode(this);
        mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        fileDSML = new File(mediaStorageDir.getPath()+"/exportpdam.csv").toString();
        jumlah = (TextView)findViewById(R.id.txtGambar);
        jumlah.setText(String.valueOf(alat.getFoto().length));

        uploadFoto = (Button)findViewById(R.id.btnUpload);
        uploadFoto.setOnClickListener(new Upload());

        downloadDsml = (Button)findViewById(R.id.btnDownload);
        downloadDsml.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if (konek){
                    Intent it = new Intent(MainActivity.this, DsmlActivity.class);
                    MainActivity.this.startActivity(it);
                }else{
                    alat.pesan("Tidak ada koneksi ke "+alat.getIP(MainActivity.this)+", periksa koneksi", MainActivity.this);
                }
            }
        });

        setting = (Button)findViewById(R.id.btnSetting);
        setting.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //Redirect setting
        if (kode.equals("")){
            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
            MainActivity.this.startActivity(intent);
        }
        //End redirect setting
    } //Create

    //Resume
    @Override
    public void onResume(){
        super.onResume();
        jumlah = (TextView)findViewById(R.id.txtGambar);
        jumlah.setText(String.valueOf(alat.getFoto().length));
        //alat.insertDatabase("update tanggal set tg='" + alat.tanggal() + "';");

    }
    //End resume


    public class Upload implements View.OnClickListener{
        final String host = alat.getIP(MainActivity.this);
        final String user = getString(R.string.user);
        final String pass = getString(R.string.pass);

        @Override
        public void onClick(View v){
            prg = new ProgressDialog(MainActivity.this);
            prg.setMessage("Upload foto & DSML, tunggu sampai selesai...");
            prg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //prg.setIndeterminate(true);
            final File[] files = alat.getFoto();
            prg.setCanceledOnTouchOutside(false);
            prg.setMax(files.length);
            final int totalProgress = files.length;
            prg.show();
            final Thread t = new Thread() {
                @Override
                public void run() {
                    int jumpTime = 0;
                    while (jumpTime < totalProgress) {
                        prg.setProgress(jumpTime);
                        uploadFile(files[jumpTime]);
                        jumpTime+=1;
                        if (jumpTime == totalProgress-1) alat.uploadDSML(fileDSML,MainActivity.this, alat.getIP(MainActivity.this));
                        if (jumpTime == totalProgress) prg.dismiss();
                    }
                }
            };
            if (totalProgress>0){
                if (konek){
                        t.start();
                }else{
                    prg.dismiss();
                    alat.pesan("Tidak ada koneksi, silahkan periksa koneksi ke "+ alat.getIP(MainActivity.this)+"",MainActivity.this);
                }
            }else{
                prg.dismiss();
                alat.pesan("Tidak file foto untuk hari ini", MainActivity.this);

            }
        }

        public void uploadFile(final File filename){
            FTPClient client = new FTPClient();
            try {
                client.connect(host, 21);
                client.login(user, pass);
                client.setType(FTPClient.TYPE_BINARY);
                client.changeDirectory("/");
                client.upload(filename,
                        new FTPDataTransferListener() {

                            public void transferred(int arg0) {
                                //Log.wtf("Jumlah", " transferred ..."+arg0 );
                            }

                            public void started() {
                                // TODO Auto-generated method stub
                                //Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                            }

                            public void failed() {
                                alat.pesan("Gagal mengupload file, silahkan ulangi kembali", MainActivity.this);
                                uploadFile(filename);
                            }

                            public void completed() {
                                //Toast.makeText(getBaseContext(), " Berhasil ...", Toast.LENGTH_LONG).show();
                                Log.v("log_tag", "Transfer completed");
                            }

                            public void aborted() {
                                //Toast.makeText(getBaseContext()," Transfer gagal, coba lagi...", Toast.LENGTH_SHORT).show();

                            }
                        });
            } catch (Exception e){
                e.printStackTrace();
                try {
                    client.disconnect(true);
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            }
        }
    }
}


