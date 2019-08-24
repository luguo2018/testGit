package com.jmhy.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SeferenceGame {
   private static Seference seference;
	private Context mcontext;
	private SeferenceGame(Context context) {
		this.mcontext = context;
	}
	public static Seference getInstance(Context context){
		if(seference==null){
			seference=new Seference(context);
		}
		return seference;
	}

	public void savePreferenceData(String filename, String key, String value) {
		SharedPreferences.Editor sharedata = mcontext.getSharedPreferences(
				filename, 0).edit();
		sharedata.putString(key, value);
		sharedata.commit();
	}

	public String getPreferenceData(String filename, String key) {
		String temp = "";
		SharedPreferences sharedata = mcontext
				.getSharedPreferences(filename, 0);
		temp = sharedata.getString(key, " ");
		return temp;
	}

	
}
