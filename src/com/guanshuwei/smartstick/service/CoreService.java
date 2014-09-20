package com.guanshuwei.smartstick.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.data.HistoryDatabase;
import com.guanshuwei.smartstick.instance.HistoryModule;
import com.guanshuwei.smartstick.instance.HistoryModule.LogType;
import com.guanshuwei.smartstick.util.CommandSender;
import com.guanshuwei.smartstick.util.UserStore;

public class CoreService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private CommandReceiver mReceiver;

	public class LocalBinder extends Binder {
		public CoreService getService() {
			return CoreService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			mReceiver = new CommandReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constant.ACTION_RECEIVED_MESSAGE);
			registerReceiver(mReceiver, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(mReceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void receivedGPSMessage(double longitude, double latitule) {
		Intent intent = new Intent(Constant.ACTION_MSG_RECEIVER);
		intent.setAction(Constant.ACTION_MSG_RECEIVER);
		intent.putExtra(Constant.MESSAGE_KIND, Constant.MESSAGE_GPS);
		intent.putExtra(Constant.MESSAGE_GPS_LONGITUDE, longitude);
		intent.putExtra(Constant.MESSAGE_GPS_LATITUDE, latitule);
		this.sendBroadcast(intent);
	}

	private void receivedStatusMessage(int battery, int temperature) {
		Intent intent = new Intent(Constant.ACTION_MSG_RECEIVER);
		intent.setAction(Constant.ACTION_MSG_RECEIVER);
		intent.putExtra(Constant.MESSAGE_KIND, Constant.MESSAGE_STATUS);
		intent.putExtra(Constant.MESSAGE_STATUS_BATTERY,
				String.valueOf(battery));
		intent.putExtra(Constant.MESSAGE_STATUS_TEMPERATURE,
				String.valueOf(temperature));

		this.sendBroadcast(intent);
	}

	private void receivedAlert() {
		Intent intent = new Intent(Constant.ACTION_MSG_RECEIVER);
		intent.putExtra(Constant.MESSAGE_KIND, Constant.MESSAGE_ALERT);
		this.sendBroadcast(intent);
	}

	private void receivedDisableAlert() {
		Intent intent = new Intent(Constant.ACTION_MSG_RECEIVER);
		intent.putExtra(Constant.MESSAGE_KIND, Constant.MESSAGE_DISABLE_ALERT);
		this.sendBroadcast(intent);
	}

	private boolean isGPSMessage(String smsBody) {
		if (smsBody.contains("xwx://")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isStatusMessage(String smsBody) {
		if (smsBody.contains("电池剩余电量")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isDisableAlert(String smsBody) {
		if (smsBody.contains("主动解除") || smsBody.contains("机主状态正常")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isAlert(String smsBody) {
		if (smsBody.contains("自动报警")) {
			return true;
		} else {
			return false;
		}
	}

	private void handlerSMS(String smsBody) {
		try {
			if (isGPSMessage(smsBody)) {
				double longitude = 0, latitule = 0;
				String[] result = smsBody.split("//");
				String[] numbers = result[1].split("&");
				longitude = Double.parseDouble(numbers[1].substring(1));
				latitule = Double.parseDouble(numbers[0].substring(1));
				this.receivedGPSMessage(longitude, latitule);
				CommandSender.getInstance().setLastCommand("");
				String name = UserStore.getInstance().getCurrentUser(this)
						.getUserName();
				String phone = UserStore.getInstance().getCurrentUser(this)
						.getUserPhoneNumber();

				HistoryModule history = new HistoryModule();
				history.setLatitude(latitule);
				history.setLongitude(longitude);
				history.setLogType(LogType.LOCATE);
				history.setUserName(name);
				history.setUserPhoneNumber(phone);

				HistoryDatabase database = new HistoryDatabase(this);
				database.addItem(history);

			} else if (isStatusMessage(smsBody)) {
				int battery = 0;
				int tempreature = 0;
				String[] result = smsBody.split("%");
				battery = Integer.parseInt(result[0].split("约")[1]);
				tempreature = Integer.parseInt(result[1].split("温度")[1]
						.split("℃")[0]);
				this.receivedStatusMessage(battery, tempreature);
				CommandSender.getInstance().setLastCommand("");
			} else if (isAlert(smsBody)) {
				this.receivedAlert();
				CommandSender.getInstance().setLastCommand("");

				String name = UserStore.getInstance().getCurrentUser(this)
						.getUserName();
				String phone = UserStore.getInstance().getCurrentUser(this)
						.getUserPhoneNumber();
				HistoryModule history = new HistoryModule();
				history.setLogType(LogType.ALERT);
				history.setUserName(name);
				history.setUserPhoneNumber(phone);

				HistoryDatabase database = new HistoryDatabase(this);
				database.addItem(history);
			} else if (isDisableAlert(smsBody)) {
				this.receivedDisableAlert();
				CommandSender.getInstance().setLastCommand("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CommandReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent.getAction().equals(Constant.ACTION_RECEIVED_MESSAGE)) {
					handlerSMS(intent.getStringExtra(Constant.SMS_BODY));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
