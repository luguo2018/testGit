package com.jmhy.sdk.http;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.BaseResponse;
import com.jmhy.sdk.model.Guest;
import com.jmhy.sdk.model.InitMsg;
import com.jmhy.sdk.model.LoginMessage;
import com.jmhy.sdk.model.MobileUser;
import com.jmhy.sdk.model.Msg;
import com.jmhy.sdk.model.OnlineMessage;
import com.jmhy.sdk.model.PayData;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.utils.Utils;

public class JSONParse {

	private static final String LOGTAG = "JSONParse";

	/**
	 * 初始化接口
	 * 
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static InitMsg parseInitMsg(String data) throws JSONException {

		InitMsg result = new InitMsg();
		JSONObject jsonObject = new JSONObject(data);

		String r = jsonObject.optString("code");

		result.setCode(r);
		result.setMessage(jsonObject.getString("message"));
		List<String> codelist = new ArrayList<String>();
		// result为true时才解析data数据
		if (r.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");
			result.setAccess_token(dataObject.optString("access_token"));
			result.setAuto_login(dataObject.optString("is_auto_login_on"));
			result.setExpired(dataObject.optString("expired"));
			result.setGift_float(dataObject.optString("is_gift_float_on"));
			result.setLogout_float(dataObject.optString("is_logout_float_on"));
			result.setPay_float(dataObject.optString("is_pay_float_on"));
			result.setRecommend_float(dataObject
					.optString("is_recommend_float_on"));
			result.setReg_login(dataObject.optString("is_reg_login_on"));
			result.setSdk_float(dataObject.optString("is_sdk_float_on"));
			result.setService_float(dataObject.optString("is_service_float_on"));
			result.setUser_float(dataObject.optString("is_user_float_on"));
			result.setVisitor(dataObject.optString("is_visitor_on"));
			result.setLog_on(dataObject.optString("is_log_on"));
			result.setIsvisitoronphone(dataObject.optString("is_visitor_on_phone"));
			result.setSwitch_login(dataObject.optString("switch_login"));
			JSONArray jsonArray = dataObject.optJSONArray("code_area_list");
			for (int i = 0; i < jsonArray.length(); i++) {
				String value = (String) jsonArray.opt(i);
				codelist.add(value);
				// Log.i("kk",value);
			}

			result.setCode_area_list(codelist);
			result.setShowurlafterint(dataObject
					.optString("show_url_after_init"));
			result.setRegflowtype(dataObject.optString("reg_flow_type"));
			result.setUseragreementurl(dataObject
					.optString("user_agreement_url"));
			result.setCustomerserviceurl(dataObject
					.optString("customer_service_url"));
			result.setOnlinereportinterval(dataObject
					.optString("online_report_interval"));
			result.setForgetpasswordurl(dataObject.optString("forget_password_url"));
			result.setSkin(dataObject.optInt("skin"));
			result.setH5_game_url(dataObject.optString("h5_game_url"));

			JSONObject sdkList = dataObject.optJSONObject("channel_sdk_list");
			result.setChannel_sdk_list(sdkList);
		
		result.setAddglobalscripturl(dataObject.optString("add_global_script_url"));
		}
		return result;
	}

	/**
	 * 获取验证码
	 * 
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static Msg parseRequestSMS(String data) throws JSONException {

		Msg msg = new Msg();
		JSONObject jsonObject = new JSONObject(data);
		String r = jsonObject.optString("code");
		msg.setCode(r);
		msg.setMessage(jsonObject.optString("message"));
		return msg;
	}

	/**
	 * 手机登录
	 * 
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static MobileUser parseMobilelogin(String data) throws JSONException {
		MobileUser mobileUser = new MobileUser();
		JSONObject jsonObject = new JSONObject(data);
		String code = jsonObject.optString("code");
		mobileUser.setCode(code);
		mobileUser.setMessage(jsonObject.optString("message"));
		if (code.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");
			mobileUser.setMoblie_code(dataObject.optString("code"));
			mobileUser.setCode_area(dataObject.optString("code_area"));
			mobileUser
					.setPhone_register(dataObject.optString("phone_register"));
			mobileUser.setUnname(dataObject.optString("uname"));
			mobileUser.setMoblie(dataObject.optString("mobile"));
			mobileUser.setFloat_url_user_center(dataObject
					.optString("float_url_user_center"));
			mobileUser.setGame_token(dataObject.optString("game_token"));
			mobileUser.setLogin_token(dataObject.optString("login_token"));
			mobileUser.setOpenid(dataObject.optString("openid"));
			mobileUser.setShow_url_after_login(dataObject
					.optString("show_url_after_login"));
			mobileUser.setFloat_red_recommend(dataObject.optInt("float_red_recommend"));
			mobileUser.setFloat_url_gift_center(dataObject.optString("float_url_gift_center"));

			String url = dataObject.optString("h5_game_url");
			if(!TextUtils.isEmpty(url)){
				AppConfig.loginH5GameUrl = Utils.toBase64url(url);
			}
			AppConfig.openid = mobileUser.getOpenid();
			AppConfig.USERURL = Utils.toBase64url(mobileUser.getFloat_url_user_center());
			AppConfig.GIFT = Utils.toBase64url(mobileUser.getFloat_url_gift_center());
			if(mobileUser.getFloat_red_recommend() == 1){
				AppConfig.showAccountTip = true;
				AppConfig.showGiftTip = false;
			}else if(mobileUser.getFloat_red_recommend() == 2){
				AppConfig.showAccountTip = false;
				AppConfig.showGiftTip = true;
			}else if(mobileUser.getFloat_red_recommend() == 3){
				AppConfig.showAccountTip = true;
				AppConfig.showGiftTip = true;
			}
		}

		return mobileUser;
	}

	/**
	 * 用户注册
	 * 
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static Registermsg parseuserRegister(String data) throws JSONException {
		Registermsg registermsg = new Registermsg();
		JSONObject jsonObject = new JSONObject(data);
		String code = jsonObject.optString("code");
		registermsg.setCode(code);
		registermsg.setMessage(jsonObject.optString("message"));
		if (code.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");
			registermsg.setAuto_login_token(dataObject
					.optString("auto_login_token"));
			registermsg.setUname(dataObject.optString("uname"));
		}
		return registermsg;
	}

	/**
	 * 自动登录
	 * 
	 * @param data
	 * @return
	 * @throws JSONException
	 */
	public static LoginMessage parseAutologin(String data) throws JSONException {
		LoginMessage loginMessage = new LoginMessage();
		JSONObject jsonObject = new JSONObject(data);
		String code = jsonObject.optString("code");
		loginMessage.setCode(code);
		loginMessage.setMessage(jsonObject.optString("message"));
		if (code.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");
			loginMessage.setGame_token(dataObject.optString("game_token"));
			loginMessage.setUname(dataObject.optString("uname"));
			loginMessage.setLogin_token(dataObject.optString("login_token"));
			loginMessage.setOpenid(dataObject.optString("openid"));
			loginMessage.setShow_url_after_login(dataObject.optString("show_url_after_login"));
			loginMessage.setFloat_url_user_center(dataObject.optString("float_url_user_center"));
            loginMessage.setFloat_red_recommend(dataObject.optInt("float_red_recommend"));
            loginMessage.setFloat_url_gift_center(dataObject.optString("float_url_gift_center"));

			String url = dataObject.optString("h5_game_url");
			if(!TextUtils.isEmpty(url)){
				AppConfig.loginH5GameUrl = Utils.toBase64url(url);
			}
            AppConfig.openid = loginMessage.getOpenid();
            AppConfig.USERURL = Utils.toBase64url(loginMessage.getFloat_url_user_center());
            AppConfig.GIFT = Utils.toBase64url(loginMessage.getFloat_url_gift_center());
            if(loginMessage.getFloat_red_recommend() == 1){
                AppConfig.showAccountTip = true;
                AppConfig.showGiftTip = false;
            }else if(loginMessage.getFloat_red_recommend() == 2){
                AppConfig.showAccountTip = false;
                AppConfig.showGiftTip = true;
            }else if(loginMessage.getFloat_red_recommend() == 3){
                AppConfig.showAccountTip = true;
                AppConfig.showGiftTip = true;
            }
		}
		return loginMessage;
	}

