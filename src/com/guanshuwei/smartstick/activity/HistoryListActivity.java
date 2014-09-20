package com.guanshuwei.smartstick.activity;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.guanshuwei.smartstick.R;
import com.guanshuwei.smartstick.adapter.HistoryListAdapter;
import com.guanshuwei.smartstick.data.HistoryDatabase;
import com.guanshuwei.smartstick.instance.HistoryModule;

public class HistoryListActivity extends Activity {

	private ListView mHistoryListView;
	private Button mBack;

	private HistoryListAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		this.mBack = (Button) this.findViewById(R.id.back);
		this.mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		this.mHistoryListView = (ListView) findViewById(R.id.listView1);

		HistoryDatabase historyDatabse = new HistoryDatabase(this);

		final Vector<HistoryModule> historyList = historyDatabse.getAll();

		this.mListAdapter = new HistoryListAdapter(historyList, this);

		this.mHistoryListView.setAdapter(this.mListAdapter);
		this.mHistoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		HistoryDatabase historyDatabse = new HistoryDatabase(this);
		final Vector<HistoryModule> historyList = historyDatabse.getAll();
		this.mListAdapter.setHistoryList(historyList);
		this.mListAdapter.notifyDataSetChanged();
	}

	public ListView getUserListView() {
		return mHistoryListView;
	}
}
