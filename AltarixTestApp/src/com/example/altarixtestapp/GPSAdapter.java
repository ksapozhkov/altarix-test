package com.example.altarixtestapp;

import java.util.List;

import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GPSAdapter extends ArrayAdapter<Marker> {

	Context context;

	public GPSAdapter(Context context, int textViewResourceId,
			List<Marker> markers) {
		super(context, textViewResourceId, markers);
		this.context = context;
	}

	/* private view holder class */
	private class ViewHolder {
		TextView txtLat;
		TextView txtLong;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Marker rowItem = getItem(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_row, null);
			holder = new ViewHolder();
			holder.txtLat = (TextView) convertView.findViewById(R.id.latitude);
			holder.txtLong = (TextView) convertView.findViewById(R.id.longitude);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		holder.txtLat.setText(String.valueOf(rowItem.getPosition().latitude));
		holder.txtLong.setText(String.valueOf(rowItem.getPosition().longitude));

		return convertView;
	}

}
