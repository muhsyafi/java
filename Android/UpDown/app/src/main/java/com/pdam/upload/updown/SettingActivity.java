package com.pdam.upload.updown;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;


public class SettingActivity extends AppCompatActivity {

    EditText kode;
    public Button btnUpdate;
    public Button btnTanggal;
    public RadioButton radioPublik;
    public RadioButton radioLokal;
    public RadioGroup radioHost;

    public String server;
    Alat alat;
    private int year, month, day;
    private Calendar cal;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        alat = new Alat(this);
        toolbar.setTitle("Setting Aplikasi");
        kode = (EditText)findViewById(R.id.editText);
        kode.setText(alat.getSettingCode(this));
        setSupportActionBar(toolbar);
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnTanggal = (Button)findViewById(R.id.btnTanggal);
        radioHost = (RadioGroup)findViewById(R.id.radio_host);
        radioPublik = (RadioButton)findViewById(R.id.radio_publik);
        radioLokal = (RadioButton)findViewById(R.id.radio_lokal);

        server = alat.getHost(SettingActivity.this);
        if (server.equals("publik")){
            radioPublik.setChecked(true);
        }else if(server.equals("lokal")){
            radioLokal.setChecked(true);
        }

        radioHost.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton)findViewById(checkedId);
                if (rb.getText().equals("Publik")){
                    alat.insertDatabase("update host set stat='0'");
                    alat.insertDatabase("update host set stat='1' where server='publik'");
                }else if (rb.getText().equals("Lokal")){
                    alat.insertDatabase("update host set stat='0'");
                    alat.insertDatabase("update host set stat='1' where server='lokal'");
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_save, getApplicationContext().getTheme()));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_save));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kode = (EditText)findViewById(R.id.editText);
                String kodePM = String.valueOf(kode.getText());
                if (kodePM.isEmpty()){
                    alat.pesan("ID Pembaca Meter tidak boleh kosong",SettingActivity.this);
                }else{
                    alat.insertDatabase("delete from code;");
                    alat.insertDatabase("insert into code values('"+kodePM+"');");
                    Snackbar.make(view, "Setting telah disimpan", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(SettingActivity.this);
                pDialog.setMessage("Download Aplikasi, tunggu sampai selesai...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setIndeterminate(true);
                pDialog.setMax(1);
                pDialog.show();
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                            downloadStart("app-debug.apk");
                            pDialog.dismiss();
                    }
                };
                t.start();

            }
        });
        btnTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal(v);
            }
        });
    }


    //Back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        String kode = alat.getSettingCode(SettingActivity.this);
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if(!kode.equals("")) super.onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("deprecation")
    public void setTanggal(View v){
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id==999){
            return new DatePickerDialog(this,myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3){
            showDate(arg1, arg2, arg3);
        }
    };

    private void showDate(int year, int month, int day){
        String th=String.valueOf(year);
        String bl=String.valueOf(month+1);
        String tg=String.valueOf(day);
        if (bl.length()<2) bl='0'+bl;
        if (tg.length()<2) tg='0'+tg;
        String tanggal = th+bl+tg;
        alat.insertDatabase("update tanggal set tg='"+tanggal+"';");

    }

    @Override
    public void onBackPressed() {

    }
    //ENd back button

    //Perfect code to download single file from FTP server
    private void downloadStart(String nama) {
        final String host = alat.getIP(SettingActivity.this);
        final String user = getString(R.string.app_user);
        final String pass = getString(R.string.app_pass);
        FTPClient ftp = new FTPClient();
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            ftp.connect(host,21);
            //System.out.println(ftp.connect(host)[0]);
            ftp.login(user, pass);
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
            File fileDownload = new File(mediaStorageDir.getPath()+"/"+nama);
            fileDownload.createNewFile();
            ftp.download("/"+nama, fileDownload,
                    new FTPDataTransferListener() {

                        public void transferred(int arg0) {
                            //Log.v("log_tag", "This is for tranfer");
                            //Log.wtf("Jumlah", " transferred ..."+arg0 );
                        }

                        public void started() {
                            // TODO Auto-generated method stub
                            //Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                            //Log.v("log_tag", "This is for started");
                        }

                        public void failed() {
                            //Toast.makeText(getBaseContext(), "  Gagal, coba lagi ...", Toast.LENGTH_LONG).show();
                            //System.out.println(" failed ..." );
                        }

                        public void completed() {
                            //Toast.makeText(getBaseContext(), " Berhasil ...", Toast.LENGTH_LONG).show();
                            //Log.v("log_tag", "This is for completed");

                        }

                        public void aborted() {
                            //Toast.makeText(getBaseContext()," Transfer gaga, coba lagi...", Toast.LENGTH_SHORT).show();
                            //Log.v("log_tag", "This is for aborted");

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ftp.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    } //End download

}
