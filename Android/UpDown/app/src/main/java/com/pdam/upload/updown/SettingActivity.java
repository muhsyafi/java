package com.pdam.upload.updown;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                    alat.insertDatabase("update host set stat='0' where server='lokal'");
                    alat.insertDatabase("update host set stat='1',host='"+getString(R.string.publik)+"' where server='publik'");
                }else if (rb.getText().equals("Lokal")){
                    alat.insertDatabase("update host set stat='0' where server='publik'");
                    alat.insertDatabase("update host set stat='1', host='"+getString(R.string.lokal)+"' where server='lokal'");
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


//    public class Compress {
//        public ProgressDialog prg;
//        private static final int BUFFER = 2048;
//
//     private String[] _files;
//     private String _zipFile;
//
//     public Compress(String[] files, String zipFile) {
//         _files = files;
//         _zipFile = zipFile;
//     }
//
//     public void zip() {
//         try  {
//             //Progress
//             prg.setMessage("Proses mengkompres dan upload file, tunggu sampai selesai...");
//             //prg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//             prg.setIndeterminate(true);
//             final File[] files = alat.getFoto();
//             prg.setCanceledOnTouchOutside(false);
//             prg.setMax(files.length);
//             final int totalProgress = files.length;
//             prg.show();
//
//             //Zip file
//             String[] stringFile = new String[files.length];
//             int jumlahFile = 0;
//             while (jumlahFile<totalProgress){
//                 stringFile[jumlahFile]=files[jumlahFile].toString();
//                 jumlahFile+=1;
//             }
//             //Compress cp = new Compress(stringFile,mediaStorageDir.toString()+"/Download/"+alat.getSettingCode(MainActivity.this)+"_"+alat.getTanggal()+".zip");
//             //cp.zip();
//             //End zipe
//
//             final Thread t = new Thread() {
//                 @Override
//                 public void run() {
//
//                     try {
//                         BufferedInputStream origin = null;
//                         FileOutputStream dest = new FileOutputStream(_zipFile);
//
//                         ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
//
//                         byte data[] = new byte[BUFFER];
//
//                         int jumpTime = 0;
//                         while (jumpTime < totalProgress) {
//                             FileInputStream fi = new FileInputStream(_files[jumpTime]);
//                             origin = new BufferedInputStream(fi, BUFFER);
//                             ZipEntry entry = new ZipEntry(_files[jumpTime].substring(_files[jumpTime].lastIndexOf("/") + 1));
//                             out.putNextEntry(entry);
//                             int count;
//                             while ((count = origin.read(data, 0, BUFFER)) != -1) {
//                                 out.write(data, 0, count);
//                             }
//                             origin.close();
//                             prg.setProgress(jumpTime);
//                             //uploadFile(files[jumpTime]);
//                             jumpTime+=1;
//                             //if (jumpTime == totalProgress-1) alat.uploadDSML(fileDSML,MainActivity.this, alat.getIP(MainActivity.this));
//                             if (jumpTime == totalProgress) {
//                                 out.close();
//                                 Upload up = new Upload();
//                                 File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
//                                 File fl = new File(mediaStorageDir.toString()+"/Download/"+alat.getSettingCode(SettingActivity.this)+"_"+alat.getTanggal()+".zip");
//                                 //alat.uploadDSML(fileDSML,MainActivity.this, alat.getIP(MainActivity.this));
//                                 up.uploadFile(fl,getString(R.string.pictzip_user),getString(R.string.pictzip_pass));
//
//                             }
//
//                         }
//
//                     }catch (Exception e1) {
//                         e1.printStackTrace();
//                     }
//                 }
//             };
//             if (totalProgress>0){
//                 if (alat.cekKoneksi(SettingActivity.this,alat.getIP(SettingActivity.this))){
//                     t.start();
//
//                 }else{
//                     prg.dismiss();
//                     alat.pesan("Tidak ada koneksi, silahkan periksa koneksi ke "+ alat.getIP(SettingActivity.this)+"",SettingActivity.this);
//                 }
//             }else{
//                 prg.dismiss();
//                 alat.pesan("Tidak file foto untuk hari ini", SettingActivity.this);
//
//                }
//                //End progress
//////
//////
//            //    for(int i=0; i < _files.length; i++) {
//            //        //Log.v("Compress", "Adding: " + _files[i]);
//            //        FileInputStream fi = new FileInputStream(_files[i]);
//            //        origin = new BufferedInputStream(fi, BUFFER);
//            //        ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
//            //        out.putNextEntry(entry);
//            //        int count;
//            //        while ((count = origin.read(data, 0, BUFFER)) != -1) {
//            //            out.write(data, 0, count);
//            //        }
//            //        origin.close();
//            //    }
//////
//            //    out.close();
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
////
//        }
//        public void uploadFile(final File filename, final String username, final String password){
//            FTPClient client = new FTPClient();
//            try {
//                client.connect(alat.getIP(SettingActivity.this), 21);
//                client.login(username, password);
//                client.setType(FTPClient.TYPE_BINARY);
//                client.changeDirectory("/");
//                client.upload(filename,
//                        new FTPDataTransferListener() {
//
//                            public void transferred(int arg0) {
//                                Log.i("Jumlah", " transferred ..."+arg0 );
//                            }
//
//                            public void started() {
//                                // TODO Auto-generated method stub
//                                //Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
//                            }
//
//                            public void failed() {
//
//                                prg.dismiss();
//                                //alat.pesan("Gagal mengupload file, silahkan ulangi kembali", MainActivity.this);
//                                uploadFile(filename,username,password);
//                            }
//
//                            public void completed() {
//                                //Toast.makeText(getBaseContext(), " Berhasil ...", Toast.LENGTH_LONG).show();
//                                Log.v("log_tag", "Transfer completed");
//                                prg.dismiss();
//                            }
//
//                            public void aborted() {
//                                //Toast.makeText(getBaseContext()," Transfer gagal, coba lagi...", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//            } catch (Exception e){
//                prg.dismiss();
//                //e.printStackTrace();
//                try {
//                    client.disconnect(true);
//                }catch (Exception e2){
//                    //e2.printStackTrace();
//                }
//            }
//        }
//
//    }

    //Upload task

    //End upload task

}
