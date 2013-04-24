package br.com.redu.redumobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PinCodeHelper {

	private static final String REDU_PIN_CODE = "REDU_PIN_CODE";
	
	public static boolean hasPinCode(Context context) {
		return getPrefs(context).contains(REDU_PIN_CODE);
	}
	
	public static String getPinCode(Context context) {
		return getPrefs(context).getString(REDU_PIN_CODE, null);
	}
	
	public static boolean setPinCode(Context context, String pinCode) {
		return getPrefs(context).edit().putString(REDU_PIN_CODE, pinCode).commit();
	}
	
	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
