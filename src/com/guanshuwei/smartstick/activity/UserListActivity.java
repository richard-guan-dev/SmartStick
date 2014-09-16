package com.guanshuwei.smartstick.activity;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.guanshuwei.smartstick.R;
import com.guanshuwei.smartstick.adapter.UserListAdapter;
import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.data.UserInfoDatabase;
import com.guanshuwei.smartstick.instance.UserModule;

public class UserListActivity extends Activity {

	private ListView mUserListView;
	private Button mAddUserButtom;
	private Button mBack;

	private UserListAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);

		this.mBack = (Button) this.findViewById(R.id.back);
		this.mBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		this.mUserListView = (ListView) findViewById(R.id.listView1);
		this.mAddUserButtom = (Button) findViewById(R.id.adduser);

		this.mAddUserButtom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UserListActivity.this, UserDetailActivity.class);
				intent.putExtra(Constant.USER_INFO_OPERATION,
						Constant.USER_INFO_OPERATION_ADD);
				UserListActivity.this.startActivity(intent);
			}
		});

		UserInfoDatabase userDatabse = new UserInfoDatabase(this);

		final Vector<UserModule> userList = userDatabse.getAll();

		this.mListAdapter = new UserListAdapter(userList, this);

		this.mUserListView.setAdapter(this.mListAdapter);
		this.mUserListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		UserInfoDatabase userDatabse = new UserInfoDatabase(this);
		final Vector<UserModule> userList = userDatabse.getAll();
		this.mListAdapter.setUserList(userList);
		this.mListAdapter.notifyDataSetChanged();
	}

	public ListView getUserListView() {
		return mUserListView;
	}
}
