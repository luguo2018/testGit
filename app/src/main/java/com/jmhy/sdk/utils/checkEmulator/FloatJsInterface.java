package com.jmhy.sdk.utils.checkEmulator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.BaseFloatActivity;
import com.jmhy.sdk.sdk.Loginout;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.MediaUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.utils.changeAccountUtil;

/**
 * 创建人luow
 * 此类方法是给js调用的方法，涉及UI操作务必切换到UI线程
 */
public class FloatJsInterface {
	private Activity activity;
	
	private Seference mSeference;
	private BaseFloatActivity baseFloatActivity;
	public FloatJsInterface(Activity activity, BaseFloatActivity baseFloatActivity) {
		this.activity = activity;
		mSeference = new Seference(activity);
		this.baseFloatActivity = baseFloatActivity;
		
	}

	@JavascriptInterface
	public void JavaScriptSavepassword(String user, String pwd, String uid) {
		mSeference.saveAccount(user, pwd, uid);
		AppConfig.saveMap(user, pwd, uid);
		Utils.saveUserToSd(activity);
	}

	@JavascriptInterface
    public  void JavaScriptToJumpLogin(){
    	if(baseFloatActivity!=null){
			baseFloatActivity.removeContentView();
    	}
    }
	@JavascriptInterface
    public  void closeWebView(){
		if(baseFloatActivity!=null){
			baseFloatActivity.removeContentView();
    	}
	}
	
	@JavascriptInterface
    public  void JavaScriptback(){
    	if(baseFloatActivity!=null){
			baseFloatActivity.removeContentView();
    	}
    }

	/**
	 * 下载链接 弃用，暂用于保存图片
	 * @param url
	 */
	@JavascriptInterface
	public void JavascriptToDown(String url){
		MediaUtils.saveImage(activity, MediaUtils.getBitmap(url));
		DialogUtils.showTip(activity,AppConfig.getString(activity, "float_snapshot_save"));

//		Intent viewIntent = new Intent("android.intent.action.VIEW",
//				Uri.parse(url));
//		activity.startActivity(viewIntent);

//		changeAccountUtil.changeAccount(activity,baseFloatActivity,true,"g3397081254","g3397081254","123123123");

	}
	@JavascriptInterface
	public void JavaScriptToJumppassword() {
		/*Intent intent = new Intent();
		intent.putExtra("url", AppConfig.USERURL);
		intent.setClass(mContext,  ZYUserinfoActivity.class);
		mContext.startActivity(intent);*/
	}
	/**
	 * 切换账号
	 */
	@JavascriptInterface
	public void JavascriptSwitchUser() {
		//Log.i("kk","切换");

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JiMiSDK.userlistenerinfo.onLogout("logout");
				baseFloatActivity.removeContentView();
				AppConfig.isShow = false;
				FloatUtils.destroyFloat();

				AppConfig.skin9_is_switch=true;
				AppConfig.isswitch=false;
			}
		});
	}

	/**
	 * 修改游客账密   删除旧帐号  存状态isChangeGuestAccount  调用登录时登新号
	 */
	@JavascriptInterface
	public void JavascriptSetAccount(final String oldAccount, final String newAccount, final String newPassword) {
		changeAccountUtil.changeAccount(activity,baseFloatActivity,true,oldAccount,newAccount,newPassword);
	}

	/**
	 * 修改密码 改完旧的帐号列表中改账号token登录不上  先删除该帐号  存状态isChangeGuestAccount  调用登录时用新设置的账号密码
	 */
	@JavascriptInterface
	public void JavascriptChangePassword(final String account, final String password) {
		changeAccountUtil.changeAccount(activity,baseFloatActivity,true,account,account,password);
	}


	/**
	 * 跳转浏览器
	 */
	@JavascriptInterface
	public void loadUrlByBrowser(String url){
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(url));
		activity.startActivity(intent);
	}
	
	@JavascriptInterface
	public void payNotify(String result){
		//result=0 失败，1 成功
	}

	@JavascriptInterface
	public void closeAnimation(){
		Log.i("JsInterface", "closeAnimation");
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(activity instanceof JmUserinfoActivity){
					JmUserinfoActivity userinfoActivity = (JmUserinfoActivity)activity;
					userinfoActivity.hiddenLoading();
				}else if(activity instanceof JmCommunityActivity){
					JmCommunityActivity communityActivity = (JmCommunityActivity)activity;
					communityActivity.hiddenLoading();
				}
			}
		});
	}


	@JavascriptInterface
	public void forceLogout(final String str){
		Log.i("JsInterface", "forceLogout");
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JiMiSDK.forceLogout(str);

			}
		});
	}

	@JavascriptInterface
	public void openWebView(String url, int type){
		Intent intent = new Intent(activity, JmCommunityActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("type", type);
		activity.startActivity(intent);
	}

	/*@JavascriptInterface
	public void hiddenTip(final String type){
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(TextUtils.equals("account", type)){
					FloatUtils.hiddenTip(FloatView.INDEX_ACCOUNT);
				}else if(TextUtils.equals("gift", type)){
					FloatUtils.hiddenTip(FloatView.INDEX_GIFT);
				}
			}
		});
	}*/
}