	public static Guest parseGuestlogin(String data) throws JSONException {
		Guest mGuest = new Guest();
		JSONObject jsonObject = new JSONObject(data);
		String code = jsonObject.optString("code");
		mGuest.setCode(code);
		mGuest.setMessage(jsonObject.optString("message"));
		if (code.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");
			mGuest.setGame_token(dataObject.optString("game_token"));
			mGuest.setUname(dataObject.optString("uname"));
			mGuest.setLogin_token(dataObject.optString("login_token"));
			mGuest.setOpenid(dataObject.optString("openid"));
			mGuest.setUpass(dataObject.optString("upass"));
			mGuest.setShow_url_after_login(dataObject.optString("show_url_after_login"));
			mGuest.setFloat_url_user_center(dataObject.optString("float_url_user_center"));
            mGuest.setFloat_red_recommend(dataObject.optInt("float_red_recommend"));
            mGuest.setFloat_url_gift_center(dataObject.optString("float_url_gift_center"));

			String url = dataObject.optString("h5_game_url");
			if(!TextUtils.isEmpty(url)){
				AppConfig.loginH5GameUrl = Utils.toBase64url(url);
			}
            AppConfig.openid = mGuest.getOpenid();
            AppConfig.USERURL = Utils.toBase64url(mGuest.getFloat_url_user_center());
            AppConfig.GIFT = Utils.toBase64url(mGuest.getFloat_url_gift_center());
            if(mGuest.getFloat_red_recommend() == 1){
                AppConfig.showAccountTip = true;
                AppConfig.showGiftTip = false;
            }else if(mGuest.getFloat_red_recommend() == 2){
                AppConfig.showAccountTip = false;
                AppConfig.showGiftTip = true;
            }else if(mGuest.getFloat_red_recommend() == 3){
                AppConfig.showAccountTip = true;
                AppConfig.showGiftTip = true;
            }
		}
		return mGuest;
	}
/**
 * 订单创建
 * @param data
 * @return
 * @throws JSONException
 */
	public static PayData parseCreate(String data) throws JSONException {
	    PayData payData = new PayData();
		JSONObject jsonObject = new JSONObject(data);
		String code = jsonObject.optString("code");
		payData.setCode(code);
		payData.setMessage(jsonObject.optString("message"));
		if (code.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");
			payData.setOcontent(dataObject.optString("o_content"));
			payData.setOtype(dataObject.optString("o_type"));
			payData.setOrderid(dataObject.optString("order_id"));
			payData.setRealnameneedeed(dataObject.optString("real_name_needeed"));
			payData.setCallbackUrl(dataObject.optString("callback_url"));
			long date = dataObject.optLong("user_reg_date");
			payData.setUser_reg_date(String.valueOf(date));
			//payData.setUser_reg_date(dataObject.optString("user_reg_date"));

			AppConfig.payData = payData;
		}

		return payData;
	}

