package com.jmhy.sdk.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.model.SdkParams;
import com.meituan.android.walle.ChannelInfo;
import com.meituan.android.walle.WalleChannelReader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

public class Utils {

	// UTF-8 encoding
	private static final String ENCODING_UTF8 = "UTF-8";
	private static final String LOGTAG = "Utils";

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		if (info != null) {
			return info.isAvailable();
		}

		return false;
	}

	public static String NetWork_Type(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		String type = info.getTypeName();
		return type;

	}

	/**
	 * 判断MOBILE网络是否可用
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isMobileDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isMobileDataEnable = false;

		isMobileDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

		return isMobileDataEnable;
	}

	/**
	 * 判断wifi 是否可用
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static boolean isWifiDataEnable(Context context) throws Exception {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isWifiDataEnable = false;
		isWifiDataEnable = connectivityManager.getNetworkInfo(
				ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		return isWifiDataEnable;
	}

	/**
	 * 
	 * Get UTF8 bytes from a string
	 * 
	 * 
	 * @param string
	 *            String
	 * @return UTF8 byte array, or null if failed to get UTF8 byte array
	 */
	public static byte[] getUTF8Bytes(String string) {
		if (string == null)
			return new byte[0];

		try {
			return string.getBytes(ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			/*
			 * If system doesn't support UTF-8, use another way
			 */
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeUTF(string);
				byte[] jdata = bos.toByteArray();
				bos.close();
				dos.close();
				byte[] buff = new byte[jdata.length - 2];
				System.arraycopy(jdata, 2, buff, 0, buff.length);
				return buff;
			} catch (IOException ex) {
				return new byte[0];
			}
		}
	}

	/**
	 * 
	 * Get string in UTF-8 encoding
	 * 
	 * 
	 * @param b
	 *            byte array
	 * @return string in utf-8 encoding, or empty if the byte array is not
	 *         encoded with UTF-8
	 */
	public static String getUTF8String(byte[] b) {
		if (b == null)
			return "";
		return getUTF8String(b, 0, b.length);
	}

	/**
	 * 
	 * Get string in UTF-8 encoding
	 * 
	 */
	public static String getUTF8String(byte[] b, int start, int length) {
		if (b == null) {
			return "";
		} else {
			try {
				return new String(b, start, length, ENCODING_UTF8);
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}
	}

	public static void getSeferencegame(Context context) {
		try {

			Seference seference = new Seference(context);

			if (TextUtils.isEmpty(AppConfig.Token)) {
				AppConfig.Token = seference.getPreferenceData("game", "token");
			}
			if (AppConfig.ONLIE_TIEM == 0) {
				AppConfig.ONLIE_TIEM = Long.parseLong(seference
						.getPreferenceData("game", "onlintiem"));
			}
			if(TextUtils.isEmpty(AppConfig.is_user_float_on)){
				AppConfig.is_user_float_on = seference.getPreferenceData("game", "userfloat");
			}
			if(TextUtils.isEmpty(AppConfig.is_service_float_on)){
				AppConfig.is_service_float_on = seference.getPreferenceData("game", "servicefloat");
			}
			if(TextUtils.isEmpty( AppConfig.add_global_script_url)){
				 AppConfig.add_global_script_url = seference.getPreferenceData("game", "scripturl");
			}
			// Log.i("kk","获取"+seference.getPreferenceData("game", "token"));
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static void saveSeferencegameuser(Context context,
			LoginMessage gameuser) {
		Seference seference = new Seference(context);
		/*
		 * seference.savePreferenceData("gameuser", "userurl",
		 * gameuser.getUserurl()); seference.savePreferenceData("gameuser",
		 * "orderurl", gameuser.getOrderurl());
		 * seference.savePreferenceData("gameuser", "liboaurl",
		 * gameuser.getLibaourl()); /*seference.savePreferenceData("gameuser",
		 * "serviceurl", gameuser.getServiceurl());
		 * seference.savePreferenceData("gameuser", "tuijianurl",
		 * gameuser.getTuijianurl());
		 * //Log.i("kk","保存"+gameuser.getTuijianurl());
		 */
	}

	public static void getSeferencegameuser(Context context) {
		Seference seference = new Seference(context);
		if (TextUtils.isEmpty(AppConfig.USERURL)) {
			AppConfig.USERURL = seference.getPreferenceData("gameuser",
					"userurl");
		}

		/*
		 * if (TextUtils.isEmpty(AppConfig.SERVICEURL)) { AppConfig.SERVICEURL =
		 * seference.getPreferenceData("gameuser", "serviceurl"); } if
		 * (TextUtils.isEmpty(AppConfig.TUIJIANURL)) { AppConfig.TUIJIANURL =
		 * seference.getPreferenceData("gameuser", "tuijianurl"); }
		 * Log.i("kk","获取"+seference.getPreferenceData("gameuser",
		 * "tuijianurl"));
		 */
	}

	public static SdkParams getSdkParams(Context context) {
		ChannelInfo channelInfo = WalleChannelReader.getChannelInfo(context.getApplicationContext());
		if (channelInfo != null) {
			Log.i(LOGTAG, "getSdkParams ChannelInfo");
			SdkParams params = new SdkParams();
			Map<String, String> extraInfo = channelInfo.getExtraInfo();

			if(!extraInfo.containsKey("agent")){
				return getPropertiesParams(context);
			}

			params.host = extraInfo.get("host");
			params.agent = extraInfo.get("agent");
			params.version = extraInfo.get("version");
			if(extraInfo.containsKey("appid")){
				params.appid = extraInfo.get("appid");
			}
			if(extraInfo.containsKey("appkey")){
				params.appkey = extraInfo.get("appkey");
			}

			return params;
		}else{
			return getPropertiesParams(context);
		}
	}

	private static SdkParams getPropertiesParams(Context context) {
		Log.i(LOGTAG, "getSdkParams getProperties");

		Properties properties = new Properties();
		SdkParams params = new SdkParams();
		try {
			properties.load(context.getAssets().open("jmhy.properties"));
			params.host = properties.getProperty("host", "");
			params.agent = properties.getProperty("agent", "");
			params.version = properties.getProperty("version", "");
			params.appid = properties.getProperty("appid", "");
			params.appkey = properties.getProperty("appkey", "");
			params.supportEnglish = properties.getProperty("supportEnglish", "0");

			/*if(!TextUtils.isEmpty(params.agent)) {
				params.agent = params.agent.trim();
			}

			if(!TextUtils.isEmpty(params.version)) {
				params.version = params.version.trim();
			}

			if(!TextUtils.isEmpty(params.appid)) {
				params.appid = params.appid.trim();
			}

			if(!TextUtils.isEmpty(params.appkey)) {
				params.appkey = params.appkey.trim();
			}*/

			return params;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	/*public static String getChannel(Context context) {
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;
		Log.e("kk", sourceDir);
		String ret = "";
		ZipFile zipfile = null;
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String entryName = entry.getName();
				if (entryName.indexOf("JMchannel") > 0) {
					ret = entryName;
					Log.e("kk", "==>" + ret);
					break;
				}
			}
		} catch (IOException e) {
			Log.e("kk", e.getMessage());
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String[] split = ret.split("_");
		if (split != null && split.length >= 2) {
			return ret.substring(split[0].length() + 1);
		} else {
			return "";
		}
	}

	public static String getVersion(Context context) {
		Properties properties = new Properties();
		String Version = "0.0";

		try {
			properties.load(context.getAssets().open("jmhy.properties"));
			Version = properties.getProperty("version");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Version = "0.0";
		}
		return Version;
	}*/

	/**
	 * check this obj
	 * 
	 * @param obj
	 * @param <T>
	 * @return
	 */
	public static <T> T checkNotNull(T obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		return obj;
	}

	public static String toBase64url(String url) {
		String murl = "";
		if (!TextUtils.isEmpty(url)) {
			murl = Base64.decode(url);
		}

		return murl;
	}

	/**
	 * 保存账号到sdcard
	 */
	public static void saveUserToSd(Context context) {
		List<HashMap<String, String>> contentList = new ArrayList<HashMap<String, String>>();
		Seference seference = new Seference(JiMiSDK.mContext);
		UserInfo creatFile = new UserInfo();
		contentList = seference.getContentList();
		String data = "";
		if (contentList == null) {
			creatFile.deletefile();
			return;
		} else {
			for (int i = 0; i < contentList.size(); i++) {
				String tempUser = contentList.get(i).get("account");
				String tempPwd = contentList.get(i).get("password");
				String tempUid = contentList.get(i).get("uid");
				data += tempUser + ":" + tempPwd + ":" + tempUid + "#";
			}
			creatFile.saveUserInfo("", "", "", data);
			// Log.i("kk","data"+data);
		}
	}

	public static String getVersion(Context context){
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {}

		return "";
	}

	public static boolean showEnglishUI(Context context){
		Locale locale = context.getResources().getConfiguration().locale;
		String country = locale.getCountry();
		return TextUtils.equals(AppConfig.supportEnglish, "1") && !TextUtils.equals("CN", country);
	}
}
