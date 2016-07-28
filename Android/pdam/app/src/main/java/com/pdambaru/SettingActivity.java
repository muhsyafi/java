package com.pdambaru;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class SettingActivity extends Activity {

	GridView gridView;
	SettingAdapter grisViewCustomeAdapter;
	DataBaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);
		setTitle("Setting");
		// load database
		db = new DataBaseHelper(this);
		db.openDataBase();
		gridView = (GridView) findViewById(R.id.gridViewCustom);
		// Create the Custom Adapter Object
		grisViewCustomeAdapter = new SettingAdapter(this);
		// Set the Adapter to GridView
		gridView.setAdapter(grisViewCustomeAdapter);

		// Handling touch/click Event on GridView Item
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posisi,
					long arg3) {
				switch (posisi) {
				case 0:
					open("Apakah anda akan mengexport data ke CSV?", 0);
					// exportCVS();
					break;
				case 1:
					// importCVS();
					open("Apakah anda akan mengimport data ke CSV?", 1);
					break;
				case 2:
					// resetData();
					open("Apakah anda yakin akan mereset tabel?", 2);

					// db.resetAllData();

					break;
				case 3:
					// clearData();
					open("Apakah anda akan mengosongkan data?", 3);
					
					break;

				}

			}
		});

	}

	// open dialog
	public void open(String pesan, final int aksi) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(pesan);

		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						switch (aksi) {
						case 0:
							// do backup data
							if (db.backupDatabaseCSV()) {

								Toast.makeText(getApplicationContext(),
										"data pelanggan berhasil dibackup",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"data pelanggan GAGAL dibackup",
										Toast.LENGTH_LONG).show();
							}
							break;
						case 1:
							if (db.importFromCSV2()) {

								Toast.makeText(getApplicationContext(),
										"data pelanggan berhasil diimport, silahkan buka kembali aplikasi",
										Toast.LENGTH_LONG).show();
								System.exit(0);
								
							} else {
								Toast.makeText(getApplicationContext(),
										"data pelanggan GAGAL di import",
										Toast.LENGTH_LONG).show();
							}
							// do import data
							break;
						case 2:
							// do reset data
							if (db.resetAllData()) {

								Toast.makeText(getApplicationContext(),
										"Tabel pelanggan berhasil di RESET",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Tabel pelanggan GAGAL di Reset",
										Toast.LENGTH_LONG).show();
							}

							break;
						case 3:
							// do empty data
							if (db.hapusAllData()) {

								Toast.makeText(getApplicationContext(),
										"tabel pelanggan berhasil dikosongkan",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"tabel pelanggan gagal di kosongkan",
										Toast.LENGTH_LONG).show();
							}
							break;
						}

					}
				});

		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(SettingActivity.this,
								"You clicked No button", Toast.LENGTH_LONG)
								.show();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void exportCSV() {
		String columnString = "\"PersonName\",\"Gender\",\"Street1\",\"postOffice\",\"Age\"";
		String dataString = ""; // "\"" + currentUser.userName +"\",\"" +
								// currentUser.gender + "\",\"" +
								// currentUser.street1 + "\",\"" +
								// currentUser.postOFfice.toString()+ "\",\"" +
								// currentUser.age.toString() + "\"";
		String combinedString = columnString + "\n" + dataString;

		File file = null;
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			File dir = new File(root.getAbsolutePath() + "/PersonData");
			dir.mkdirs();
			file = new File(dir, "Data.csv");
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				out.write(combinedString.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
