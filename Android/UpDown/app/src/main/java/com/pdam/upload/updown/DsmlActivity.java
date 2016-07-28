package com.pdam.upload.updown;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class DsmlActivity extends Activity {
    String url = "http://36.78.220.224/meter/core/get.php?act=get-dsml";
    String tag_json_arry = "json_array_req";
    ListView lstDsml;
    ProgressDialog pDialog;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsml);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setTitle("Daftar DSML");
        pDialog  = new ProgressDialog(DsmlActivity.this);
        pDialog.setMessage("Loading DSML");
        pDialog.show();
        lstDsml = (ListView)findViewById(R.id.listDsml);
        request();
    }

    public void request(){
        RequestQueue queue = Volley.newRequestQueue(DsmlActivity.this);
        String url ="http://36.78.220.224/meter/core/get.php?act=get-dsml";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        fetch(response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //txt.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void fetch(String str){
        String regex = "\\[|\\]|\"";
        str = str.replaceAll(regex, "");
        ArrayList lst = new ArrayList(Arrays.asList(str.split(",")));
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_listview,lst);
        lstDsml.setAdapter(adapter);
        lstDsml.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                pDialog = new ProgressDialog(DsmlActivity.this);
                pDialog.setMessage("Download DSML, tunggu sampai selesai...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setIndeterminate(true);
                pDialog.setMax(1);
                pDialog.show();
                final Thread t = new Thread() {
                    @Override
                    public void run() {
                        downloadStart(parent.getItemAtPosition(position).toString());
                        pDialog.dismiss();
                    }
                };
                t.start();
            }
        });
    }
    //Perfect code to download single file from FTP server
    private void downloadStart(String nama) {
        final String host = "36.78.220.224";
        final String user = "dsml";
        final String pass = "satu2345";
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
            File fileDownload = new File(mediaStorageDir.getPath()+"/importpdam.csv");
            fileDownload.createNewFile();
            ftp.download("/"+nama, fileDownload,
                    new FTPDataTransferListener() {

                public void transferred(int arg0) {
                    //Log.v("log_tag", "This is for tranfer");
                    //Toast.makeText(getBaseContext(), " transferred ..."+arg0 , Toast.LENGTH_SHORT).show();
                }

                public void started() {
                    // TODO Auto-generated method stub
                    //Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                    //Log.v("log_tag", "This is for started");
                }

                public void failed() {
                    //pDialog.dismiss();
                    //Toast.makeText(getBaseContext(), "  Gagal, coba lagi ...", Toast.LENGTH_LONG).show();
                    ////System.out.println(" failed ..." );
                }

                public void completed() {
                    //pDialog.dismiss();
                    //Toast.makeText(getBaseContext(), " Berhasil ...", Toast.LENGTH_LONG).show();
                    ////Log.v("log_tag", "This is for completed");

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

    }
}