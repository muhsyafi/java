package com.pdam.upload.updown;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by muhsyafi on 12/14/15.
 */
public class Alat {
    public File settingFile;
    SQLiteDatabase DB;
    static String ip;
    static TextView teksPesan;
    public File[] files;
    public String[] fileString;
    int serverResponseCode = 0;
    ProgressDialog prg;
    Alat(Context ct){

        DB = ct.openOrCreateDatabase("upDB", ct.MODE_PRIVATE, null);
    }

    public void pesan(String psn, Context ct){

        //Toast toast = Toast.makeText(ct.getApplicationContext(),psn,Toast.LENGTH_SHORT);
        //toast.show();
        new AlertDialog.Builder(ct)
                .setTitle("Error")
                .setMessage(psn)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(R.drawable.warning)
                .show();
    }
    public void initDB(Context ct){
        DB.execSQL("drop  table if exists tanggal;");
        DB.execSQL("create table if not exists code(code varchar(32) unique primary key);");
        DB.execSQL("create table if not exists tanggal(tg varchar(32) primary key);");
        DB.execSQL("create table if not exists host(stat int(8),server varchar(16), host varchar(32) unique primary key)");
        DB.execSQL("insert or ignore into code values('')");
        DB.execSQL("insert or ignore into tanggal values('')");
        DB.execSQL("insert or ignore into host values('0','lokal','" + ct.getString(R.string.lokal) +"')");
        DB.execSQL("insert or ignore into host values('1','publik','"+ct.getString(R.string.publik)+"')");
    }

    //Host
    public String getHost(Context ct){
        String server;
        Cursor hasil = DB.rawQuery("select * from host where stat=1",null);
        hasil.moveToFirst();
        server = hasil.getString(1);
        hasil.close();
        return server;
    }

    public String getIP(Context ct){
        String server;
        Cursor hasil = DB.rawQuery("select * from host where stat=1",null);
        hasil.moveToFirst();
        server = hasil.getString(2);
        hasil.close();
        return server;
    }
    //--End Host

    public String getSettingCode(Context ct){
        String code;
        Cursor hasil = DB.rawQuery("select * from code limit 1",null);
        hasil.moveToFirst();
        code = hasil.getString(0);
        hasil.close();
        return code;
    }

