package com.guanshuwei.smartstick.adapter;

import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.guanshuwei.smartstick.R;
import com.guanshuwei.smartstick.activity.MapActivity;
import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.instance.HistoryModule;
import com.guanshuwei.smartstick.instance.HistoryModule.LogType;

public class HistoryListAdapter extends BaseAdapter {
	private Vector<HistoryModule> mHistoryList;
	private Context mContext;

	public HistoryListAdapter(Vector<HistoryModule> mUserList, Context mContext) {
		super();
		this.mHistoryList = mUserList;
		this.mContext = mContext;
	}

	public HistoryListAdapter(Vector<HistoryModule> userList) {
		super();
		this.mHistoryList = userList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mHistoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mHistoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ViewHolder {
		TextView nameTextView;
		TextView phoneTextVew;
		TextView typeTextView;
		TextView longitudeTextView;
		TextView latitudeTextView;
		TextView dateTextView;
		Button mapButton;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.item_history, null);
		}

		HistoryModule history = this.mHistoryList.get(position);

		viewHolder.phoneTextVew = (TextView) convertView
				.findViewById(R.id.history_user_phone);
		viewHolder.nameTextView = (TextView) convertView
				.findViewById(R.id.history_user_name);
		viewHolder.longitudeTextView = (TextView) convertView
				.findViewById(R.id.history_longitude);
		viewHolder.latitudeTextView = (TextView) convertView
				.findViewById(R.id.history_latitude);
		viewHolder.mapButton = (Button) convertView
				.findViewById(R.id.history_go_to_map);
		viewHolder.typeTextView = (TextView) convertView
				.findViewById(R.id.history_log_type);
		viewHolder.dateTextView = (TextView) convertView
				.findViewById(R.id.history_time);

		viewHolder.phoneTextVew.setText(history.getUserPhoneNumber());
		viewHolder.nameTextView.setText(history.getUserName());

		if (history.getLogType().equals(LogType.ALERT)) {
			viewHolder.typeTextView.setText("跌倒警报");
			viewHolder.longitudeTextView.setText(history.getmDate());
			viewHolder.longitudeTextView.setText("");
			viewHolder.latitudeTextView.setText("");
			viewHolder.mapButton.setVisibility(Button.GONE);
		} else {
			viewHolder.typeTextView.setText("位置");
			viewHolder.longitudeTextView.setText(String.valueOf(history
					.getLongitude()));
			viewHolder.latitudeTextView.setText(String.valueOf(history
					.getLatitude()));
			viewHolder.mapButton.setVisibility(Button.VISIBLE);
		}

		viewHolder.dateTextView.setText(history.getmDate());

		viewHolder.mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HistoryModule history = mHistoryList.get(position);
				if (history.getLogType().equals(LogType.LOCATE)) {
					try {
						Intent mapIntent = new Intent();
						mapIntent.setClass(mContext, MapActivity.class);
						mapIntent.putExtra(Constant.GPS_LONTITULE,
								history.getLongitude());
						mapIntent.putExtra(Constant.GPS_LATITULE,
								history.getLatitude());
						mContext.startActivity(mapIntent);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		return convertView;
	}

	public Vector<HistoryModule> getHistoryList() {
		return mHistoryList;
	}

	public void setHistoryList(Vector<HistoryModule> mUserList) {
		this.mHistoryList = mUserList;
	}
}
