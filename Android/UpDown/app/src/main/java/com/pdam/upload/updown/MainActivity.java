package com.pdam.upload.updown;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;

import static com.pdam.upload.updown.AppController.TAG;

public class MainActivity extends Activity {
    public Button uploadFoto;
    public Button downloadDsml;
    public Button setting;
    public Button fotoManual;
    public TextView jumlah;
    ProgressDialog prg;
    public String kode;
    public String tanggal;
    Alat alat;
    public boolean konek;
    public String fileDSML;
    TextView pesanTeks;
    public boolean pilihJenisUploadFoto;
    public String nosamw;

    //Fetch JSON Password
    private String TAG = MainActivity.class.getSimpleName();

    //End fetch JSON Password

    public Boolean passApp;

    //Alert Dialog pilih
    public AlertDialog.Builder builderSingle;
    //End alert dialog pilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prg = new ProgressDialog(MainActivity.this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        File f = new File(mediaStorageDir.getPath()+"/PDAM/");
        pesanTeks = (TextView)findViewById(R.id.textPesan);
        //pesanTeks.setText("Gagal upload, ulangi kembali");
        pesanTeks.setVisibility(View.INVISIBLE);
        builderSingle  = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.warning);
        builderSingle.setTitle("Pilih salah satu");
        pilihJenisUploadFoto = true;



        if (!f.exists()) {
            f.mkdir();
        }
        alat = new Alat(this);
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
                if (alat.cekKoneksi(MainActivity.this,alat.getIP(MainActivity.this))){
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

        //Upload manual
        fotoManual = (Button)findViewById(R.id.btnManual);
        fotoManual.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                if (!alat.cekKoneksi(MainActivity.this,alat.getIP(MainActivity.this))){
                    alat.pesan("Tidak ada koneksi ke "+alat.getIP(MainActivity.this)+", periksa koneksi", MainActivity.this);
                }else{
                    promptPassword();
                }
            }
        });
        //End upload manual

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
            final int totalProgress = files.length;

            final Thread t = new Thread() {
                @Override
                public void run() {
                    if (pilihJenisUploadFoto){
                        int jumpTime = 0;
                        prg.setMax(files.length);
                        while (jumpTime < totalProgress) {
                            prg.setProgress(jumpTime);
                            uploadFile(files[jumpTime],getString(R.string.user),getString(R.string.pass));
                            jumpTime+=1;
                            Log.i("Jum",String.valueOf(jumpTime));
                            if (jumpTime == totalProgress) alat.uploadDSML(fileDSML,MainActivity.this, alat.getIP(MainActivity.this));
                            if (jumpTime == totalProgress) prg.dismiss();
                        }
                    }else{
                        prg.setIndeterminate(true);
                        try {
                            synchronized (this){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                            prg.setMessage("Upload foto, ditunggu");
                                            final File[] fileFotoSatu = alat.getSatuFoto(nosamw);
                                            if (fileFotoSatu.length>0){
                                                prg.setMax(fileFotoSatu.length);
                                                uploadFile(fileFotoSatu[0],getString(R.string.user),getString(R.string.pass));
                                                prg.dismiss();
                                            }else{
                                                alat.pesan("Foto tersebut tidak ada",MainActivity.this);
                                                prg.dismiss();
                                            }
                                    }
                                });
                            }
                        }catch (Exception e){
                            throw e;
                        }
                    }
                }
            };
            prg.show();

            //Init alert pilih
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Upload Semua");
            arrayAdapter.add("Upload foto tertentu");
            builderSingle.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    prg.dismiss();
                }
            });
            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = arrayAdapter.getItem(which);
                    if (which==0){
                        dialog.dismiss();
                        t.start();
                    }else if (which==1){
                        LayoutInflater li = LayoutInflater.from(MainActivity.this);
                        View promptsView = li.inflate(R.layout.dialog_nosamw, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                MainActivity.this);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText userInput = (EditText) promptsView
                                .findViewById(R.id.dialog_nosamw);

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // get user input and set it to result
                                                // edit text
                                                if (!userInput.getText().toString().matches("")){
                                                    //getPassword(userInput.getText().toString());
                                                    if (userInput.getText().length()!=10){
                                                        alat.pesan("No Sambung kurang harus 10 digit",MainActivity.this);
                                                        prg.dismiss();
                                                    }else{
                                                        nosamw = userInput.getText().toString();
                                                        pilihJenisUploadFoto=false;
                                                        dialog.dismiss();
                                                        t.start();
                                                    }

                                                }else{
                                                    alat.pesan("Isisan Tidak boleh kosong",MainActivity.this);
                                                    prg.dismiss();
                                                }
                                            }
                                        })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                                prg.dismiss();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }
                }
            });

            //End init alert pilih

            if (totalProgress>0){
                if (alat.cekKoneksi(MainActivity.this,alat.getIP(MainActivity.this))){
                        builderSingle.show();
                }else{
                    prg.dismiss();
                    alat.pesan("Tidak ada koneksi, silahkan periksa koneksi ke "+ alat.getIP(MainActivity.this)+"",MainActivity.this);
                }
            }else{
                prg.dismiss();
                alat.pesan("Tidak file foto untuk hari ini", MainActivity.this);
            }
        }

        public void uploadFile(final File filename, final String username, final String password){
            FTPClient client = new FTPClient();
            try {
                client.connect(host, 21);
                client.login(username, password);
                client.setType(FTPClient.TYPE_BINARY);
                client.changeDirectory("/");
                client.upload(filename,
                        new FTPDataTransferListener() {

                            public void transferred(int arg0) {
                                Log.i("Jumlah", " transferred ..."+arg0 );
                            }

                            public void started() {
                                // TODO Auto-generated method stub
                                //Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                            }

                            public void failed() {
                                //pesan("Gagal mengupload file, silahkan ulangi kembali");
                                //alat.pesan("Pesan",ct);
                                //Toast.makeText(getBaseContext(), " Gagal ...", Toast.LENGTH_LONG).show();
                                //pesanTeks.setVisibility(View.VISIBLE);
                                //prg.dismiss();
                                //alat.pesan("Gagal mengupload file, silahkan ulangi kembali", MainActivity.this);
                                uploadFile(filename,username,password);
                            }

                            public void completed() {
                                //Toast.makeText(getBaseContext(), " Berhasil ...", Toast.LENGTH_LONG).show();
                                Log.v("log_tag", "Transfer completed");
                                //prg.dismiss();
                            }

                            public void aborted() {
                                //Toast.makeText(getBaseContext()," Transfer gagal, coba lagi...", Toast.LENGTH_SHORT).show();

                            }
                        });
            } catch (Exception e){
                //pesanTeks.setVisibility(View.VISIBLE);
                //prg.dismiss();
                //e.printStackTrace();
                try {
                    client.disconnect(true);
                }catch (Exception e2){
                    //pesanTeks.setVisibility(View.VISIBLE);
                    //e2.printStackTrace();
                }
            }
        }
    }


    //Show dialog password
    public void promptPassword(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_signin, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.prompPassword);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                if (!userInput.getText().toString().matches("")){
                                    getPassword(userInput.getText().toString());

                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    //End show dialog password

    public void getPassword(final String password){
        class getPassString extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                //pDialog = new ProgressDialog(MainActivity.this);
                //pDialog.setMessage("Please wait...");
                //pDialog.setCancelable(false);
                //pDialog.show();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();

                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall("http://"+alat.getIP(MainActivity.this)+"/api/pm@key/"+password);

                Log.e(TAG, "Response from url: " + jsonStr);

                if (jsonStr != null) {
                    try {

                        // Getting JSON Array node
                        JSONArray jsonArr = new JSONArray(jsonStr);

                        // looping through All Contacts
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject arr = jsonArr.getJSONObject(i);

                            String teks     = arr.getString("teks");
                            Boolean error   = arr.getBoolean("error");
                            if (!error){
                                passApp = true;
                            }else{
                                passApp = false;
                            }
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Dismiss the progress dialog
                //if (pDialog.isShowing())
                //    pDialog.dismiss();
                ///**
                // * Updating parsed JSON data into ListView
                // * */
                if (passApp){
                    Intent intent  = new Intent(MainActivity.this, ManualActivity.class);
                    MainActivity.this.startActivity(intent);
                }
            }

        }
        new getPassString().execute();
    }

}


