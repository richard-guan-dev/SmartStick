package com.guanshuwei.smartstick.instance;

public class HistoryModule {
	public enum LogType {
		ALERT, LOCATE;

		@Override
		public String toString() {
			if (this.equals(ALERT)) {
				return "ALERT";
			} else {
				return "LOCATE";
			}
		}
	};

	private long mID;
	private String mUserName;
	private String mUserPhoneNumber;
	private LogType mLogType;
	private double mLongitude;
	private double mLatitude;
	private String mDate;

	public String getmDate() {
		return mDate;
	}

	public void setmDate(String mDate) {
		this.mDate = mDate;
	}

	public long getID() {
		return mID;
	}

	public void setID(long mID) {
		this.mID = mID;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getUserPhoneNumber() {
		return mUserPhoneNumber;
	}

	public void setUserPhoneNumber(String mUserPhoneNumber) {
		this.mUserPhoneNumber = mUserPhoneNumber;
	}

	public LogType getLogType() {
		return mLogType;
	}

	public void setLogType(LogType mLogType) {
		this.mLogType = mLogType;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

}
