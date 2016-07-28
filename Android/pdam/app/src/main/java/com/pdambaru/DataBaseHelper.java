package com.pdambaru;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

/*
 * DatabaseHelper adalah klass yang berhubungan
 * dengan database/.
 * disini proses yang dilakukan adalah mengkopi database
 * dan membuka database 
 * 
 */

public class DataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.

	private static String DB_PATH = "/data/data/com.pdambaru/databases/";


	private static String DB_NAME = "pdam.db";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	private BufferedReader buffer;

	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		try {
		
			createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * cek apakah database exis, jika sudah ada DO NOTHING jika belum ada
	 * panggil fungsi copyDatabase();
	 * */
	public void createDataBase() throws IOException {
		boolean dbExist = databaseExist();

		if (dbExist) {
			// do nothing - database already exist
		} else {
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/*
	 * kode untuk megnecek apakah file database sudah ada
	 */

	public boolean databaseExist() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	/*
	 * fungsi untuk mengkopi database database pada mulanya ada di folder
	 * assets, database ini akan di kopikan ke SDCard dengan path di DB_PATH
	 */

	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) != -1) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	/*
	 * fungsi untuk membuka database SQLite
	 */

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS);

	}

	@Override
	/* fungsi untuk menutup database */
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	public Cursor getData(int no) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "select * from pelanggan where no='" + no + "'";
		Log.d("query", query);
		Cursor res = db.rawQuery(query, null);
		return res;
	}

	public boolean hapusAllData() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("delete from pelanggan");
		return true;

	}

	public boolean resetAllData() {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("update pelanggan set status='0'");
		// Toast.makeText(,"data sudah direset", Toast.LENGTH_LONG).show();
		return true;
	}

	public int totalData() {
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, "pelanggan");

		return numRows;
	}

	public boolean importFromCSV() {
		SQLiteDatabase db = this.getWritableDatabase();
		FileReader file = null;
		boolean status=false;
		String path = "/sdcard/importpdam.csv";
		try {
			file = new FileReader(path);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			 
		}
		BufferedReader	mybuffer = new BufferedReader(file);
		String line = "";
		String tableName = "pelanggan";
		String columns = "no,nosamw,nama,alamat,met_l,met_k,pakai,urjlw,urjlwp,ket,status,foto";
		String str1 = "INSERT INTO " + tableName + " (" + columns + ") values(";
		String str2 = ");";
		db.execSQL("delete from pelanggan");
		db.beginTransaction();
		
		try {
			
			
			while ((line = mybuffer.readLine()) != null) {
				StringBuilder sb = new StringBuilder(str1);
				String[] str = line.split(",");
				sb.append("'"+str[0].trim() + "',");
				sb.append("'"+str[1].trim() + "',");
				sb.append("'"+str[2].trim() + "',");
				sb.append("'"+str[3].trim() + "',");
				sb.append("'"+str[4].trim() + "',");
				sb.append("'"+str[5].trim() + "',");
				sb.append("'"+str[6].trim() + "',");
				sb.append("'"+str[7].trim() + "',");
				sb.append("'"+str[8].trim() + "',");
				sb.append("'"+str[9].trim() + "',");
				sb.append("'"+str[10].trim() + "',");
				sb.append("'"+str[11].trim()+"'");
				sb.append(str2);
				
				db.execSQL(sb.toString());
				
				//Log.d("sql",sb.toString());
				
			}
			mybuffer.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
		status=true;
		return status;
	}
	public boolean importFromCSV2() {
		SQLiteDatabase db = this.getWritableDatabase();
		FileReader file = null;
		boolean status=false;
		
		String path ="/sdcard/importpdam.csv";
		try {
			file = new FileReader(path);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			 
		}
		buffer = new BufferedReader(file);
		 String line = "";
			db.execSQL("delete from pelanggan");
		 db.beginTransaction();
		        try {
		            while ((line = buffer.readLine()) != null) {
		            String[] colums = line.split(",");
		                if (colums.length != 12) {
		                    Log.d("CSVParser", "Skipping Bad CSV Row");
		                    continue;
		                }
		                //"no,nosamw,nama,alamat,met_l,met_k,pakai,urjlw,urjlwp,ket,status,foto";
		                ContentValues cv = new ContentValues(12);
		                cv.put("no", colums[0].trim());
		                cv.put("nosamw", colums[1].trim());
		                cv.put("nama", colums[2].trim());
		                cv.put("alamat", colums[3].trim());
		                cv.put("met_l", colums[4].trim());
		                cv.put("met_k", colums[5].trim());
		                cv.put("pakai", colums[6].trim());
		                cv.put("urjlw", colums[7].trim());
		                cv.put("urjlwp", colums[8].trim());
		                cv.put("ket", colums[9].trim());
		                cv.put("status", colums[10].trim());
		                cv.put("foto", colums[11].trim());
		                db.insert("pelanggan", null, cv);
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		   db.setTransactionSuccessful();
		   db.endTransaction();
		return true;
	}

	public int totalDataTerisi() {
		String countQuery = "SELECT  * FROM  pelanggan where status='1' ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int cnt = cursor.getCount();
		cursor.close();
		return cnt;
	}

	public boolean updatepelanggan(String no, String keterangan) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("ket", keterangan);

		db.update("pelanggan", contentValues, "no = ? ", new String[] { no });
		return true;
	}

	public boolean updateInputData(int no, int input, int pakai, String foto) {
		String noLocal = String.valueOf(no);
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("met_k", input);
		contentValues.put("pakai", pakai);
		contentValues.put("foto", foto);
		contentValues.put("status", "1");

		db.update("pelanggan", contentValues, "no = ? ",
				new String[] { noLocal });
		return true;
	}

	public Boolean backupDatabaseCSV() {
		String outFileName = "exportpdam.csv";
		Log.d("backup", "backupDatabaseCSV");
		Boolean returnCode = false;
	
		String csvHeader = "no,nosamw,nama,alamat,met_l,met_k,pakai,urjlw,urjlwp,ket,status,foto\n";
		String csvValues = "";
		String countQuery = "SELECT  * FROM  pelanggan ";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(countQuery, null);

		try {
			File outFile = new File("/sdcard/", outFileName);
			FileWriter fileWriter = new FileWriter(outFile);
			BufferedWriter out = new BufferedWriter(fileWriter);
			/*
			 * c.getString(c.getColumnIndex("no")),
			 * c.getString(c.getColumnIndex("nosamw")),
			 * c.getString(c.getColumnIndex("nama")),
			 * c.getString(c.getColumnIndex("alamat")),
			 * c.getString(c.getColumnIndex("ket")),
			 * c.getString(c.getColumnIndex("status")),
			 * c.getInt(c.getColumnIndex("met_l")),
			 * c.getInt(c.getColumnIndex("met_k")),
			 * c.getInt(c.getColumnIndex("pakai")))
			 */
			if (c != null) {
				out.write(csvHeader);
				if (c.moveToFirst()) {
					do {
					csvValues = c.getString(c.getColumnIndex("no")) + ",";
					csvValues += c.getString(c.getColumnIndex("nosamw")) + ",";
					csvValues += c.getString(c.getColumnIndex("nama")).trim() + ",";
					csvValues += c.getString(c.getColumnIndex("alamat")).trim() + ",";
					csvValues += c.getString(c.getColumnIndex("met_l")) + ",";
					csvValues += c.getString(c.getColumnIndex("met_k")) + ",";
					csvValues += c.getString(c.getColumnIndex("pakai")) + ",";
					csvValues += c.getString(c.getColumnIndex("urjlw")) + ",";
					csvValues += c.getString(c.getColumnIndex("urjlwp")) + ",";
					csvValues += c.getString(c.getColumnIndex("ket")).trim() + ",";
					csvValues += c.getString(c.getColumnIndex("status")) + ",";
					csvValues += c.getString(c.getColumnIndex("foto")) + "\n";
					out.write(csvValues);
				}while(c.moveToNext());
				}//end of if
				c.close();
			}
			out.close();
			returnCode = true;
		} catch (IOException e) {
			returnCode = false;
			Log.d("Error", "IOException: " + e.getMessage());
		}
		db.close();
		return returnCode;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}