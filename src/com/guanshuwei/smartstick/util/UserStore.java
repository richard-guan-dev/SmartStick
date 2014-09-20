package com.guanshuwei.smartstick.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.instance.UserModule;

public class UserStore {
	
	
	private static UserStore Instance = null;
	private UserStore(){};
	
	public static UserStore getInstance(){
		if(UserStore.Instance == null){
			UserStore.Instance = new UserStore();
		}
		
		return UserStore.Instance;
	}
	
	public void saveUser(UserModule user, Context mContext){
		SharedPreferences sp = mContext.getSharedPreferences(Constant.USERS_LIST_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		try{
			editor.remove(Constant.USER_NAME);
			editor.remove(Constant.USER_PHONE_NUMBER);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		editor.putString(Constant.USER_NAME, user.getUserName());
		editor.putString(Constant.USER_PHONE_NUMBER, user.getUserPhoneNumber());
		editor.commit();
	}
	
	public UserModule getCurrentUser(Context context){
		UserModule user = new UserModule();
		SharedPreferences sp = context.getSharedPreferences(Constant.USERS_LIST_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		
		user.setUserName(sp.getString(Constant.USER_NAME, ""));
		user.setUserPhoneNumber(sp.getString(Constant.USER_PHONE_NUMBER, ""));
		
		return user;
	}
	
	public String getLastBattery(Context context){
		String battery;
		SharedPreferences sp = context.getSharedPreferences(Constant.USERS_LIST_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		battery = sp.getString(Constant.BATTERY, "-");
		return battery;
	}
	
	public String getLastTemprature(Context context){
		String temprature;
		SharedPreferences sp = context.getSharedPreferences(Constant.USERS_LIST_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		temprature = sp.getString(Constant.TEMPRATURE, "-");
		return temprature;
	}
	
	public void setBattery(String battery, Context context){
		SharedPreferences sp = context.getSharedPreferences(Constant.USERS_LIST_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		try{
			editor.remove(Constant.BATTERY);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		editor.putString(Constant.BATTERY, battery);
		editor.commit();
	}
	
	public void setTemprature(String temprature, Context context){
		SharedPreferences sp = context.getSharedPreferences(Constant.USERS_LIST_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		try{
			editor.remove(Constant.TEMPRATURE);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		editor.putString(Constant.TEMPRATURE, temprature);
		editor.commit();
	}
}
