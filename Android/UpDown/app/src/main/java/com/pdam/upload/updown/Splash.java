package com.pdam.upload.updown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.view.Window;
import android.view.MotionEvent;

/**
 * Created by muhsyafi on 12/18/15.
 */
public class Splash extends Activity{
    private Thread mSplashThread;
    Alat alat;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        WebView wv = (WebView)findViewById(R.id.wv);
        wv.loadUrl("file:///android_asset/loader.html");
        final Splash sPlashScreen = this;
        alat = new Alat(this);
        alat.initDB(this);
        alat.insertDatabase("update tanggal set tg='" + alat.tanggal() + "';");
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                    }
                } catch (InterruptedException ex) {
                }
                Intent intent = new Intent();
                intent.setClass(sPlashScreen, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        mSplashThread.start();
    }
}
