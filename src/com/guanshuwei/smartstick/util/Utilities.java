package com.guanshuwei.smartstick.util;

public class Utilities {
	public static boolean isStringNullOrEmpty(String str){
		if(str == null || str.equals("")){
			return true;
		} else {
			return false;
		}
	}
}
