package com.jmhy.sdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.sdk.Loginout;


public class JsInterface {
	private Activity activity;

	private Seference mSeference;

	public JsInterface(Activity activity) {
		this.activity = activity;
		mSeference = new Seference(activity);
	}

	@JavascriptInterface
	public void JavaScriptSavepassword(String user, String pwd, String uid) {
		mSeference.saveAccount(user, pwd, uid);
		AppConfig.saveMap(user, pwd, uid);
		Utils.saveUserToSd(activity);
	}

	@JavascriptInterface
    public  void JavaScriptToJumpLogin(){
    	if(activity!=null){
			activity.finish();
    	}
    }
	@JavascriptInterface
    public  void closeWebView(){
		if(activity!=null){
			activity.finish();
    	}
	}

	@JavascriptInterface
    public  void JavaScriptback(){
    	if(activity!=null){
			activity.finish();
    	}
    }

	/**
	 * 下载链接
	 * @param url
	 */
	@JavascriptInterface
	public void JavascriptToDown(String url){
		MediaUtils.saveImage(activity, MediaUtils.getBitmap(url));
		DialogUtils.showTip(activity,AppConfig.getString(activity, "float_snapshot_save"));

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
				Loginout.getInstatnce(activity);
				JiMiSDK.userlistenerinfo.onLogout("logout");
				activity.finish();
				AppConfig.isShow = false;
				AppConfig.skin9_is_switch=true;
				AppConfig.isswitch=false;
				FloatUtils.destroyFloat();
			}
		});
	}
	/**
	 * 修改游客账密   删除旧帐号  存状态isChangeGuestAccount  调用登录时登新号
	 */
	@JavascriptInterface
	public void JavascriptSetAccount(final String oldAccount, final String newAccount, final String newPassword) {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				changeAccountUtil.changeAccount(activity,null,true,oldAccount,newAccount,newPassword);
			}
		});
	}

	/**
	 * 修改密码 改完旧的帐号列表中改账号token登录不上  先删除该帐号  存状态isChangeGuestAccount  调用登录时用新设置的账号密码
	 */
	@JavascriptInterface
	public void JavascriptChangePassword(final String account, final String password) {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				changeAccountUtil.changeAccount(activity,null,true,account,account,password);
			}
		});
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
