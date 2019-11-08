package com.jmhy.sdk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;

import com.jmhy.sdk.config.AppConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

public class DeviceInfo {

	protected static final String PREFS_FILE = "device_id.xml";
	protected static final String PREFS_DEVICE_ID = "device_id";

	// private Context context;
	private String serialId = "";// sim序列号
	private String imei = "";// imei 唯一的设备ID：GSM手机的 IMEI 和 CDMA手机的 MEID. 
	private String systemId = "";// ANDROID_ID
	private String systemInfo = "";// 系统信息【格式：系统版本@手机型号】
	private String uuidString = null;// 设备唯一码
	private String systemVer = ""; //手机版本号
	private String model = "";//手机型号
	private String deviceScreen = "";//手机分辨率
	private String appVersion = "";//应用版本号
	private String imsi = "";//手机sim卡的串号
	private String mac = "";
	private String packagename = "";//包名
	private String network = "";//网络制式
	private String operator = "";//运营商
	private String is_charged;//是否在充电


	// private static UUID uuid;

	public DeviceInfo(Context context) {

		// this.context = context;
		getData(context);


	}

	private void getData(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		if (VERSION.SDK_INT < VERSION_CODES.Q) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
				imei = telephonyManager.getDeviceId();
				serialId = telephonyManager.getSimSerialNumber();
				imsi = telephonyManager.getSubscriberId();
			}


			operator = IntenetUtil.getOperator(context);
		}

		// systemInfo
		systemVer = VERSION.RELEASE;
		model = "";
		try {
			model = URLEncoder.encode(Build.MODEL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		systemInfo = systemVer + "@" + model;

		// systemId
		systemId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

		if (uuidString == null) {
			final SharedPreferences prefs = context.getSharedPreferences(
					PREFS_FILE, 0);
			uuidString = prefs.getString(PREFS_DEVICE_ID, null);

			if (uuidString == null) {

				// Use the Android ID unless it's broken, in which case
				// fallback on deviceId,
				// unless it's not available, then fallback on a random
				// number which we store
				// to a prefs file
				try {
					if (!"9774d56d682e549c".equals(systemId)) {
						uuidString = UUID.nameUUIDFromBytes(
								systemId.getBytes("utf8")).toString();
					} else {

						uuidString = imei != null ? UUID.nameUUIDFromBytes(
								imei.getBytes("utf8")).toString() : UUID
								.randomUUID().toString();
					}
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}

			}

			// Write the value out to the prefs file
			prefs.edit().putString(PREFS_DEVICE_ID, uuidString).apply();

		}
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		if (context instanceof Activity) {
			Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
			if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
				display.getRealMetrics(mDisplayMetrics);
			}else{
				display.getMetrics(mDisplayMetrics);
			}
		}
		int W = mDisplayMetrics.widthPixels;
		int H = mDisplayMetrics.heightPixels;
		deviceScreen=W+"*"+H;
		   try {
		      PackageManager manager = context.getPackageManager();
		        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
		        appVersion = AppConfig.version;//info.versionName;
		        packagename = info.packageName;
		    } catch (Exception e) {
		        e.printStackTrace();
		        appVersion= "";
		    }

	        network =IntenetUtil.getNetworkState(context);

	        is_charged = IntenetUtil.is_charged(context);
	       
	}
	public String getImsi(){
		return imsi;
	}
	
	public String getMac() {
		return mac;
	}

	public String getSerialId() {
		return serialId;
	}

	public String getImei() {
		return imei;
	}

	public String getSystemId() {
		return systemId;
	}

	public String getUuid() {
		return uuidString;
	}

	public String getSystemInfo() {
		return systemInfo;
	}

	public String getSystemVer() {
		return systemVer;
	}

	public void setSystemVer(String systemVer) {
		this.systemVer = systemVer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDeviceScreen() {
		return deviceScreen;
	}

	public void setDeviceScreen(String deviceScreen) {
		this.deviceScreen = deviceScreen;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	  private static String formatIpAddress(int ipAdress) {      
	        
	         return (ipAdress & 0xFF ) + "." +      
	        ((ipAdress >> 8 ) & 0xFF) + "." +      
	        ((ipAdress >> 16 ) & 0xFF) + "." +      
	        ( ipAdress >> 24 & 0xFF) ;  
	     }

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getIs_charged() {
		return is_charged;
	}
	public void setIs_charged(String is_charged) {
		this.is_charged = is_charged;
	}




	public static String getPrefsFile() {
		return PREFS_FILE;
	}




	public static String getPrefsDeviceId() {
		return PREFS_DEVICE_ID;
	}




	public String getUuidString() {
		return uuidString;
	}


/**
 * 获取手机厂商
 * @return
 */

	public String getDevicebrand() {
		return  android.os.Build.MANUFACTURER;
	}  
	  
}
