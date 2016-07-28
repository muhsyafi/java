package com.pdambaru;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PelangganInputActivity extends Activity {

	int from_Where_I_Am_Coming = 0;
	DataBaseHelper db;
	EditText txtNama;
	EditText txtAlamat;
	EditText txtNoPelanggan;
	EditText txtInputMeter;
	EditText txtMeterLalu;

	int no = 0;

	int id_To_Update = 0;
	String nopelanggan;
	String inputMeter;
	String foto;
	int met_l = 0;
	int pakai = 0;

	Button btnSimpanInput;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);
		// get extra

		String timeStamp = new SimpleDateFormat("MMMM yyyy").format(new Date());
		setTitle(timeStamp);
		Intent intent = getIntent();
		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {

			if (extras.containsKey("no")) {
				no = Integer.valueOf(getIntent().getExtras().getString("no"));
			}

			if (extras.containsKey("foto")) {
				foto = getIntent().getExtras().getString("foto", "-");
			}
		}

		btnSimpanInput = (Button) findViewById(R.id.btnSimpanInput);
		txtInputMeter = (EditText) findViewById(R.id.txtInputMeter);
		txtNoPelanggan = (EditText) findViewById(R.id.txtNoPelanggan);
		txtMeterLalu=(EditText) findViewById(R.id.txtMeterLalu);
		txtNama = (EditText) findViewById(R.id.txtNama);
		txtAlamat = (EditText) findViewById(R.id.txtAlamat);

		txtNama.setKeyListener(null);
		// txtAlamat.setKeyListener(null);
		// txtNoPelanggan.setKeyListener(null);
		db = new DataBaseHelper(this);
		tampilData(no);

	}

	public void tampilData(int nomor) {
		try {
			db.openDataBase();
			Cursor rs = db.getData(nomor);
			Log.d("TAMPIL", "difungsi tampilData");
			if (rs.getCount() > 0) {
				rs.moveToFirst();
				no = rs.getInt(rs.getColumnIndex("no"));
				met_l = rs.getInt(rs.getColumnIndex("met_l"));
				txtMeterLalu.setText(rs.getString(rs.getColumnIndex("met_l")));
				txtNoPelanggan
						.setText(rs.getString(rs.getColumnIndex("nosamw")));
				txtNama.setText(rs.getString(rs.getColumnIndex("nama")));
				txtAlamat.setText(rs.getString(rs.getColumnIndex("alamat")));
				Log.d("TAMPIL2", "di dalam rs.getCont() tampilData");

			}

		} catch (SQLiteException se) {
			Log.e(getClass().getSimpleName(), "Error DB related ");

		} finally {

			db.close();
		}

	}

	public void klikSimpan(View v) {

		switch (v.getId()) {

		case R.id.btnSimpanInput:
			int statusInput = 0;
			int input = 0;

			if (txtInputMeter.getText().length() < 1) {
				Toast.makeText(this, "Input tidak boleh kosong",
						Toast.LENGTH_LONG).show();
				statusInput = 1;

			}
			if (statusInput == 0) {
				input = Integer.parseInt(txtInputMeter.getText().toString());

				pakai = input - met_l;
			/*	if (input <= met_l) {
					Toast.makeText(this, "Input harus >=  " + met_l,
							Toast.LENGTH_LONG).show();
					statusInput = 2;
				}
*/
			}

			if (statusInput == 0) {

				db.openDataBase();

				db.updateInputData(no, input, pakai, foto);
				Toast.makeText(this, "input data sukses ", Toast.LENGTH_LONG)
						.show();
				Intent in = new Intent(getApplicationContext(),
						PelangganDataActivity.class);
				//	finish();
				in.putExtra("no", String.valueOf(no));
				startActivity(in);
			}

			break;

		default:
			break;
		}
	}

}
