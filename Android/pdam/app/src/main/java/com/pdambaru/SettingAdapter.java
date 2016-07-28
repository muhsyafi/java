package com.pdambaru;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

public class SettingAdapter extends ArrayAdapter<Object> {
	Context context;

	public SettingAdapter(Context context) {
		super(context, 0);
		this.context = context;

	}

	public int getCount() {
		return 4;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.grid_item, parent, false);

			TextView textViewTitle = (TextView) row.findViewById(R.id.textView);
			ImageView imageViewIte = (ImageView) row
					.findViewById(R.id.imageView);
			switch (position) {
			case 0:
				textViewTitle.setText("export");
				imageViewIte.setImageResource(R.drawable.export);
				break;
			case 1:
				textViewTitle.setText("Import");
				imageViewIte.setImageResource(R.drawable.impor);
				break;
			case 2:
				textViewTitle.setText("reset");
				imageViewIte.setImageResource(R.drawable.reset);
				break;
			case 3:
				textViewTitle.setText("clear");
				imageViewIte.setImageResource(R.drawable.trash);
				break;

			}

		}

		return row;

	}

}