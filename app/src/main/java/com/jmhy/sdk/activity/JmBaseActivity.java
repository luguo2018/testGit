package com.jmhy.sdk.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jmhy.sdk.common.JiMiSDK;
import com.jmhy.sdk.config.AppConfig;
import com.jmhy.sdk.model.LoginMessageinfo;
import com.jmhy.sdk.model.Registermsg;
import com.jmhy.sdk.utils.DialogUtils;
import com.jmhy.sdk.utils.Seference;
import com.jmhy.sdk.utils.UserInfo;
import com.jmhy.sdk.utils.Utils;

public class JmBaseActivity extends Activity {

	public String appKey = "", serverId = "", verId = "";
	public int appId;
	public Seference mSeference;
	public UserInfo mUserinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		Utils.getSeferencegame(this);

		mSeference = new Seference(this);
		mUserinfo = new UserInfo();
	}

	private void initData() {
	
	}
	/**
	 * 接口返回数据处理
	 */
	public void sendData(int num, Object data, Handler callback) {
		Message msg = callback.obtainMessage();
		msg.what = num;
		msg.obj = data;
		msg.sendToTarget();
	}

	/**
	 * toast 提示信息
	 * 
	 * @param msg
	 */
	public void showMsg(String msg) {
		DialogUtils.showTip(this, msg);
	}
	public void showUserMsg(String msg) {
		String s = "";
		if (msg.length() > 8) {
			s = msg.substring(0, 8) + "...";
		}else{
			s=msg;
		}
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		View layout = View.inflate(this,
				AppConfig.resourceId(this, "jmtoast", "layout"), null);
		TextView user = (TextView) layout.findViewById(AppConfig.resourceId(this, "tvuser", "id"));
		// 设置toast文本，把设置好的布局传进来
		user.setText(s+"");
		toast.setView(layout);
		toast.setGravity(Gravity.TOP, 0, 50);
		toast.show();
	}
	/**
	 * Intent页面跳转
	 */
	public void changePage(Activity activity, Class<?> cls) {
		Intent intent = new Intent(activity, cls);
		Bundle bundle = new Bundle();
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	/**
	 * 显示登录成功
	 * 
	 * @param userName
	 * @param xOffset
	 * @param yOffset
	 * @param duration
	 */
	public void showLoginFinish(String userName, int xOffset, int yOffset,
			int duration) {
	}

	/**
	 * 回调信息
	 */
	public void callBack(String result, String msg, String timestamp,
			String uid, String username, String sign,String token) {
	}


	/**
	 * 判断输入框中的数据是否符合格式
	 */
	public boolean verfy(EditText user, EditText pwd) {

		if (user != null && pwd != null) {
			if (user.getText() == null || "".equals(user.getText().toString())) {
				String emptyAccount = AppConfig.getString(this, "user_hintuser_msg");
				showMsg(emptyAccount);
				return true;
			} else if (pwd.getText() == null
					|| "".equals(pwd.getText().toString())) {
				String emptyPassword = AppConfig.getString(this, "user_hintpwd_msg");
				showMsg(emptyPassword);
				return true;
			}
		}
		if (user == null) {
			if (pwd.getText() == null || "".equals(pwd.getText().toString())) {
				String emptyPassword = AppConfig.getString(this, "user_hintpwd_msg");
				showMsg(emptyPassword);
				return true;
			}
		}

		if (pwd == null) {
			if (user.getText() == null || "".equals(user.getText().toString())) {
				String emptyAccount = AppConfig.getString(this, "user_hintuser_msg");
				showMsg(emptyAccount);
				return true;
			}
		}
		return false;
	}

	public void addFragmentToActivity(FragmentManager fragmentManager,
			Fragment fragment, int frameId) {
		Utils.checkNotNull(fragmentManager);
		Utils.checkNotNull(fragment);
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(frameId, fragment);
		transaction.commit();
	}

	public void wrapaLoginInfo(String result, String msg, String userName,
			String openid, String gametoken) {
		Log.i("JiMiSDK", "wrapaLoginInfo result=" + result+",msg=" +msg + ",openid=" + openid + ",gametoken=" + gametoken+",userName="+userName);

		if(TextUtils.isEmpty(openid) || TextUtils.isEmpty(gametoken)){
			Log.w("JiMiSDK", "wrapaLoginInfo openid or gametoken is null");
			return;
		}
		if(result.equals("success")){
			AppConfig.isShow = true;
		}

		JiMiSDK.getStatisticsSDK().onLogin(openid);

		AppConfig.openid=openid;
		LoginMessageinfo login = new LoginMessageinfo();
		login.setMsg(msg);
		login.setResult(result);
		login.setGametoken(gametoken);
		login.setUname(openid);
		login.setOpenid(openid);
		Message mssg = new Message();
		mssg.obj = login;
		mssg.what = 1;
		JiMiSDK.handler.sendMessage(mssg);
	}
	public void toUsetlogin(Registermsg registermsg){
		Intent intent = new Intent(this, JmLoginActivity.class);
		this.startActivity(intent);
		}
	public void turnToIntent(String url) {
		if (TextUtils.isEmpty(url)) {
			// Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
			return;
		}
	
		  Intent intent = new Intent();
		  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		  	Intent.FLAG_ACTIVITY_SINGLE_TOP);
		  intent.putExtra("url", url);
		  intent.setClass(this, JmUserinfoActivity.class);
		  startActivity(intent);
		
	}

	public void turnToNotice(final String url) {
		if (TextUtils.isEmpty(url)) {
			// Toast.makeText(mContext, "此功能暂未开通", Toast.LENGTH_SHORT).show();
			return;
		}

		JiMiSDK.handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("url", url);
				intent.putExtra("notice", true);
				intent.setClass(JiMiSDK.mContext, JmUserinfoActivity.class);
				JiMiSDK.mContext.startActivity(intent);
			}
		}, 1000);
	}

}
