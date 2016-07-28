package com.pdambaru;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/* proses menampilkan data di listview serta pencarian 
 * 
 */
public class PelangganListActivity extends Activity {
	DataBaseHelper db;
	private ListView lvPelanggan;
	private EditText inputCari;
	public PelangganAdapter adapter;
	private final int REQ_CODE_SPEECH_INPUT = 100;

	ImageButton btnVoice;
	List<Pelanggan> lsLokasi = new ArrayList<Pelanggan>();

	public PelangganListActivity() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pelanggan_activity);
		lvPelanggan = (ListView) findViewById(R.id.list_kamus);
		inputCari = (EditText) findViewById(R.id.input_cari);
		btnVoice = (ImageButton) findViewById(R.id.bnVoice);

		btnVoice.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();
			}
		});

		// call db stuff
		listAllData();

	}

	@Override
	public void onResume() {
		super.onResume();
		// put your code here...
		listAllData();

	}

	public void listAllData() {
		/* do database thinks */
		/* buka database terlebih dahulu */
		SQLiteDatabase dbl = null;
		db = new DataBaseHelper(this);

		try {
			db.openDataBase();
			dbl = db.getReadableDatabase();
			/* query kamus urutkan berdasarkan bahasa indoesia */

			String sql = " select * from pelanggan  group by nosamw order by no ";

			// sql.concat(object);
			Log.d("sql", sql);
			Cursor c = dbl.rawQuery(sql, null);

			if (c != null) {
				if (c.moveToFirst()) {
					do {

						lsLokasi.add(new Pelanggan(

						c.getString(c.getColumnIndex("no")), c.getString(c
								.getColumnIndex("nosamw")), c.getString(c
								.getColumnIndex("nama")), c.getString(c
								.getColumnIndex("alamat")), c.getString(c
								.getColumnIndex("ket")), c.getString(c
								.getColumnIndex("status")), c.getInt(c
								.getColumnIndex("met_l")), c.getInt(c
								.getColumnIndex("met_k")), c.getInt(c
								.getColumnIndex("pakai"))) // end of
															// kamusdata
						); // end of add

					} while (c.moveToNext());
				}
			}

		} catch (SQLiteException se) {
			Log.e(getClass().getSimpleName(), "Error DB related ");

		} finally {
			if (dbl != null)

				dbl.close();
			db.close();
		}
		adapter = new PelangganAdapter(this, lsLokasi);
		lvPelanggan.setAdapter(adapter);
		lvPelanggan.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// getting values from selected ListItem
				String no = ((TextView) view.findViewById(R.id.tvNo)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						PelangganDataActivity.class);
				// sending idnama_kasus to next activity
				in.putExtra("no", no);
			//	finish();
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);

			}
		});

		// lvkamus.setVisibility(View.INVISIBLE);
		/* kode untuk pencarian */
		inputCari.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				adapter.filter(cs.toString());
				/*
				 * if (cs.length() > 1) { lvkamus.setVisibility(View.VISIBLE); }
				 */
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

	}

	/*** FUNGSI PENCARIAN SUARA */
	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Input");
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					"input voice tidak support di Device ini",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Receiving speech input
	 * */

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				inputCari.setText(result.get(0));
			}
			break;
		}

		}
	}

}
