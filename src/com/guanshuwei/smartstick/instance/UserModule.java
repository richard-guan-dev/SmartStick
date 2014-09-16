package com.guanshuwei.smartstick.instance;

public class UserModule {
	private String mUserName;
	private String mUserPhoneNumber;
	private long   mID;

	public String getUserPhoneNumber() {
		return mUserPhoneNumber;
	}

	public void setUserPhoneNumber(String mUserPhoneNumber) {
		this.mUserPhoneNumber = mUserPhoneNumber;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public long getID() {
		return mID;
	}

	public void setID(long mID) {
		this.mID = mID;
	}
}
