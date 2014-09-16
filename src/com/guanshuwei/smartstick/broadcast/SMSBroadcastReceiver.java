package com.guanshuwei.smartstick.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.instance.UserModule;
import com.guanshuwei.smartstick.util.UserStore;

public class SMSBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		UserModule currentUser = UserStore.getInstance().getCurrentUser(context);

		if (currentUser.getUserName() == null
				|| currentUser.getUserPhoneNumber() == null
				|| currentUser.getUserPhoneNumber().equals("")
				|| currentUser.getUserName().equals("")) {
			// skip
		} else {
			Bundle bundle = intent.getExtras();
			Object[] object = (Object[]) bundle.get("pdus");
			SmsMessage sms[] = new SmsMessage[object.length];

			for (int i = 0; i < object.length; i++) {
				sms[0] = SmsMessage.createFromPdu((byte[]) object[i]);

				if (sms[i].getOriginatingAddress().contains(
						currentUser.getUserPhoneNumber())) {
					String commandBody;
					try {
						commandBody = new String(sms[i].getDisplayMessageBody().getBytes(),"UTF-8");
						Toast.makeText(context, commandBody, Toast.LENGTH_LONG).show();
						this.abortBroadcast();
						Intent smsIntent = new Intent();
						smsIntent.setAction(Constant.ACTION_RECEIVED_MESSAGE);
						smsIntent.putExtra(Constant.SMS_BODY, commandBody);
						context.sendBroadcast(smsIntent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
