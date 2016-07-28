package com.pdambaru;




import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
/* kode untuk menampilkan splash screen salama 3 detik */
	private final int SPLASH_DISPLAY_LENGHT = 1000;
//	private final long BATAS_APP = 1436486400;
	 SharedPreferences sharedpreferences;
	 String status_login;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_splash);
	//	final long waktuNow = System.currentTimeMillis() / 1000L;
		
		
		/*
		 * New Handler to start the Menu-Activity and close this Splash-Screen
		 * after some seconds.
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent mainIntent=null;
			
					 mainIntent = new Intent(SplashActivity.this,
								PelangganDataActivity.class);
						
				
				
				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);
	}
}