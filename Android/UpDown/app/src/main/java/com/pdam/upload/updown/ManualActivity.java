package com.pdam.upload.updown;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class ManualActivity extends AppCompatActivity {

    //Variable
    RadioButton radioManual;
    RadioButton radioUpdate;
    RadioGroup radioGroupManual;
    Button post;
    Button btnSimpan;
    public String pilih;
    private int tahun,bulan,tanggal;
    private Calendar cal;
    private DatePicker datePicker;
    TextView tanggalManual;
    EditText keterangan;
    EditText noSambung;
    String tanggalBaca;
    Alat alat;
    private int PICT_IMAGE_REQUEST = 1;
    private  static  final  int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap;
    private Uri filePath;
    private ImageView imageView;
    public String fileFoto;
    public boolean konek;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        //Init alat
        alat  = new Alat(this);

        //Set variabel
        fileFoto = "";

        //Set hari ini
        cal                             = Calendar.getInstance();
        tahun                           = cal.get(Calendar.YEAR);
        bulan                           = cal.get(Calendar.MONTH);
        tanggal                         = cal.get(Calendar.DAY_OF_MONTH);
        keterangan                        = (EditText)findViewById(R.id.keterangan);
        noSambung                       = (EditText)findViewById(R.id.noSambung);
        post                            = (Button)findViewById(R.id.btnPilihFoto);
        btnSimpan                       = (Button)findViewById(R.id.btnSimpan);
        btnSimpan.setVisibility(View.INVISIBLE);
        final Drawable tidakAktif       = getBaseContext().getResources().getDrawable(R.drawable.file_tidak);
        final Drawable aktif            = getBaseContext().getResources().getDrawable(R.drawable.file);
        final Drawable simpan           = getBaseContext().getResources().getDrawable(R.drawable.simpan);
        imageView                       = (ImageView) findViewById(R.id.imageView);


        //Cek koneksi
        if (!alat.cekKoneksi(this, alat.getIP(this))){
            konek = false;
        }else{
            konek = true;
        }
        //End cek koneksi

        //Radio
        radioGroupManual = (RadioGroup)findViewById(R.id.radio_group_manual);
        radioGroupManual.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)findViewById(i);
                post.setEnabled(true);
                post.setBackground(aktif);
                if (rb.getText().equals("Manual")){
                    pilih = "manual";
                    keterangan.setEnabled(true);
                }else{
                    pilih = "update";
                    keterangan.setEnabled(false);
                }

            }
        });
        //End radio

        //Set placeholder
        keterangan.setHint("Keterangan");
        keterangan.setEnabled(false);
        //Disable tombol post
        post.setEnabled(false);
        post.setBackground(tidakAktif);


        //Ganti tanggal
        tanggalManual           = (TextView) findViewById(R.id.tanggalManual);
        SimpleDateFormat df     = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfM    = new SimpleDateFormat("yyyyMMdd");
        tanggalManual.setText(df.format(cal.getTime()));
        tanggalBaca             = dfM.format(cal.getTime());
        tanggalManual.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setTanggal(view);
            }
        });



        //Get file
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihFile();

            }
        });
        //End get file

        //Simpan
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String stNosamw   = noSambung.getText().toString();
                final String stketerangan = keterangan.getText().toString();
                final String stTanggal  = tanggalBaca;
                if (konek){
                    if (stNosamw.length()==10){
                        if (pilih.equals("update")){
                            if (!stNosamw.isEmpty() && !stTanggal.isEmpty()){
                                if (!fileFoto.equals("")){
                                    pDialog = new ProgressDialog(ManualActivity.this);
                                    pDialog.setMessage("Download Aplikasi, tunggu sampai selesai...");
                                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    pDialog.setCanceledOnTouchOutside(false);
                                    pDialog.setIndeterminate(true);
                                    pDialog.setMax(1);
                                    pDialog.show();
                                    final Thread t = new Thread() {
                                        @Override
                                        public void run() {
                                            uploadMultipart(fileFoto,stNosamw,stTanggal,"-");
                                            pDialog.dismiss();
                                        }
                                    };
                                    t.start();
                                }else{
                                    alat.pesan("Pilih foto dahulu", ManualActivity.this);
                                }
                            }else{
                                alat.pesan("Isian tidak boleh kosong", ManualActivity.this);
                            }
                        }else if (pilih.equals("manual")){
                            if (!stNosamw.isEmpty() && !stTanggal.isEmpty()){
                                if (!fileFoto.equals("")){

                                    pDialog = new ProgressDialog(ManualActivity.this);
                                    pDialog.setMessage("Download Aplikasi, tunggu sampai selesai...");
                                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    pDialog.setCanceledOnTouchOutside(false);
                                    pDialog.setIndeterminate(true);
                                    pDialog.setMax(1);
                                    pDialog.show();
                                    final Thread t = new Thread() {
                                        @Override
                                        public void run() {
                                            uploadMultipart(fileFoto,stNosamw,stTanggal,stketerangan);
                                            pDialog.dismiss();
                                        }
                                    };
                                    t.start();

                                }else{
                                    alat.pesan("Pilih foto dahulu", ManualActivity.this);
                                }
                            }else{
                                alat.pesan("Isian tidak boleh kosong", ManualActivity.this);
                            }
                        }
                    }else{
                        alat.pesan("Nomor sambung harus 10 digit",ManualActivity.this);
                    }
                }else{
                    alat.pesan("Tidak ada koneksi ke "+alat.getIP(ManualActivity.this)+", periksa koneksi", ManualActivity.this);
                }
            }
        });
        //End simpan
    }

    //Init tanggal

    @SuppressWarnings("deprecation")
    public void setTanggal(View v){
        showDialog(999);
    };


    @Override
    protected Dialog onCreateDialog(int id){
        if (id==999){
            return new DatePickerDialog(this, myDateListener,tahun,bulan,tanggal);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            showDate(i,i1,i2);
        }
    };

    private void showDate(int tahun, int bulan, int tanggal){
        String th               = String.valueOf(tahun);
        String bl               = String.valueOf(bulan+1);
        String tg               = String.valueOf(tanggal);
        if (bl.length()<2) bl   = '0'+bl;
        if (tg.length()<2) tg   = '0'+tg;
        String tanggalPenuh     = th+bl+tg;
        tanggalManual.setText(th+"-"+bl+"-"+tg);
        tanggalBaca             = tanggalPenuh;
    };

    //Pilih file
    private void pilihFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Pilih gambar"),PICT_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                fileFoto = getPath(filePath).toString();
                if (!fileFoto.equals("")){
                    post.setVisibility(View.INVISIBLE);
                    btnSimpan.setVisibility(View.VISIBLE);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //Copy file
    private void copyFile(String inputPathFile, String outputPath, String outputFile) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPathFile);
            out = new FileOutputStream(outputPath+"/" + outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag 1", fnfe1.getMessage());
            alat.pesan(fnfe1.getMessage(),this);
        }
        catch (Exception e) {
            Log.e("tag 2", e.getMessage());
            alat.pesan(e.getMessage(),this);
        }

    }
    //End copy file

    //Upload
    public void uploadMultipart(String foto,String nosamw,String tanggalFoto, String keteranganManual) {
        //getting name for the image
        //String name = editText.getText().toString().trim();
        //getting the actual path of the image
        //String path = getPath(filePath);
        String fileFotoPindahPath;
        File mediaStorageDir    = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        mediaStorageDir         = new File(Environment.getExternalStorageDirectory().getPath(), "/");
        fileFotoPindahPath      = new File(mediaStorageDir.getPath()+"/PDAM/").toString();

        File fileGambar         = new File(foto);
        //String  namaFile        = fileGambar.getName();
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            if (!alat.cekKoneksi(this,alat.getIP(this))) {
                alat.pesan("Tidak ada koneksi ke "+alat.getIP(this)+", periksa koneksi", this);
            }else{
                if (pilih.equals("update")){
                    new MultipartUploadRequest(this, uploadId,"http://"+alat.getIP(this)+"/api/pm")
                            .addFileToUpload(foto, "file") //Adding file
                            .addParameter("name", "file") //Adding text parameter to the request
                            .addParameter("jenis","update")
                            .addParameter("nosamw",nosamw)
                            .addParameter("tanggal",tanggalFoto)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload(); //Starting the upload
                            copyFile(foto,fileFotoPindahPath,tanggalFoto+"-"+nosamw+".jpg");
                            ManualActivity.this.finish();
                }else{
                    new MultipartUploadRequest(this, uploadId,"http://"+alat.getIP(this)+"/api/pm")
                            .addFileToUpload(foto, "file") //Adding file
                            .addParameter("name", "file") //Adding text parameter to the request
                            .addParameter("jenis","manual")
                            .addParameter("nosamw",nosamw)
                            .addParameter("tanggal",tanggalFoto)
                            .addParameter("keterangan",keteranganManual)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload(); //Starting the upload
                            copyFile(foto,fileFotoPindahPath,tanggalFoto+"-"+nosamw+".jpg");
                            ManualActivity.this.finish();
                }
            }

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


}
