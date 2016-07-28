package com.pdambaru;

import java.util.ArrayList;
import java.util.List;




import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/* 
 * adapater dalam konsep MVC adalah controllernya
 * disinilah proses menempatkan kata sesuai pada tempatnya di Listvew
 * kelas ini juga bertanggung jawab dalam pencarian data 
 */
public class PelangganAdapter extends BaseAdapter
{
	private Context mContext;
	private List<Pelanggan> mlistLokasi;
	private ArrayList<Pelanggan> listlokasiasli;

	public PelangganAdapter(Context context, List<Pelanggan> list) {
		mContext = context;
		mlistLokasi = list;//list;
	//	mlistLokasi.clear();
		 this.listlokasiasli = new ArrayList<Pelanggan>();
		 this.listlokasiasli.addAll(list);

	}

	@Override
	public int getCount() {
		return mlistLokasi.size();
	}

	@Override
	public Object getItem(int pos) {
		return mlistLokasi.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// get selected entry
		Pelanggan entry = mlistLokasi.get(pos);

		// inflating list view layout if null
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(R.layout.pelanggan_row, null);
		}

		

		// set nama
		TextView tvNama = (TextView) convertView.findViewById(R.id.tvNama);
		tvNama.setText(entry.getNama());
		
		TextView tvAlamat = (TextView) convertView.findViewById(R.id.tvAlamat);
		tvAlamat.setText(entry.getAlamat());

		TextView tvKeterangan = (TextView) convertView.findViewById(R.id.tvKeterangan);
		tvKeterangan.setText(entry.getKeterangan());
		
		TextView tvNo = (TextView) convertView.findViewById(R.id.tvNo);
		tvNo.setText(entry.getNo());
		
		TextView tvPengukuran = (TextView) convertView.findViewById(R.id.tvPengukuran);
		tvPengukuran.setText(entry.tampilUkur());
		
		
		TextView tvNosamw = (TextView) convertView.findViewById(R.id.tvNosamw);
		tvNosamw.setText(entry.getNosamw());
		if(entry.getStatus().equals("1")){
			tvNama.setTextColor(Color.parseColor("#008B05"));
		}else{
			tvNama.setTextColor(Color.parseColor("#000000"));
		}
		

		return convertView;
	}

	@SuppressLint("DefaultLocale")
	/* fungsi pencarian
	 * jika tidak ada input yang dicari tampilkan seluruh data 
	 */
	public void filter(String charText) {
		//charText = charText.toLowerCase();
		mlistLokasi.clear();
		if (charText.length() == 0) {
			/* tampilkan seluruh data */
			mlistLokasi.addAll(listlokasiasli);
		} else {
			/* jika adainput yang dicari, tampilkan
			 * kata yang sesuai mengandung frase tersebut  
			 */
			mlistLokasi.clear();
			for (Pelanggan lok :listlokasiasli) {
				if (lok.getNosamw().contains(charText)) {
					
					mlistLokasi.add(lok);
					
				}
			}
		}
		notifyDataSetChanged();
	}



}
