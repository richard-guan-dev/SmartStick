package com.guanshuwei.smartstick.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.guanshuwei.smartstick.R;
import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.instance.UserModule;
import com.guanshuwei.smartstick.service.CoreService;
import com.guanshuwei.smartstick.util.CommandSender;
import com.guanshuwei.smartstick.util.UserStore;
import com.guanshuwei.smartstick.util.Utilities;

public class MainActivity extends Activity {

	private MsgReceiver mMsgReceiver;

	private Button mUserListButton;
	private Button mCheckButton;
	private Button mLocateButton;
	private Button mHistoryButton;

	private TextView mUserName;
	private TextView mBatteryText;
	private TextView mTemperatureText;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();

		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(this.mMsgReceiver);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		this.mCheckButton = (Button) this.findViewById(R.id.check_status);
		this.mLocateButton = (Button) this.findViewById(R.id.locate_client);
		this.mHistoryButton = (Button) this.findViewById(R.id.History);

		this.mBatteryText = (TextView) this.findViewById(R.id.battery);
		this.mUserName = (TextView) this.findViewById(R.id.user_name_main);
		this.mTemperatureText = (TextView) this.findViewById(R.id.temperature);

		this.mBatteryText.setTextColor(android.graphics.Color.GRAY);
		this.mTemperatureText.setTextColor(android.graphics.Color.GRAY);
		
		this.mBatteryText.setText(UserStore.getInstance().getLastBattery(this));
		this.mTemperatureText.setText(UserStore.getInstance().getLastTemprature(this));

		this.mHistoryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						HistoryListActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});

		UserModule currentUser = UserStore.getInstance().getCurrentUser(this);

		if (!Utilities.isStringNullOrEmpty(currentUser.getUserName())) {
			this.mUserName.setText(currentUser.getUserName());
		} else {
			this.mUserName.setText("请先设置用户");
		}

		this.mCheckButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommandSender.getInstance().checkStatus(MainActivity.this);
				Toast.makeText(MainActivity.this, "状态查询指令已发送", Toast.LENGTH_LONG).show();
			}
		});

		this.mLocateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommandSender.getInstance().getLocation(MainActivity.this);
				Toast.makeText(MainActivity.this, "位置查询指令已发送", Toast.LENGTH_LONG).show();
			}
		});

		this.mMsgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constant.ACTION_MSG_RECEIVER);
		registerReceiver(this.mMsgReceiver, intentFilter);

		this.mUserListButton = (Button) this.findViewById(R.id.userlistbutton);
		mUserListButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, UserListActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, CoreService.class);
		this.startService(intent);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		UserModule currentUser = UserStore.getInstance().getCurrentUser(this);

		if (!Utilities.isStringNullOrEmpty(currentUser.getUserName())) {
			this.mUserName.setText(currentUser.getUserName());
		} else {
			this.mUserName.setText("请先设置用户");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class MsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, final Intent intent) {
			Log.i("Received", intent.getAction());
			String messageKind = intent.getStringExtra(Constant.MESSAGE_KIND);

			if (messageKind.equals(Constant.MESSAGE_GPS)) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setMessage("收到用户定位信息，是否打开百度地图跟踪？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										try {
											Intent mapIntent = new Intent();
											mapIntent.setClass(
													MainActivity.this,
													MapActivity.class);
											mapIntent
													.putExtra(
															Constant.GPS_LONTITULE,
															intent.getDoubleExtra(
																	Constant.MESSAGE_GPS_LONGITUDE,
																	0));
											mapIntent
													.putExtra(
															Constant.GPS_LATITULE,
															intent.getDoubleExtra(
																	Constant.MESSAGE_GPS_LATITUDE,
																	0));
											startActivity(mapIntent);

										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								})
						.setNegativeButton("否",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
				AlertDialog ad = builder.create();
				ad.show();

			} else if (messageKind.equals(Constant.MESSAGE_STATUS)) {
				mBatteryText.setTextColor(android.graphics.Color.BLACK);
				mTemperatureText.setTextColor(android.graphics.Color.BLACK);
				String battery = intent
						.getStringExtra(Constant.MESSAGE_STATUS_BATTERY);
				mBatteryText.setText(battery + "%");
				String temperature = intent
						.getStringExtra(Constant.MESSAGE_STATUS_TEMPERATURE);
				mTemperatureText.setText(temperature);
				UserStore.getInstance().setBattery((battery), MainActivity.this);
				UserStore.getInstance().setTemprature((temperature), MainActivity.this);
				Toast.makeText(MainActivity.this, "电量和温度信息已更新",
						Toast.LENGTH_LONG);
			} else if (messageKind.equals(Constant.MESSAGE_ALERT)) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setMessage("自动报警！用户可能跌倒，正在获取位置信息...")
						.setPositiveButton("拨打电话",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										String number = UserStore
												.getInstance()
												.getCurrentUser(
														MainActivity.this)
												.getUserPhoneNumber();
										CommandSender.getInstance()
												.makePhoneCall(number,
														MainActivity.this);
									}
								})
						.setNegativeButton("继续等待",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
				AlertDialog ad = builder.create();
				ad.show();
			} else if (messageKind.equals(Constant.MESSAGE_DISABLE_ALERT)) {
				Toast.makeText(MainActivity.this, "警报已解除！", Toast.LENGTH_LONG)
						.show();
			}
		}
	}
}