    public String getTanggal(){
        String tanggal;
        Cursor hasil = DB.rawQuery("select * from tanggal limit 1",null);
        hasil.moveToFirst();
        tanggal = hasil.getString(0);
        hasil.close();
        return tanggal;
    }
    public void insertDatabase(String sql){
        try {
            DB.execSQL(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    static public boolean cekKoneksi(Context context, String host) {
        ip = "http://"+host+"";
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL(ip);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(1 * 1000);          // 10 s.
                urlc.connect();
                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public void uploadDSML(String sourceFileUri, Context ct, String host){ //Upload DSML
        String tg = getTanggal();
        //String upLoadServerUri = "http://"+host+"/meter/core/upload-data.php?type=csv&tg="+tg+"&name=";
        //boolean stat = false;
        //String fileName = sourceFileUri;
        //HttpURLConnection conn = null;
        //DataOutputStream dos = null;
        //String lineEnd = "\r\n";
        //String twoHyphens = "--";
        //String boundary = "*****";
        //int bytesRead, bytesAvailable, bufferSize;
        //byte[] buffer;
        //int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);


      if (!sourceFile.isFile()) {
            pesan("FIle DSML tidak ada",ct);
      }
      else
      {
          try {
              String uploadId = UUID.randomUUID().toString();
              //Creating a multi part request
              new MultipartUploadRequest(ct, uploadId,"http://"+host+"/api/pm@csv")
                      .addFileToUpload(sourceFileUri, "file") //Adding file
                      .addParameter("tanggal", tg) //Adding text parameter to the request
                      .addParameter("nama",getSettingCode(ct))
                      .setNotificationConfig(new UploadNotificationConfig())
                      .setMaxRetries(2)
                      .startUpload(); //Starting the upload

          } catch (Exception exc) {
              Toast.makeText(ct, exc.getMessage(), Toast.LENGTH_LONG).show();
          }
          //try {
//
          //    // open a URL connection to the Servlet
          //    FileInputStream fileInputStream = new FileInputStream(sourceFile);
          //    URL url = new URL(upLoadServerUri+getSettingCode(ct.getApplicationContext()));
//
          //    // Open a HTTP  connection to  the URL
          //    conn = (HttpURLConnection) url.openConnection();
          //    conn.setDoInput(true); // Allow Inputs
          //    conn.setDoOutput(true); // Allow Outputs
          //    conn.setUseCaches(false); // Don't use a Cached Copy
          //    conn.setRequestMethod("POST");
          //    conn.setRequestProperty("Connection", "Keep-Alive");
          //    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
          //    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
          //    conn.setRequestProperty("file", fileName);
//
          //    dos = new DataOutputStream(conn.getOutputStream());
//
          //    dos.writeBytes(twoHyphens + boundary + lineEnd);
          //    dos.writeBytes("Content-Disposition: form-data; name='file';filename="+fileName + "" + lineEnd);
//
          //            dos.writeBytes(lineEnd);
//
          //    // create a buffer of  maximum size
          //    bytesAvailable = fileInputStream.available();
//
          //    bufferSize = Math.min(bytesAvailable, maxBufferSize);
          //    buffer = new byte[bufferSize];
//
          //    // read file and write it into form...
          //    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
          //    while (bytesRead > 0) {
//
          //        dos.write(buffer, 0, bufferSize);
          //        bytesAvailable = fileInputStream.available();
          //        bufferSize = Math.min(bytesAvailable, maxBufferSize);
          //        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
          //    }
//
          //    // send multipart form data necesssary after file data...
          //    dos.writeBytes(lineEnd);
          //    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
          //    // Responses from the server (code and message)
          //    serverResponseCode = conn.getResponseCode();
          //    String serverResponseMessage = conn.getResponseMessage();
          //    if(serverResponseCode == 200){
//
          //    }
//
          //    //close the streams //
          //    fileInputStream.close();
          //    dos.flush();
          //    dos.close();
//
          //} catch (MalformedURLException ex) {
//
          //      pesan("Upload file DSML error"+ex.getMessage()+", hubungi IT", ct);
          //  } catch(Exception e) {
          //    pesan(e.getMessage()+", hubungi IT", ct);
          //}
        } // End else block
    } // End upload DSML

    //Getfiles
    public File[] getFoto(){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        File f = new File(mediaStorageDir.getPath()+"/PDAM/");
        if (!f.exists()){
            f.mkdir();
        }else {
            files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.substring(0, 8).equals(getTanggal())) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        return files;
    }
    //End getfiles
    public File[] getAllFoto(){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        File f = new File(mediaStorageDir.getPath()+"/PDAM/");
        if (!f.exists()){
            f.mkdir();
        }else {
            files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return true;
                }
            });
        }
        return files;
    }
    //Get all file foto

    //End getfiles
    public File[] getSatuFoto(final String nosamw){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        File f = new File(mediaStorageDir.getPath()+"/PDAM/");
        if (!f.exists()){
            f.mkdir();
        }else {
            files = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.substring(9, 19).equals(nosamw)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        return files;
    }
    //Get all file foto

    //End get all file foto

    //Getfile String

    //End getfile string

    //Get date
    public String tanggal(){
        final Date hariIni = new Date();
        final SimpleDateFormat frm = new SimpleDateFormat("yyyyMMdd");
        return frm.format(hariIni).toString();
    }
    //End get date

    public void uploadFile(String sourceFileUri, Context ct, String host){ //Upload DSML
        try {
            String tg = getTanggal();
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
                    new MultipartUploadRequest(ct, uploadId,"http://"+host+"/api/pm")
                            .addFileToUpload(sourceFileUri, "file") //Adding file
                            .addParameter("tanggal", tg) //Adding text parameter to the request
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(ct, exc.getMessage(), Toast.LENGTH_LONG).show();
        }


    //    String upLoadServerUri = "http://"+host+"/meter/core/upload-data.php?type=csv&tg="+tg+"&name=";
    //    boolean stat = false;
    //    String fileName = sourceFileUri;
    //    HttpURLConnection conn = null;
    //    DataOutputStream dos = null;
    //    String lineEnd = "\r\n";
    //    String twoHyphens = "--";
    //    String boundary = "*****";
    //    int bytesRead, bytesAvailable, bufferSize;
    //    byte[] buffer;
    //    int maxBufferSize = 1 * 1024 * 1024 * 1024;
    //    File sourceFile = new File(sourceFileUri);
//
//
    //    if (!sourceFile.isFile()) {
    //        pesan("File foto tidak ada",ct);
    //    }
    //    else
    //    {
    //        try {
//
    //            // open a URL connection to the Servlet
    //            FileInputStream fileInputStream = new FileInputStream(sourceFile);
    //            URL url = new URL(upLoadServerUri+getSettingCode(ct.getApplicationContext()));
//
    //            // Open a HTTP  connection to  the URL
    //            conn = (HttpURLConnection) url.openConnection();
    //            conn.setDoInput(true); // Allow Inputs
    //            conn.setDoOutput(true); // Allow Outputs
    //            conn.setUseCaches(false); // Don't use a Cached Copy
    //            conn.setRequestMethod("POST");
    //            conn.setRequestProperty("Connection", "Keep-Alive");
    //            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
    //            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
    //            conn.setRequestProperty("file", fileName);
//
    //            dos = new DataOutputStream(conn.getOutputStream());
//
    //            dos.writeBytes(twoHyphens + boundary + lineEnd);
    //            dos.writeBytes("Content-Disposition: form-data; name='file';filename="+fileName + "" + lineEnd);
//
    //            dos.writeBytes(lineEnd);
//
    //            // create a buffer of  maximum size
    //            bytesAvailable = fileInputStream.available();
//
    //            bufferSize = Math.min(bytesAvailable, maxBufferSize);
    //            buffer = new byte[bufferSize];
//
    //            // read file and write it into form...
    //            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
    //            while (bytesRead > 0) {
//
    //                dos.write(buffer, 0, bufferSize);
    //                bytesAvailable = fileInputStream.available();
    //                bufferSize = Math.min(bytesAvailable, maxBufferSize);
    //                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
    //            }
//
    //            // send multipart form data necesssary after file data...
    //            dos.writeBytes(lineEnd);
    //            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
    //            // Responses from the server (code and message)
    //            serverResponseCode = conn.getResponseCode();
    //            String serverResponseMessage = conn.getResponseMessage();
    //            if(serverResponseCode == 200){
//
    //            }
//
    //            //close the streams //
    //            fileInputStream.close();
    //            dos.flush();
    //            dos.close();
//
    //        } catch (MalformedURLException ex) {
//
    //            pesan("Upload file foto error"+ex.getMessage()+", hubungi IT", ct);
    //        } catch(Exception e) {
    //            pesan(e.getMessage()+", hubungi IT", ct);
    //        }
    //    } // End else block
    } // End upload Foto manual

}
