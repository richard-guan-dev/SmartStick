package com.guanshuwei.smartstick.util;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.instance.UserModule;

public class CommandSender {
	private static CommandSender Instance = null;
	private String mLastCommand = "";

	private CommandSender() {
	}

	public static CommandSender getInstance() {
		if (CommandSender.Instance == null) {
			CommandSender.Instance = new CommandSender();
		}

		return Instance;
	}

	public void makePhoneCall(String phoneNumber, Context context) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNumber));
		context.startActivity(intent);
	}

	public void locateGPSPoint(Context context) {
		String phoneNum = UserStore.getInstance().getCurrentUser(context)
				.getUserPhoneNumber();
		String message = Constant.COMMAND_STATUS;
		sendSMS(phoneNum, message, context);
	}

	public void openGPS(Context context) {
		String phoneNum = UserStore.getInstance().getCurrentUser(context)
				.getUserPhoneNumber();
		String message = Constant.COMMAND_OPEN_GPS;
		sendSMS(phoneNum, message, context);
	}

	public void BindUser(Context context, String phoneNumber) {
		String message = Constant.COMMAND_BINDER_OPEN;
		sendSMS(phoneNumber, message, context);
	}

	public void UnBindUser(Context context, String phoneNumber) {
		String message = Constant.COMMAND_BINDER_CLOSE;
		sendSMS(phoneNumber, message, context);
	}

	private void sendSMS(String phoneNum, String message, Context context) {
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0,
				new Intent(), 0);
		if (message.length() > 70) {
			ArrayList<String> msgs = smsManager.divideMessage(message);
			for (String msg : msgs) {
				smsManager.sendTextMessage(phoneNum, null, msg, sentIntent,
						null);
			}
		} else {
			smsManager.sendTextMessage(phoneNum, null, message, sentIntent,
					null);
		}
	}

	public void checkStatus(Context mainActivity) {
		UserModule user = UserStore.getInstance().getCurrentUser(mainActivity);
		if (!Utilities.isStringNullOrEmpty(user.getUserPhoneNumber())) {
			sendSMS(user.getUserPhoneNumber(), Constant.COMMAND_CHECK_STATUS,
					mainActivity);
			this.mLastCommand = Constant.COMMAND_CHECK_STATUS_MARK;
		}
	}

	public void getLocation(Context mainActivity) {
		UserModule user = UserStore.getInstance().getCurrentUser(mainActivity);
		if (!Utilities.isStringNullOrEmpty(user.getUserPhoneNumber())) {
			sendSMS(user.getUserPhoneNumber(), Constant.COMMAND_LOCATE_CLIENT,
					mainActivity);
			this.mLastCommand = Constant.COMMAND_LOCATE_CLIENT_MARK;
		}
	}

	public String getLastCommand() {
		return mLastCommand;
	}

	public void setLastCommand(String lastCommand) {
		mLastCommand = lastCommand;
	}
}
