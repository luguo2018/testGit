package com.jmhy.sdk.config;

import android.content.Context;

import com.jmhy.sdk.bean.PayData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppConfig {

	public static boolean isDebugMode = true;
	public static boolean save_guest_end = true;

	public final static int INIT = 101;
	public final static int INIT_SUCCESS = 102;
	public final static int FLAG_PUSH = 103;
	public final static int FLAG_FAIL = 104;
	public final static int CODE_BUTTON = 105;
	public final static int FLAG_SHOW_POPWINDOW = 106;
	public final static int MOBILELOGIN_SUCCESS = 107;
	public final static int AUTO_LOGIN_SUCCESS = 108;
	public final static int GUEST_lOGIN_SUCCESS = 109;
	public final static int GET_PAY = 110;
	public final static int REGISTER_SUCCESS = 111;
	public final static int LOGINOUT_SUCCESS = 112;
	public final static int LOGIN_SUCCESS = 113;
	public final static int CODE_SUCCESS = 114;
	public final static int PAY_SUCCESS = 115;
	public final static int CODE_FAIL = 116;
	public final static int ONEKEY_lOGIN_SUCCESS = 117;

	public static boolean ismobillg = true;
	public static boolean isswitch = true;

	public static boolean isChangeGuestAccount = false;//修改游客号  删除旧帐号  拉登录时登新号
	public static String change_old_account = "";
	public static String change_new_account = "";
	public static String change_new_password = "";

	public static int appId = 0;
	public static String cache_orderId = "";
	public static String appKey = "";
	public static String agent = "";
	public static String version = "";
    public static String sdk_version ="1.4.0";
	public static String supportEnglish ="0";
	public static int skin = 1;
	public static int userType = 0;

	public static boolean isShow = false;
	public static boolean isgoto = true;
	public static boolean isRegister = false;
	public static boolean skin9_is_switch = false;
	public static boolean skin9_switch_showDelete = false;
	public static boolean skin9_show_setAccount = false;

	public static String openid = ""; // 用户id
	public static String userName = "";// 用户名
	public static String time = "";
	public static String USERURL = "";// 用户信息地址
	public static String GIFT = "";
	public static String KEFU = "";
	public static String float_url_home_center = "";
	public static String FPWD = "";// 找回密码
	public static String USERAGREEMENTURL = "";// 用户协议
	public static String add_global_script_url = "";
	public static long ONLIE_TIEM = 5000;
	public static String is_user_float_on = "";// 浮点用户浮点
	public static String is_service_float_on = "";// 客服
	public static String is_visitor_on = "1";
	public static String is_visitor_on_phone = "1";
	public static String is_auto_login_on = "1";
	public static String is_log_on = "1";
	public static String is_reg_login_on = "1";
	public static String switch_login = "1";
	public static String is_sdk_float_on = "0";
	public static JSONObject sdkList;
	public static String h5GameUrl;
	public static String oneKeyLogin_SecretKey;
	public static PayData payData;
	public static String loginH5GameUrl;

	public static boolean showAccountTip;
	public static boolean showGiftTip;
	public static boolean showKefuTip;
	public static String phone_number = "";
	public static boolean showOneKeyLogin = false;

	public static String Token = "";
	public static String oneKey_access_token = "";
	public static Map<String, String> tempMap = new HashMap<>();// 临时保存未激活的注册账号和修改密码页面的用户信息
	public static Map<String, String> loginMap = new HashMap<>();// 临时保存文件系统中的user0信息
	public static Map<String, String> initMap = new HashMap<>();// 临时保存初始化信�?
	public static Map<String, String> markMap = new LinkedHashMap<>();// 临时保存已调用接口
	public static List<String> iphoneidList = new ArrayList<>();
	public static String oridId;

	public static void saveMap(String user, String pwd, String uid) {
		loginMap.put("user", user);
		loginMap.put("pwd", pwd);
		loginMap.put("uid", uid);
	}

	/**
	 * 清除数据
	 */
	public static void clear() {
		tempMap.clear();
	}

	public static void clearCache() {
		tempMap.clear();
		loginMap.clear();
		initMap.clear();

	}

	public static List<String> intersect(String[] a, String[] b) {
		List<String> list = new ArrayList<String>(Arrays.asList(b));
		list.retainAll(Arrays.asList(a));
		return list;
	}

	public static int resourceId(Context context, String name, String type) {
		return context.getResources().getIdentifier(name, type,
				context.getPackageName());
	}

	public static String getString(Context context, String name) {
		return context.getResources().getString(
				context.getResources().getIdentifier(name, "string",
						context.getPackageName()));
	}

}
