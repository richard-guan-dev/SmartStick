package com.guanshuwei.smartstick.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.guanshuwei.smartstick.R;
import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.instance.UserModule;
import com.guanshuwei.smartstick.util.CommandSender;
import com.guanshuwei.smartstick.util.UserStore;
import com.guanshuwei.smartstick.util.Utilities;

public class MainActivity extends Activity {

	private MsgReceiver mMsgReceiver;

	private Button mUserListButton;
	private Button mCheckButton;
	private Button mLocateButton;

	private TextView mUserName;
	private TextView mBatteryText;
	private TextView mTemperatureText;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(this.mMsgReceiver);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// this.mSMSBroadcastReceiver = new SMSBroadcastReceiver();
		//
		// IntentFilter filter = new IntentFilter();
		// // add Action atgument to filter
		// filter.addAction(SMS_ACTION);
		// // send a Broadcast to Receiver，为smsr对象注册Receive的filter
		// this.registerReceiver(this.mSMSBroadcastReceiver, filter);
		//
		// System.out.println("Broadcast has Broaded!");

		this.mCheckButton = (Button) this.findViewById(R.id.check_status);
		this.mLocateButton = (Button) this.findViewById(R.id.locate_client);

		this.mBatteryText = (TextView) this.findViewById(R.id.battery);
		this.mUserName = (TextView) this.findViewById(R.id.user_name_main);
		this.mTemperatureText = (TextView) this.findViewById(R.id.temperature);

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
			}
		});

		this.mLocateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommandSender.getInstance().getLocation(MainActivity.this);
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
		Intent intent = new Intent(Constant.ACTION_SMS_SERVICE);
		this.startService(intent);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("SdCardPath")
	private boolean isInstallByread(String packageName) {
		return new File("/data/data/" + packageName).exists();
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
									// 单击事件
									@SuppressWarnings("deprecation")
									public void onClick(DialogInterface dialog,
											int which) {

										try {
											Intent bdIntent = new Intent(
													Intent.ACTION_VIEW);
											String longitude = String.valueOf(intent
													.getDoubleExtra(
															Constant.MESSAGE_GPS_LONGITUDE,
															0));
											String latitude = String.valueOf(intent
													.getDoubleExtra(
															Constant.MESSAGE_GPS_LATITUDE,
															0));

											Toast.makeText(
													MainActivity.this,
													"lon:" + longitude
															+ ",lat:"
															+ latitude,
													Toast.LENGTH_LONG).show();

											Uri uri = Uri
													.parse("map/marker?location="
															+ latitude
															+ ","
															+ longitude + "&coord_type=wgs844&title=我的位置&content=用户位置&referer=USTB|SmartStick");
											bdIntent.setData(uri);

//											bdIntent = Intent
//													.getIntent("intent://map/geocoder?location="
//															+ latitude
//															+ ","
//															+ longitude
//															+ "&coord_type=gcj02&src=SmartStick|SmartStick#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
											 bdIntent.setPackage("com.baidu.BaiduMap");
//											bdIntent = Intent
//													.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
											bdIntent = Intent
													.getIntent("intent://map/marker?location="
															+ longitude
															+ ","
															+ latitude
															+ "&coord_type=wgs84&title=我的位置&content=用户位置&src=USTB|SmartStick#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
//											bdIntent = Intent.getIntent("intent://map/marker?location=39.916979519873,116.41004950566&title=我的位置&content=百度奎科大厦&referer=USTB|SmartStick#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
											Log.i("intent",
													"intent://map/geocoder?location="
															+ longitude
															+ ","
															+ latitude
															+ "&coord_type=gcj02#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
											if (isInstallByread("com.baidu.BaiduMap")) {
												startActivity(bdIntent); // 启动调用
												Log.e("GasStation",
														"百度地图客户端已经安装");
											} else {
												Log.e("GasStation",
														"没有安装百度地图客户端");
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}).
						// 设置取消按钮
						setNegativeButton("否",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								});
				// 创建对话框
				AlertDialog ad = builder.create();
				// 显示对话框
				ad.show();

			} else if (messageKind.equals(Constant.MESSAGE_STATUS)) {
				String battery = intent
						.getStringExtra(Constant.MESSAGE_STATUS_BATTERY);
				mBatteryText.setText(battery + "%");
				String temperature = intent
						.getStringExtra(Constant.MESSAGE_STATUS_TEMPERATURE);
				mTemperatureText.setText(temperature);
			}

		}
	}
}