	public static MobileUser parseThirdPlatformLogin(String data) throws JSONException {
		MobileUser user = new MobileUser();
		JSONObject jsonObject = new JSONObject(data);
		String code = jsonObject.optString("code");
		String message = jsonObject.optString("message");
		user.setCode(code);
		user.setMessage(message);
		if (code.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");

			user.setOpenid(dataObject.optString("openid"));
			user.setUnname(dataObject.optString("uname"));
			user.setLogin_token(dataObject.optString("login_token"));
			user.setGame_token(dataObject.optString("game_token"));
			user.setShow_url_after_login(dataObject.optString("show_url_after_login"));
			user.setFloat_url_user_center(dataObject.optString("float_url_user_center"));
			user.setChannel_user_info(dataObject.optString("channel_user_info"));
            user.setFloat_red_recommend(dataObject.optInt("float_red_recommend"));
            user.setFloat_url_gift_center(dataObject.optString("float_url_gift_center"));

            String url = dataObject.optString("h5_game_url");
            if(!TextUtils.isEmpty(url)){
				AppConfig.loginH5GameUrl = Utils.toBase64url(url);
			}
            AppConfig.openid = user.getOpenid();
            AppConfig.USERURL = Utils.toBase64url(user.getFloat_url_user_center());
            AppConfig.GIFT = Utils.toBase64url(user.getFloat_url_gift_center());
            if(user.getFloat_red_recommend() == 1){
                AppConfig.showAccountTip = true;
                AppConfig.showGiftTip = false;
            }else if(user.getFloat_red_recommend() == 2){
                AppConfig.showAccountTip = false;
                AppConfig.showGiftTip = true;
            }else if(user.getFloat_red_recommend() == 3){
                AppConfig.showAccountTip = true;
                AppConfig.showGiftTip = true;
            }
		}
		return user;
	}

	public static BaseResponse parseRechargeNotify(String data) throws JSONException {
		BaseResponse response = new BaseResponse();
		JSONObject jsonObject = new JSONObject(data);
		response.code = jsonObject.optString("code");
		response.message = jsonObject.optString("message");
		return response;
	}

	public static BaseResponse parseBaseResponse(String data) throws JSONException {
		BaseResponse response = new BaseResponse();
		JSONObject jsonObject = new JSONObject(data);
		response.code = jsonObject.optString("code");
		response.message = jsonObject.optString("message");
		return response;
	}

    public static OnlineMessage parseOnlineNotify(String data) throws JSONException {
		//测试用例
		//data = "{\"code\":0,\"data\":[],\"message\":\"成功\"}";
		//data = "{\"code\":0,\"data\":{\"showUrl\":\"aHR0cDovL3Rlc3QuMTcyam0uY29tL2dvL3RvP2FjY2Vzc190b2tlbj0xMDAwMDFfMTU3NjA1MDkyMmViMmIwZmRkYzVhNWM0Y2FmZmI4YjA4NmEzZTk2MjM5ZjUzMTExZDUxMGQ0OGUzMSZyZWRpcmVjdF91cmw9aHR0cHMlM0ElMkYlMkZ0ZXN0LjE3MmptLmNvbSUyRmNvbW11bml0eSUyRmRpc3QlMkZzZXRfYXV0aGVudGljYXRpb24uaHRtbCUzRmZyb21HYW1lJTNEMSUyNmNsb3NlJTNEMTA=\",\"showMsg\":\"哈哈哈哈哈\",\"exit\":5},\"message\":\"成功\"}";
		OnlineMessage msg = new OnlineMessage();
		JSONObject jsonObject = new JSONObject(data);
		String code = jsonObject.optString("code");
		msg.setCode(code);
		msg.setMessage(jsonObject.optString("message"));
		if (code.equals("0")) {
			JSONObject dataObject = jsonObject.optJSONObject("data");
			if(dataObject == null){
				return msg;
			}
			msg.setShowUrl(dataObject.optString("showUrl",""));
			msg.setShowMsg(dataObject.optString("showMsg",""));
			msg.setExit(dataObject.optInt("exit"));
		}
		return msg;

    }
}
