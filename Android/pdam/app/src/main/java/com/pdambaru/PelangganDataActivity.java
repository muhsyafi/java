package com.pdambaru;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PelangganDataActivity extends Activity {

	int from_Where_I_Am_Coming = 0;
	DataBaseHelper db;
	EditText nama;
	EditText alamat;
	EditText keterangan;
	EditText nosamw;
	TextView txtMetK,txtMetL,txtPakai;

	int no = 1;
	int totalData = 0;
	int sudah = 0;
	int belum = 0;
	int id_To_Update = 0;
	String nopelanggan;

	TextView txtTotal, txtSudah, txtBelum;
	ImageButton btnNext, btnBack;
	Button btnCamera;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pelanggan_pager);
		// get extra

		Intent intent = getIntent();
		String timeStamp = new SimpleDateFormat("MMMM yyyy").format(new Date());
		setTitle(timeStamp);
		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.containsKey("no")) {
				String data = getIntent().getExtras().getString("no", "1");
				no = Integer.parseInt(data);
			}
		}

		db = new DataBaseHelper(this);

		txtTotal = (TextView) findViewById(R.id.txtTotal);
		txtSudah = (TextView) findViewById(R.id.txtSudah);
		txtBelum = (TextView) findViewById(R.id.txtBelum);

		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnCamera=(Button) findViewById(R.id.btnCamera);

		nosamw = (EditText) findViewById(R.id.txtNosamw);
		nama = (EditText) findViewById(R.id.txtNama);
		alamat = (EditText) findViewById(R.id.txtAlamat);
		keterangan = (EditText) findViewById(R.id.txtKeterangan);
		
		txtMetL = (TextView) findViewById(R.id.txtMetL);
		txtMetK = (TextView) findViewById(R.id.txtMetK);
		txtPakai= (TextView) findViewById(R.id.txtPakai);

		nama.setKeyListener(null);
		alamat.setKeyListener(null);
		// keterangan.setKeyListener(null);

		/*
		 * SQLiteDatabase dbl = null;
		 * 
		 * try { dbpelanggan.createDataBase(); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * totalData=dbpelanggan.totalData();
		 * sudah=dbpelanggan.totalDataTerisi();
		 */
		belum = totalData - sudah;
		txtTotal.setText("" + totalData);

		txtSudah.setText("" + sudah);
		txtBelum.setText("" + belum);

		// Button b = (Button) findViewById(R.id.btnCamera);
		// b.setEnabled(false);
		loadDataFromDB(no);

	}

	public void loadDataFromDB(int no) {
		try {
			db.openDataBase();
			totalData = db.totalData();
			sudah = db.totalDataTerisi();
			txtTotal.setText("Total:" + totalData);
			txtSudah.setText("Sudah:" + sudah);
			belum = totalData - sudah;
			txtBelum.setText("Belum:" + belum);

			Cursor rs = db.getData(no);

			if (rs.getCount() > 0) {
				rs.moveToFirst();
				no = rs.getInt(rs.getColumnIndex("no"));
				nosamw.setText(rs.getString(rs.getColumnIndex("nosamw")));
				nopelanggan = rs.getString(rs.getColumnIndex("nosamw"));
				nama.setText(rs.getString(rs.getColumnIndex("nama")));
				txtMetL.setText(rs.getString(rs.getColumnIndex("met_l")));
				txtMetK.setText(rs.getString(rs.getColumnIndex("met_k")));
				txtPakai.setText(rs.getString(rs.getColumnIndex("pakai")));
			
				alamat.setText(rs.getString(rs.getColumnIndex("alamat")));
				keterangan.setText(rs.getString(rs.getColumnIndex("ket")));
				if (rs.getString(rs.getColumnIndex("status")).equals("1")) {
					nama.setTextColor(Color.parseColor("#008B05"));
					btnCamera.setEnabled(false);
				} else {
					nama.setTextColor(Color.parseColor("#000000"));
					btnCamera.setEnabled(true);
				}

				if (no == 1) {
					btnBack.setEnabled(false);
				} else if (no == totalData) {
					btnNext.setEnabled(false);
				} else {
					btnBack.setEnabled(true);
					btnNext.setEnabled(true);
				}
				if (!rs.isClosed()) {
					rs.close();
				}
			}

		} catch (SQLiteException se) {
			Log.e(getClass().getSimpleName(), "Error DB related ");

		} finally {

			db.close();
		}

	}

	public void klikButton(View v) {

		switch (v.getId()) {
		case R.id.btnBack:
			no--;
			loadDataFromDB(no);
			// txtpesan.setText("Happy Coding  Android");
			break;

		case R.id.btnNext:
			no++;
			loadDataFromDB(no);

			// txtpesan.setText("Selamat belajar Android");
			break;

		case R.id.btnSimpan:
			db.openDataBase();

			db.updatepelanggan(String.valueOf(no), keterangan.getText()
					.toString());
			Toast.makeText(this, "keterangan telah diupate", Toast.LENGTH_LONG)
					.show();
			// keterangan.setEnabled(false);
			// keterangan.setFocusable(false);
			// keterangan.requestFocus();
			keterangan.clearFocus();

			// txtpesan.setText("Selamat belajar Android");
			break;
		case R.id.btnCamera:
			Intent in = new Intent(getApplicationContext(),
					AndroidCamera3.class);
			in.putExtra("nopelanggan", nopelanggan);
			in.putExtra("no", String.valueOf(no));
			//finish();
			startActivity(in);

			// txtpesan.setText("Selamat belajar Android");
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_data, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title

		// Handle action bar actions click
		switch (item.getItemId()) {

		case R.id.list_data:
			Intent in = new Intent(getApplicationContext(),
					PelangganListActivity.class);
			finish();
			startActivity(in);

			break;
		case R.id.menu_setting:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Password");

			// Set up the input
			final EditText input = new EditText(this);
			// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
			input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			builder.setView(input);

			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			         if(input.getText().toString().equals("protect")){
			        	 Intent in2 = new Intent(getApplicationContext(),
			 					SettingActivity.class);
			 				startActivity(in2);
			         }else{
			        	 Toast.makeText(PelangganDataActivity.this,"Password salah", Toast.LENGTH_SHORT).show();
			         }
			    }
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.cancel();
			        
			    }
			});
			builder.show();
			

			break;
		case R.id.menu_about:
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("About");
			ab.setMessage("PDAM KAB. BATANG \n Jl. dr.Wahidin No.50 Batang");
			ab.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog dialog = ab.create();
			dialog.show();
			return (true);

		case R.id.menu_exit:
			
			int pid = android.os.Process.myPid();
			android.os.Process.killProcess(pid);
			System.exit(0);

			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

}
