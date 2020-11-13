package com.jmhy.sdk.utils.checkEmulator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.jmhy.sdk.activity.JmBaseActivity;
import com.jmhy.sdk.activity.JmCommunityActivity;
import com.jmhy.sdk.activity.JmUserinfoActivity;
import com.jmhy.sdk.ad.JmAdSdk;
import com.jmhy.sdk.ad.callback.AdListener;
import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.BaseFloatActivity;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.FloatUtils;
import com.jmhy.sdk.utils.MediaUtils;
import com.jmhy.sdk.utils.PackageUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.Utils;
import com.jmhy.sdk.utils.changeAccountUtil;
import com.jmhy.sdk.view.AdTipDialog;

/**
 * 创建人luow
 * 此类方法是给js调用的方法，涉及UI操作务必切换到UI线程
 */
public class FloatJsInterface {
	private Activity activity;
	
	private Seference mSeference;
	private BaseFloatActivity baseFloatActivity;
	private WebAdListener adListener;
	public FloatJsInterface(Activity activity, BaseFloatActivity baseFloatActivity, WebAdListener webAdListener) {
		this.activity = activity;
		mSeference = new Seference(activity);
		this.baseFloatActivity = baseFloatActivity;
		this.adListener=webAdListener;

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
		Log.i("jimisdk","关闭网页");
//		adListener.close();
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

	}

	/**
	 * 保存图片，url需过base64
	 * @param url
	 */
	@JavascriptInterface
	public void JavascriptSaveBase64Image(String url){
		try {
			byte[] decode = Base64.decode(url.split(",")[1], Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
			MediaUtils.saveImage(activity, bitmap);
			DialogUtils.showTip(activity,AppConfig.getString(activity, "float_snapshot_save"));
		}catch (Exception e){
			e.printStackTrace();
		}
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

				if (JmBaseActivity.getNoticeActivity()!=null){
					JmBaseActivity.getNoticeActivity().removeContentView();
				};
			}
		});

//		String appId="5112188";
//		String adId="945546866";
//		String data="警告！警告！\n 警告！警告！\n 警告！警告！\n 警告！警告！\n 警告！警告！\n 世界即将毁灭！ \n 请观看广告拯救世界";
//		loadAd(adId,data);
	}

	/**
	 * 修改游客账密   删除旧帐号  存状态isChangeGuestAccount  调用登录时登新号
	 */
	@JavascriptInterface
	public void JavascriptSetAccount(final String oldAccount, final String newAccount, final String newPassword) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				changeAccountUtil.changeAccount(activity,baseFloatActivity,true,oldAccount,newAccount,newPassword);
			}
		});
	}

	/**
	 * 修改密码 改完旧的帐号列表中改账号token登录不上  先删除该帐号  存状态isChangeGuestAccount  调用登录时用新设置的账号密码
	 */
	@JavascriptInterface
	public void JavascriptChangePassword(final String account, final String password) {
		Log.i("jimi","修改密码："+account+"---"+password);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				changeAccountUtil.changeAccount(activity,baseFloatActivity,true,account,account,password);
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

	/**
	 * 跳转微信、QQ、微博
	 */
	@JavascriptInterface
	public void turnToApp(String packageName){
		if (PackageUtils.isInstall(activity,packageName)){
			Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
			activity.startActivity(intent);
		}else{
			String tip="该软件";
			if (packageName.equals("com.sina.weibo")){
				tip="新浪微博";
			}else if (packageName.equals("com.tencent.mobileqq")){
				tip="腾讯qq";
			}else if (packageName.equals("com.tencent.mm")){
				tip="微信";
			}
			DialogUtils.showTip(activity,tip+"未安装，请安装后重试");
		}
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

	/**
	 * load广告
	 */
	@JavascriptInterface
	public void loadAd(final String adId, final String data, final String gameId){
		final String adAppId=AppConfig.ad_app_id;
		if (adAppId.equals("")){
			adListener.error("apk not adAppId");
			return;
		}

		Log.i("jimi","广告appId"+adAppId+"广告id："+adId+"gameId"+gameId);

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				if (data!=null&&!data.equals("")){
					AdTipDialog exitdialog = new AdTipDialog(activity, AppConfig.resourceId(activity, "jm_MyDialog", "style"),data, new AdTipDialog.AdTipDialogListener() {
						@Override
						public void onStratAd() {
							JmAdSdk.init(activity.getApplicationContext(), adAppId);
							JmAdSdk.loadAd(activity, adAppId, adId,AppConfig.openid, gameId,new AdListener() {
								@Override
								public void Success(Object var1) {
									Log.i("jimi","加载广告回调开始");
									adListener.end("end");
								}

								@Override
								public void fail(String var1) {
									Log.i("jimi","加载广告回调异常"+var1);
									adListener.error(var1);
								}
							});
						}
					});
					exitdialog.show();
				}
			}
		});



	}

	public interface WebAdListener {
		void end(String var1);

		void error(String var1);

		void close();
	}

}
